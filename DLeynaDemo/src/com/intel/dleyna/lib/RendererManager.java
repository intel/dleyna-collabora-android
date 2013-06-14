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

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.intel.dleyna.IRendererCallback;
import com.intel.dleyna.IRendererService;
import com.intel.dleyna.dleynademo.App;

/**
 * This class allows applications to discover Digital Media Renderers
 * on local networks attached to this device (or on the device itself).
 * Each such DMR is represented by an instance of {@link Renderer}.
 * <p>
 * Use {@link #getInstance(Events)} to to obtain the singleton instance of this class.
 * <p>
 * Use {@link #connect(Context)} to initiate a connection to the background renderer service,
 * typically from the onStart() method of an Activity.
 * <p>
 * Use {@link #disconnect()} to break the connection and deallocate connection resources,
 * typically from the onDestroy() method of an Activity.
 * <p>
 * While connected to the background renderer service,
 * you can use {@link #getRenderers()} to obtain a list of all currently available renderers.
 * <p>
 * You will be notified of the appearance and disappearance of renderers in callbacks to
 * the {@link Events} object that you passed to {@link #getInstance(Events)}.
 * These notifications will be run on the main thread of your application.
 */
public class RendererManager {

    private static final String TAG = "RendererMgr";

    /** The singleton instance */
    private static RendererManager instance;

    /** The application package containing the Renderer service. */
    private static final String RENDERER_SERVICE_PACKAGE = "com.intel.dleyna.dleynademo";

    /** The class implementing the Renderer service. */
    private static final String RENDERER_SERVICE_CLASS = "com.intel.dleyna.RendererService";

    /** The Binder interface to the Renderer service. */
    private IRendererService rendererService;

    private boolean rendererServiceBound;

    private List<Events> listeners = new LinkedList<Events>();

    /** The hidden constructor */
    private RendererManager() {
    }

    /**
     * Get the unique renderer manager instance, creating it if necessary.
     * @param events an instance of your extension of {@link Events},
     * for receiving notification of events.
     * @return the singleton instance
     */
    public static RendererManager getInstance(Events events) {
        if (instance == null) {
            instance = new RendererManager();
        }
        instance.addListener(events);
        return instance;
    }

    private void addListener(Events events) {
        listeners.add(events);
    }

    /**
     * Initiate a connection to the background renderer service.
     * <p>
     * Connection establishment is asynchronous --
     * if this method succeeds, you will later receive a callback to
     * the {@link Events#onConnected()} method of the Events object you passed to
     * {@link #getInstance(Events)} or to {@link Events#onDisconnected()}
     * if something went wrong in the meantime.
     * <p>
     * This method could fail if, for example, the application package containing
     * the renderer service hasn't been installed on the device.
     * <p>
     * You would typically call this method from {@link Activity#onStart()},
     * on the application's main thread. The thread this is called on will be the one
     * used to deliver all event notifications from this {@link RendererManager}
     * and from all {@link Renderer}s discovered by it.
     * @param context the context in which this is being called, typically an {@link Activity}.
     * @return true on success, false on failure
     */
    public boolean connect(Context context) {
        Intent intent = new Intent();
        intent.setClassName(RENDERER_SERVICE_PACKAGE, RENDERER_SERVICE_CLASS);
        try {
            rendererServiceBound = context.bindService(intent, RendererConnection, Context.BIND_AUTO_CREATE);
        } catch (SecurityException e) {
        }
        if (!rendererServiceBound) {
            Log.w(TAG, "connect: can't bind to renderer service");
            // For some crazy reason you have to have to unbind a service even if
            // the bind failed, or you'll get a "leaked ServiceConnection" warning.
            context.unbindService(RendererConnection);
        }
        return rendererServiceBound;
    }

    private final ServiceConnection RendererConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder b) {
            if (App.LOG) Log.i(TAG, "onServiceConnected");
            rendererService = IRendererService.Stub.asInterface(b);
            try {
                rendererService.registerCallback(rendererCallback);
                for (Events events : listeners) {
                    events.onConnected();
                }
            } catch (RemoteException e) {
                // I'm supposing that if we get here, then onServiceDisconnected()
                // will still be called, so do nothing special.
                e.printStackTrace();
            }
        }

        public void onServiceDisconnected(ComponentName arg0) {
            if (App.LOG) Log.i(TAG, "onServiceDisconnected");
            for (Events events : listeners) {
                events.onDisconnected();
            }
        }
    };

    /**
     * Callbacks from the Renderer service via Binder are handled here.
     */
    private final IRendererCallback rendererCallback =
            new IRendererCallback.Stub() {
    };

    /**
     * Disconnect from the background renderer service.
     * <p>
     * You would typically call this method from {@link Activity#onDestroy()}.
     */
    public void disconnect() {
    }

    /**
     * Get all known renderers.
     * @return all currently known renderers
     * @throws RemoteException no connection to the background renderer service
     */
    public Renderer[] getRenderers() throws RemoteException {
        return null;
    }

    /**
     * Cause a GuPNP rescan to be performed.
     * <p>
     * Renderers that haven't responded after a few seconds will be considered unavailable.
     * <p>
     * This may result in callbacks to {@link Events#onRendererFound(Renderer)} and/or
     * {@link Events#onRendererLost(Renderer)} on registered observers.
     */
    public void rescan() {
    }

    /**
     * Get the version number of this implementation of dLeyna-renderer.
     * @return version number
     * @throws RemoteException no connection to the background renderer service
     */
    public String getVersion() throws RemoteException {
        return null;
    }

    /**
     * Asynchronous Renderer Manager events.
     */
    public static class Events {
        /**
         * Override this to be notified when the connection to the background renderer service
         * has been established.
         * <p>
         * This will be run on the application's main thread.
         */
        public void onConnected() {
        }

        /**
         * Override this to be notified when the connection to the background renderer service
         * has been broken.
         * <p>
         * This will be run on the application's main thread.
         */
        public void onDisconnected() {
        }

        /**
         * Override this to be notified whenever a renderer appears on the network.
         * @param r the renderer that has appeared
         * <p>
         * This will be run on the application's main thread.
         */
        public void onRendererFound(Renderer r) {
        }

        /**
         * Override this to be notified whenever a renderer disappears from the network.
         * @param r the renderer that has disappeared
         * <p>
         * This will be run on the application's main thread.
         */
        public void onRendererLost(Renderer r) {
        }
    }
}
