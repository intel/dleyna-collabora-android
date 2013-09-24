/*
 * dLeyna
 *
 * Copyright (C) 2013 Intel Corporation. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms and conditions of the GNU Lesser General Public License,
 * version 2.1, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St - Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Tom Keel <thomas.keel@intel.com>
 */

package com.intel.dleyna.lib;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

/**
 * This class enables applications to discover Digital Media Renderers
 * on local networks attached to this device (or on the device itself).
 * Each such DMR is represented by an instance of {@link Renderer}.
 * <p>
 * Use {@link #RendererManager(RendererManagerListener)} to to obtain an instance of this class
 * and register for notification of events.
 * <p>
 * Use {@link #connect(Context)} to initiate the connection to the background renderer service.
 * <p>
 * Use {@link #disconnect()} to break the connection and deallocate connection resources.
 * <p>
 * While connected to the background renderer service,
 * you can use {@link #getRenderers()} to obtain a list of all currently available renderers,
 * and you will be notified of the appearance and disappearance of renderers in callbacks to
 * the {@link RendererManagerListener} object that you passed to
 * {@link #RendererManager(RendererManagerListener)}.
 * Those notifications will run on the application's main thread.
 */
public class RendererManager {

    private static final boolean LOG = true;
    private static final String TAG = "RendererMgr";

    /** The application package containing the Renderer service. */
    private static final String RENDERER_SERVICE_PACKAGE = "com.intel.dleyna.dleynademo";

    /** The class implementing the Renderer service. */
    private static final String RENDERER_SERVICE_CLASS = "com.intel.dleyna.RendererService";

    /** The Binder interface to the Renderer service. */
    private IRendererService rendererService;

    /** The context from which {@link #connect(Context)} was called. */
    private Context context;

    /** Do we have a binder to the service? */
    private boolean serviceBound;

    /** Are we connected to the service? */
    private boolean serviceConnected;

    /** The event listener. */
    private RendererManagerListener listener;

    /** Maps "object path" strings to renderers. */
    private HashMap<String,Renderer> renderers = new HashMap<String,Renderer>();

    /** The handler we use to run notifications on the main thread. */
    private Handler handler;

    /**
     * Construct a manager instance, and register for notification of events.
     * Note that you will only receive events after invoking {@link #connect(Context)}.
     * @param listener an instance of your extension of {@link RendererManagerListener},
     * for receiving notification of events.
     */
    public RendererManager(RendererManagerListener listener) {
        handler = new Handler(Looper.getMainLooper());
        this.listener = listener;
    }

    /**
     * Initiate a connection to the background renderer service.
     * <p>
     * Connection establishment is asynchronous --
     * if this method succeeds, you will later receive a callback to the
     * {@link RendererManagerListener#onConnected()} method of the {@link RendererManagerListener}
     * object you passed to{@link #RendererManager(RendererManagerListener)}, or to
     * {@link RendererManagerListener#onDisconnected()}
     * if something went wrong in the meantime.
     * <p>
     * This method could fail if, for example, the application package containing
     * the renderer service hasn't been installed on the device.
     * <p>
     * You would typically call this method from {@link Activity#onResume()}.
     * @param context the context in which this is being called, typically an {@link Activity}.
     * @return true on success, false on failure
     */
    public boolean connect(Context context) {
        if (LOG) Log.i(TAG, "connect: bound=" + serviceBound + " connected=" + serviceConnected);
        if (!serviceBound) {
            if (LOG) Log.i(TAG, "connect: binding");
            this.context = context;
            Intent intent = new Intent();
            intent.setClassName(RENDERER_SERVICE_PACKAGE, RENDERER_SERVICE_CLASS);
            try {
                serviceBound = context.bindService(intent, rendererConnection, Context.BIND_AUTO_CREATE);
            } catch (SecurityException e) {
                if (LOG) Log.e(TAG, "connect: " + e);
            }
            if (!serviceBound) {
                if (LOG) Log.e(TAG, "connect: can't bind to renderer service");
                // For some crazy reason you have to unbind a service even if
                // the bind failed, or you'll get a "leaked ServiceConnection" warning.
                context.unbindService(rendererConnection);
            }
            // Note: even though unbinding the service does not necessarily result in an
            // onServiceDisconnected() callback, i.e. even though we could be here with
            // serviceConnected == true, we *will* get another onServiceConnected() callback.
        }
        return serviceBound;
    }

