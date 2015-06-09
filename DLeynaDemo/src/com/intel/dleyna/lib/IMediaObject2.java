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
 * The MediaObject2 portion of the Server API:
 * methods for obtaining information about a Media Object.
 * <p>
 * Since Servers are usually on remote devices, and thus prone to arbitrary delays,
 * you should never invoke the methods of this interface from the UI thread.
 * Perhaps the simplest way to manage the required multi-threading is to use an
 * {@link android.os.AsyncTask}.
 */
public interface IMediaObject2 {

    /**
     * Register for notification of events from this media object.
     * <p>
     * All event notifications will run on the application's main thread.
     * @param listener an instance of your implementation of {@link IMediaObject2Listener}.
     */
    // TODO: ? public void addMediaObject2Listener(IMediaObject2Listener listener);

    /**
     * Get the media object's metadata.
     * @return the metadata as an xml string.
     * @throws RemoteException no connection to the background server service
     * @throws DLeynaException failure reported by the background server service
     */
    public String getMetadata() throws RemoteException, DLeynaException;
}
