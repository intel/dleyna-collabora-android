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
 */

package com.intel.dleyna.lib;

import android.os.Bundle;
import android.os.RemoteException;

/**
 * The Controller portion of the Server API:
 * methods for controlling the ??? of media content on a Server.
 * <p>
 * Since Servers are usually on remote devices, and thus prone to arbitrary delays,
 * you should never invoke the methods of this interface from the UI thread
 * (with one exception: {@link #addControllerListener(IServerControllerListener)}).
 * Perhaps the simplest way to manage the required multi-threading is to use an
 * {@link android.os.AsyncTask}.
 */
public interface IServerController {


    /**
     * Register for notification of events from this controller.
     * <p>
     * All event notifications will run on the application's main thread.
     * @param listener an instance of your implementation of {@link IServerControllerListener}.
     */
    public void addControllerListener(IServerControllerListener listener);

    // TODO: ?
}
