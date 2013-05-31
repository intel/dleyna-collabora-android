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

import android.app.Activity;
import android.os.RemoteException;

/**
 * This class allows applications to discover Digital Media Renderers
 * on local networks attached to this device (or on the device itself).
 * Each such DMR is represented by an instance of {@link Renderer}.
 * <p>
 * Use {@link #getInstance(Events)} to to obtain the singleton instance of this class.
 * <p>
 * Use {@link #connect()} to initiate a connection to the background renderer service,
 * and {@link #disconnect()} to break the connection and deallocate connection resources.
 * <p>
 * While connected to the background renderer service,
 * you can use {@link #getRenderers()} to obtain a list of all currently available renderers,
 * and you will be notified of the appearance and disappearance of renderers in callbacks to
 * the {@link Events} object that you passed to {@link #getInstance(Events)}.
 */
public class RendererManager {

    /** The singleton instance */
    private static RendererManager instance;

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
        instance.addObserver(events);
        return instance;
    }

    private void addObserver(Events events) {
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
     * @return true on success, false on failure
     */
    public boolean connect() {
        return true;
    }

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
    public class Events {
        /**
         * Override this to be notified when the connection to the background renderer service
         * has been established.
         */
        public void onConnected() {
        }

        /**
         * Override this to be notified when the connection to the background renderer service
         * has been broken.
         */
        public void onDisconnected() {
        }

        /**
         * Override this to be notified whenever a renderer appears on the network.
         * @param r the renderer that has appeared
         */
        public void onRendererFound(Renderer r) {
        }

        /**
         * Override this to be notified whenever a renderer disappears from the network.
         * @param r the renderer that has disappeared
         */
        public void onRendererLost(Renderer r) {
        }
    }
}
