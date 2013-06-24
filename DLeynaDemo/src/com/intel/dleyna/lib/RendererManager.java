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
 * Use {@link #RendererManager(Events)} to to obtain an instance of this class
 * and register for notification of events.
 * <p>
 * Use {@link #connect(Context)} to initiate the connection to the background renderer service.
 * <p>
 * Use {@link #disconnect()} to break the connection and deallocate connection resources.
 * <p>
 * While connected to the background renderer service,
 * you can use {@link #getRenderers()} to obtain a list of all currently available renderers,
 * and you will be notified of the appearance and disappearance of renderers in callbacks to
 * the {@link Events} object that you passed to {@link #RendererManager(Events)}.
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
    private Events listener;

    /** Maps "object path" strings to renderers. */
    private HashMap<String,Renderer> renderers = new HashMap<String,Renderer>();

    /** The handler we use to run notifications on the main thread. */
    private Handler handler;

    /**
     * You would typically invoke this constructor from {@link Activity#onStart()}.
     * @param events an instance of your extension of {@link Events},
     * for receiving notification of events.
     */
    public RendererManager(Events events) {
        handler = new Handler(Looper.getMainLooper());
        listener = events;
    }

    /**
     * Initiate a connection to the background renderer service.
     * <p>
     * Connection establishment is asynchronous --
     * if this method succeeds, you will later receive a callback to
     * the {@link Events#onConnected()} method of the Events object you passed to
     * {@link #RendererManager(Events)}, or to {@link Events#onDisconnected()}
     * if something went wrong in the meantime.
     * <p>
     * This method could fail if, for example, the application package containing
     * the renderer service hasn't been installed on the device.
     * <p>
     * You would typically call this method from {@link Activity#onStart()}.
     * @param context the context in which this is being called, typically an {@link Activity}.
     * @return true on success, false on failure
     */
    public boolean connect(Context context) {
        if (LOG) Log.i(TAG, "connect");
        if (!serviceBound) {
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
                // For some crazy reason you have to have to unbind a service even if
                // the bind failed, or you'll get a "leaked ServiceConnection" warning.
                context.unbindService(rendererConnection);
            }
        }
        return serviceBound;
    }

    /**
     * Disconnect from the background renderer service.
     * <p>
     * You would typically call this method from {@link Activity#onDestroy()}.
     */
    public void disconnect() {
        if (LOG) Log.i(TAG, "disconnect");
        if (serviceConnected) {
            try {
                rendererService.unregisterClient(rendererCallback);
            } catch (RemoteException e) {
                if (LOG) Log.e(TAG, "disconnect: unregisterClient: " + e);
            }
        }
        if (serviceBound) {
            context.unbindService(rendererConnection);
            serviceBound = false;
        }
    }

    private final ServiceConnection rendererConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder b) {
            if (LOG) Log.i(TAG, "onServiceConnected");
            serviceConnected = true;
            rendererService = IRendererService.Stub.asInterface(b);
            try {
                rendererService.registerClient(rendererCallback);
                listener.onConnected();
            } catch (RemoteException e) {
                // I'm supposing that if we get here, then onServiceDisconnected()
                // will still be called, so do nothing special.
                e.printStackTrace();
            }
        }

        public void onServiceDisconnected(ComponentName arg0) {
            if (LOG) Log.i(TAG, "onServiceDisconnected");
            serviceConnected = false;
            listener.onDisconnected();
        }
    };

    /**
     * Get all known renderers.
     * @return all currently known renderers
     * @throws RemoteException no connection to the background renderer service
     */
    public Renderer[] getRenderers() throws RemoteException {
        // TODO: fetch them from the service, update our local copy.
        return (Renderer[]) renderers.values().toArray();
    }

    /**
     * Cause a GuPNP rescan to be performed.
     * <p>
     * Renderers that haven't responded after a few seconds will be considered unavailable.
     * <p>
     * This may result in callbacks to {@link Events#onRendererFound(Renderer)} and/or
     * {@link Events#onRendererLost(Renderer)} on registered observers.
     * @throws RemoteException no connection to the background renderer service
     */
    public void rescan() throws RemoteException {
        // TODO
    }

    /**
     * Get the version number of this implementation of dLeyna-renderer.
     * @return version number
     * @throws RemoteException no connection to the background renderer service
     */
    public String getVersion() throws RemoteException {
        // TODO
        return null;
    }

    /**
     * Callbacks from the Renderer service via Binder are handled here.
     * The callbacks run on some arbitrary Binder thread.
     * We forward the calls to the main thread by using {@link #handler}.
     */
    private final IRendererCallback rendererCallback = new IRendererCallback.Stub() {

        /*------------------------+
         | RendererManager.Events |
         +------------------------*/

        public void onRendererFound(final String objectPath) {
            if (LOG) Log.i(TAG, "onRendererFound: " + objectPath);
            handler.post(new Runnable() {
                public void run() {
                    if (renderers.containsKey(objectPath)) {
                        if (LOG) Log.e(TAG, "onRendererFound: REDUNDANT: " + objectPath);
                    } else {
                        Renderer r = new Renderer(objectPath);
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

        /*---------------------------+
         | IRendererControllerEvents |
         +---------------------------*/

        public void onPlaybackStatusChanged(final String objectPath, final String status) {
            if (LOG) Log.i(TAG, "onPlaybackStatusChanged: " + objectPath + " " + status);
            handler.post(new Runnable() {
                public void run() {
                    Renderer r = renderers.get(objectPath);
                    if (r == null) {
                        if (LOG) Log.e(TAG, "onPlaybackStatusChanged: MISSING: " + objectPath);
                    } else {
                        List<IRendererControllerEvents> listeners = r.getControllerListeners();
                        for (IRendererControllerEvents l : listeners) {
                            l.onPlaybackStatusChanged(r, status);
                        }
                    }
                }
            });
        }

        public void onRateChanged(final String objectPath, final double rate) {
            if (LOG) Log.i(TAG, "onRateChanged: " + objectPath + " " + rate);
            handler.post(new Runnable() {
                public void run() {
                    Renderer r = renderers.get(objectPath);
                    if (r == null) {
                        if (LOG) Log.e(TAG, "onRateChanged: MISSING: " + objectPath);
                    } else {
                        List<IRendererControllerEvents> listeners = r.getControllerListeners();
                        for (IRendererControllerEvents l : listeners) {
                            l.onRateChanged(r, rate);
                        }
                    }
                }
            });
        }

        public void onMetadataChanged(final String objectPath, final Bundle metadata) {
            if (LOG) Log.i(TAG, "onMetadataChanged: " + objectPath + " " + metadata);
            handler.post(new Runnable() {
                public void run() {
                    Renderer r = renderers.get(objectPath);
                    if (r == null) {
                        if (LOG) Log.e(TAG, "onMetadataChanged: MISSING: " + objectPath);
                    } else {
                        List<IRendererControllerEvents> listeners = r.getControllerListeners();
                        for (IRendererControllerEvents l : listeners) {
                            l.onMetadataChanged(r, metadata);
                        }
                    }
                }
            });
        }

        public void onVolumeChanged(final String objectPath, final double volume) {
            if (LOG) Log.i(TAG, "onVolumeChanged: " + objectPath + " " + volume);
            handler.post(new Runnable() {
                public void run() {
                    Renderer r = renderers.get(objectPath);
                    if (r == null) {
                        if (LOG) Log.e(TAG, "onVolumeChanged: MISSING: " + objectPath);
                    } else {
                        List<IRendererControllerEvents> listeners = r.getControllerListeners();
                        for (IRendererControllerEvents l : listeners) {
                            l.onVolumeChanged(r, volume);
                        }
                    }
                }
            });
        }

        public void onMinimumRateChanged(final String objectPath, final long rate) {
            if (LOG) Log.i(TAG, "onMinimumRateChanged: " + objectPath + " " + rate);
            handler.post(new Runnable() {
                public void run() {
                    Renderer r = renderers.get(objectPath);
                    if (r == null) {
                        if (LOG) Log.e(TAG, "onMinimumRateChanged: MISSING: " + objectPath);
                    } else {
                        List<IRendererControllerEvents> listeners = r.getControllerListeners();
                        for (IRendererControllerEvents l : listeners) {
                            l.onMinimumRateChanged(r, rate);
                        }
                    }
                }
            });
        }

        public void onMaximumRateChanged(final String objectPath, final long rate) {
            if (LOG) Log.i(TAG, "onMaximumRateChanged: " + objectPath + " " + rate);
            handler.post(new Runnable() {
                public void run() {
                    Renderer r = renderers.get(objectPath);
                    if (r == null) {
                        if (LOG) Log.e(TAG, "onMaximumRateChanged: MISSING: " + objectPath);
                    } else {
                        List<IRendererControllerEvents> listeners = r.getControllerListeners();
                        for (IRendererControllerEvents l : listeners) {
                            l.onMaximumRateChanged(r, rate);
                        }
                    }
                }
            });
        }

        public void onCanGoNextChanged(final String objectPath, final boolean value) {
            if (LOG) Log.i(TAG, "onCanGoNextChanged: " + objectPath + " " + value);
            handler.post(new Runnable() {
                public void run() {
                    Renderer r = renderers.get(objectPath);
                    if (r == null) {
                        if (LOG) Log.e(TAG, "onCanGoNextChanged: MISSING: " + objectPath);
                    } else {
                        List<IRendererControllerEvents> listeners = r.getControllerListeners();
                        for (IRendererControllerEvents l : listeners) {
                            l.onCanGoNextChanged(r, value);
                        }
                    }
                }
            });
        }

        public void onCanGoPreviousChanged(final String objectPath, final boolean value) {
            if (LOG) Log.i(TAG, "onCanGoPreviousChanged: " + objectPath + " " + value);
            handler.post(new Runnable() {
                public void run() {
                    Renderer r = renderers.get(objectPath);
                    if (r == null) {
                        if (LOG) Log.e(TAG, "onCanGoPreviousChanged: MISSING: " + objectPath);
                    } else {
                        List<IRendererControllerEvents> listeners = r.getControllerListeners();
                        for (IRendererControllerEvents l : listeners) {
                            l.onCanGoPreviousChanged(r, value);
                        }
                    }
                }
            });
        }

        public void onNumberOfTracksChanged(final String objectPath, final int n) {
            if (LOG) Log.i(TAG, "onNumberOfTracksChanged: " + objectPath + " " + n);
            handler.post(new Runnable() {
                public void run() {
                    Renderer r = renderers.get(objectPath);
                    if (r == null) {
                        if (LOG) Log.e(TAG, "onNumberOfTracksChanged: MISSING: " + objectPath);
                    } else {
                        List<IRendererControllerEvents> listeners = r.getControllerListeners();
                        for (IRendererControllerEvents l : listeners) {
                            l.onNumberOfTracksChanged(r, n);
                        }
                    }
                }
            });
        }

        public void onTrackChanged(final String objectPath, final int track) {
            if (LOG) Log.i(TAG, "onTrackChanged: " + objectPath + " " + track);
            handler.post(new Runnable() {
                public void run() {
                    Renderer r = renderers.get(objectPath);
                    if (r == null) {
                        if (LOG) Log.e(TAG, "onTrackChanged: MISSING: " + objectPath);
                    } else {
                        List<IRendererControllerEvents> listeners = r.getControllerListeners();
                        for (IRendererControllerEvents l : listeners) {
                            l.onTrackChanged(r, track);
                        }
                    }
                }
            });
        }

        public void onTransportPlaySpeedsChanged(final String objectPath, final double[] speeds) {
            if (LOG) Log.i(TAG, "onTransportPlaySpeedsChanged: " + objectPath + " " + speeds);
            handler.post(new Runnable() {
                public void run() {
                    Renderer r = renderers.get(objectPath);
                    if (r == null) {
                        if (LOG) Log.e(TAG, "onTransportPlaySpeedsChanged: MISSING: " + objectPath);
                    } else {
                        List<IRendererControllerEvents> listeners = r.getControllerListeners();
                        for (IRendererControllerEvents l : listeners) {
                            l.onTransportPlaySpeedsChanged(r, speeds);
                        }
                    }
                }
            });
        }

        public void onMuteChanged(final String objectPath, final boolean value) {
            if (LOG) Log.i(TAG, "onMuteChanged: " + objectPath + " " + value);
            handler.post(new Runnable() {
                public void run() {
                    Renderer r = renderers.get(objectPath);
                    if (r == null) {
                        if (LOG) Log.e(TAG, "onMetadataChanged: MISSING: " + objectPath);
                    } else {
                        List<IRendererControllerEvents> listeners = r.getControllerListeners();
                        for (IRendererControllerEvents l : listeners) {
                            l.onMuteChanged(r, value);
                        }
                    }
                }
            });
        }
     };

    /**
     * Asynchronous Renderer Manager events.
     */
    public static class Events {
        /**
         * Override this to be notified when the connection to the background renderer service
         * has been established.
         * <p>
         * This will run on the application's main thread.
         */
        public void onConnected() {
        }

        /**
         * Override this to be notified when the connection to the background renderer service
         * has been broken.
         * <p>
         * This will run on the application's main thread.
         */
        public void onDisconnected() {
        }

        /**
         * Override this to be notified whenever a renderer appears on the network.
         * @param r the renderer that has appeared
         * <p>
         * This will run on the application's main thread.
         */
        public void onRendererFound(Renderer r) {
        }

        /**
         * Override this to be notified whenever a renderer disappears from the network.
         * @param r the renderer that has disappeared
         * <p>
         * This will run on the application's main thread.
         */
        public void onRendererLost(Renderer r) {
        }
    }
}
