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

/**
 * Notification of Renderer Manager events.
 * <p>
 * Pass an instance of your extension of this class to
 * {@link RendererManager#RendererManager(RendererManagerListener)}.
 */
public class RendererManagerListener {

    /**
     * Construct an object for receiving Renderer Manager events.
     */
    public RendererManagerListener() {
    }

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
