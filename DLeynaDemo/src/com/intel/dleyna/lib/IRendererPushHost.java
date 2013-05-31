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

import android.os.RemoteException;

/**
 * The PushHost portion of the Renderer API.
 */
public interface IRendererPushHost {

    /**
     * Host a file on a web server(s) on the local device.
     * <p>
     * The background renderer service will create a web server for every network interface
     * through which remote renderers need access to files created by this method.
     * <p>
     * When the number of hosted files accessible through a given interface drops to zero,
     * the web server for that interface will be shut down.
     * @param path the full pathname of the file to be hosted
     * @return the URL of the newly hosted file
     * @throws RemoteException no connection to the background renderer service
     */
    public String hostFile(String path) throws RemoteException;

    /**
     * Stop hosting the specified file.
     * @param path the full pathname of the file to be hosted
     * @throws RemoteException no connection to the background renderer service
     */
    public void removeFile(String path) throws RemoteException;
}