    /**
     * Disconnect from the background renderer service.
     * <p>
     * You would typically call this method from {@link Activity#onPause()}.
     */
    public void disconnect() {
        if (LOG) Log.i(TAG, "disconnect: bound=" + serviceBound + " connected=" + serviceConnected);
        if (serviceConnected) {
            if (LOG) Log.i(TAG, "disconnect: unregistering client");
            try {
                rendererService.unregisterClient(rendererClient);
            } catch (RemoteException e) {
                if (LOG) Log.e(TAG, "disconnect: unregisterClient: " + e);
            }
            serviceConnected = false;
        }
        if (serviceBound) {
            context.unbindService(rendererConnection);
            // We should be on the main thread, but even so, we don't want recursion.
            handler.post(new Runnable() {
                public void run() {
                    listener.onDisconnected();
                }
            });
            serviceBound = false;
        }
        if (LOG) Log.i(TAG, "disconnect: DONE");
    }

    private final ServiceConnection rendererConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder b) {
            if (LOG) Log.i(TAG, "onServiceConnected: bound=" + serviceBound + " connected=" + serviceConnected);
            // Note: the service could have been re-bound and re-connected automatically
            // following a service process crash, in which case serviceBound would be false
            // here. So set it true.
            serviceBound = true;
            serviceConnected = true;
            rendererService = IRendererService.Stub.asInterface(b);
            try {
                rendererService.registerClient(rendererClient);
                listener.onConnected();
            } catch (RemoteException e) {
                // I'm supposing that if we get here, then onServiceDisconnected()
                // will still be called, so do nothing special.
                e.printStackTrace();
            }
        }

        public void onServiceDisconnected(ComponentName arg0) {
            if (LOG) Log.i(TAG, "onServiceDisconnected: bound=" + serviceBound + " connected=" + serviceConnected);
            serviceConnected = false;
            if (serviceBound) {
                listener.onDisconnected();
                serviceBound = false;
            }
        }
    };

    /**
     * Get all known renderers.
     * <p>
     * Note: if there are no known renderers, this returns an array of length 0.
     * @return all currently known renderers
     * @throws RemoteException no connection to the background renderer service
     * @throws DLeynaException failure reported by the background renderer service
     */
    public Renderer[] getRenderers() throws RemoteException, DLeynaException {
        if (!serviceConnected) {
            throw new RemoteException();
        }

        // In what follows we construct a new map representing the new current set
        // of renderers, reusing the Renderer objects of any renderers that were already
        // in the previous map. Then we substitute the new map for the previous one.

        Bundle extras = new Bundle();
        String[] newObjectIds = rendererService.getRenderers(rendererClient, extras);
        Extras.throwExceptionIfError(extras);
        // newObjectIds is the new complete set of object ids according to the service
        if (LOG) Log.i(TAG, "getRenderers: " + newObjectIds);

        if (newObjectIds != null && newObjectIds.length > 0) {
            for (Renderer r : renderers.values()) {
                r.setIsObsolete(true);
            }
            // the 'obsolete' flag is set for all elements of the previous map
            List<Renderer> deltaRenderers = new LinkedList<Renderer>();
            for (String objectPath : newObjectIds) {
                Renderer r = renderers.get(objectPath);
                if (r == null) {
                    r = new Renderer(RendererManager.this, objectPath);
                    deltaRenderers.add(r);
                } else {
                    r.setIsObsolete(false);
                }
            }
            // deltaRenderers is the set of renderers that appeared since the previous set was built
            // the 'obsolete' flag is set only for the truly obsolete elements of the previous map
            HashMap<String, Renderer> nextRenderers = new HashMap<String, Renderer>();
            for (Renderer r : renderers.values()) {
                if (!r.getIsObsolete()) {
                    nextRenderers.put(r.getObjectPath(), r);
                }
            }
            for (Renderer r : deltaRenderers) {
                nextRenderers.put(r.getObjectPath(), r);
            }
            // nextRenderers is the new current map of renderers.
            renderers = nextRenderers;
        }
        return renderers.values().toArray(new Renderer[renderers.size()]);
    }

    /**
     * Cause a GuPNP rescan to be performed.
     * <p>
     * Renderers that haven't responded after a few seconds will be considered unavailable.
     * <p>
     * This may result in callbacks to {@link RendererManagerListener#onRendererFound(Renderer)}
     * and/or {@link RendererManagerListener#onRendererLost(Renderer)} on registered observers.
     * @throws RemoteException no connection to the background renderer service
     * @throws DLeynaException failure reported by the background renderer service
     */
    public void rescan() throws RemoteException, DLeynaException {
        if (!serviceConnected) {
            throw new RemoteException();
        }
        Bundle extras = new Bundle();
        rendererService.rescan(rendererClient, extras);
        Extras.throwExceptionIfError(extras);
    }

    /**
     * Get the version number of this implementation of dLeyna-renderer.
     * @return version number
     * @throws RemoteException no connection to the background renderer service
     * @throws DLeynaException failure reported by the background renderer service
     */
    public String getVersion() throws RemoteException, DLeynaException {
        // TODO
        return null;
    }

    /**
     * This is the Binder that we pass to the RendererService.
     * It serves both to identify us as a particular client of the RendererService, and
     * to receive callbacks from the RendererService.
     * The callbacks run on some arbitrary Binder thread.
     * We forward the callbacks to the main thread by using {@link #handler}.
     */
    private final IRendererClient rendererClient = new IRendererClient.Stub() {

        /*-------------------------+
         | RendererManagerListener |
         +-------------------------*/

        public void onRendererFound(final String objectPath) {
            if (LOG) Log.i(TAG, "onRendererFound: " + objectPath);
            handler.post(new Runnable() {
                public void run() {
                    if (renderers.containsKey(objectPath)) {
                        if (LOG) Log.e(TAG, "onRendererFound: REDUNDANT: " + objectPath);
                    } else {
                        Renderer r = new Renderer(RendererManager.this, objectPath);
                        renderers.put(objectPath, r);
                        listener.onRendererFound(r);
                    }
                }
            });
        }

        public void onRendererLost(final String objectPath) {
            if (LOG) Log.i(TAG, "onRendererLost: " + objectPath);
            handler.post(new Runnable() {
                public void run() {
                    Renderer r = renderers.get(objectPath);
                    if (r == null) {
                        if (LOG) Log.e(TAG, "onRendererLost: MISSING: " + objectPath);
                    } else {
                        listener.onRendererLost(r);
                        renderers.remove(objectPath);
                    }
                }
            });
        }

        /*-----------------------------+
         | IRendererControllerListener |
         +-----------------------------*/

        public void onControllerPropertiesChanged(String objectPath, final Bundle props) {
            if (LOG) logControllerPropertiesChanged(objectPath, props);
            final Renderer r = renderers.get(objectPath);
            if (r == null) {
                if (LOG) Log.e(TAG, "CtlrPropChange: MISSING: " + objectPath);
                return;
            }

            handler.post(new Runnable() {
                public void run() {
                    for (IRendererControllerListener l : r.getControllerListeners()) {
                        for (Iterator<String> it = props.keySet().iterator(); it.hasNext(); ) {
                            String propName = it.next();
                            RendererControllerProps.Enum e = RendererControllerProps.getEnum(propName);
                            switch (e) {
                            case PLAYBACK_STATUS:
                                l.onPlaybackStatusChanged(r, props.getString(propName));
                                break;
                            case CAN_GO_NEXT:
                                l.onCanGoNextChanged(r, props.getBoolean(propName));
                                break;
                            case CAN_GO_PREVIOUS:
                                l.onCanGoPreviousChanged(r, props.getBoolean(propName));
                                break;
                            case CAN_PLAY:
                                l.onCanPlayChanged(r, props.getBoolean(propName));
                                break;
                            case CAN_PAUSE:
                                l.onCanPauseChanged(r, props.getBoolean(propName));
                                break;
                            case CAN_SEEK:
                                l.onCanSeekChanged(r, props.getBoolean(propName));
                                break;
                            case CAN_CONTROL:
                                l.onCanControlChanged(r, props.getBoolean(propName));
                                break;
                            case MUTE:
                                l.onMuteChanged(r, props.getBoolean(propName));
                                break;
                            case RATE:
                                l.onRateChanged(r, props.getDouble(propName));
                                break;
                            case VOLUME:
                                l.onVolumeChanged(r, props.getDouble(propName));
                                break;
                            case MINIMUM_RATE:
                                l.onMinimumRateChanged(r, props.getDouble(propName));
                                break;
                            case MAXIMUM_RATE:
                                l.onMaximumRateChanged(r, props.getDouble(propName));
                                break;
                            case POSITION:
                                l.onPositionChanged(r, props.getLong(propName));
                                break;
                            case METADATA:
                                // TODO
                                break;
                            case TRANSPORT_PLAY_SPEEDS:
                                l.onTransportPlaySpeedsChanged(r, props.getDoubleArray(propName));
                                break;
                            case CURRENT_TRACK:
                                l.onCurrentTrackChanged(r, props.getInt(propName));
                                break;
                            case NUMBER_OF_TRACKS:
                                l.onNumberOfTracksChanged(r, props.getInt(propName));
                                break;
                            case UNKNOWN:
                                Log.e(TAG, "Unknown renderer controller property: " + propName);
                                break;
                            default:
                                Log.e(TAG, "Unhandled renderer controller property: " + propName);
                                break;
                            }
                        }
                    }
                }
            });
        }

        private void logControllerPropertiesChanged(String objectPath, Bundle props) {
            Log.i(TAG, "CtlrPropChange: " + objectPath);
            Iterator<String> it = props.keySet().iterator();
            while (it.hasNext()) {
                String propName = it.next();
                RendererControllerProps.Enum e = RendererControllerProps.getEnum(propName);
                switch (e) {
                case PLAYBACK_STATUS:
                    String s = props.getString(propName);
                    if (LOG) Log.i(TAG, "CtlrPropChange: " + propName + ": " + s);
                    break;
                case CAN_GO_NEXT:
                case CAN_GO_PREVIOUS:
                case CAN_PLAY:
                case CAN_PAUSE:
                case CAN_SEEK:
                case CAN_CONTROL:
                case MUTE:
                    boolean b = props.getBoolean(propName);
                    if (LOG) Log.i(TAG, "CtlrPropChange: " + propName + ": " + b);
                    break;
                case RATE:
                case VOLUME:
                case MINIMUM_RATE:
                case MAXIMUM_RATE:
                    double d = props.getDouble(propName);
                    if (LOG) Log.i(TAG, "CtlrPropChange: " + propName + ": " + d);
                    break;
                case POSITION:
                    long l = props.getLong(propName);
                    if (LOG) Log.i(TAG, "CtlrPropChange: " + propName + ": " + l);
                    break;
                case METADATA:
                    // TODO
                    if (LOG) Log.i(TAG, "CtlrPropChange: " + propName + ": " + "?");
                    break;
                case TRANSPORT_PLAY_SPEEDS:
                    double[] ad = props.getDoubleArray(propName);
                    if (LOG) Log.i(TAG, "CtlrPropChange: " + propName + ": " + ad);
                    break;
                case CURRENT_TRACK:
                case NUMBER_OF_TRACKS:
                    int i = props.getInt(propName);
                    if (LOG) Log.i(TAG, "CtlrPropChange: " + propName + ": " + i);
                    break;
                case UNKNOWN:
                    Log.e(TAG, "Unknown renderer controller property: " + propName);
                    break;
                default:
                    Log.e(TAG, "Unhandled renderer controller property: " + propName);
                    break;
                }
            }
        }
     };

     IRendererService getRendererService() {
         return rendererService;
     }

     IRendererClient getRendererClient() {
         return rendererClient;
     }
}
