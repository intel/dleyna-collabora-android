/*
 * dLeyna
 *
 * Copyright (C) 2013-2017 Intel Corporation. All rights reserved.
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
 */

package com.intel.dleyna.lib;

/**
 * Notification of Server Manager events.
 * <p>
 * Pass an instance of your extension of this class to
 * {@link ServerManager#ServerManager(ServerManagerListener)}.
 */
public class ServerManagerListener {

    /**
     * Construct an object for receiving Server Manager events.
     */
    public ServerManagerListener() {
    }

    /**
     * Override this to be notified when the connection to the background server service
     * has been established.
     * <p>
     * This will run on the application's main thread.
     */
    public void onConnected() {
    }

    /**
     * Override this to be notified when the connection to the background server service
     * has been broken.
     * <p>
     * This will run on the application's main thread.
     */
    public void onDisconnected() {
    }

    /**
     * Override this to be notified whenever a server appears on the network.
     * @param s the server that has appeared
     * <p>
     * This will run on the application's main thread.
     */
    public void onServerFound(Server s) {
    }

    /**
     * Override this to be notified whenever a server disappears from the network.
     * @param s the server that has disappeared
     * <p>
     * This will run on the application's main thread.
     */
    public void onServerLost(Server s) {
    }

    /**
     * Override this to be notified whenever a server changes on the network.
     * @param s the server that has changed
     * @param svObjects the changes
     * <p>
     * This will run on the application's main thread.
     */
    public void onLastChange(Server s, String[] svObjects) {  //TODO: fix parameter
    }
}
