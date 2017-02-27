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

import android.os.Bundle;

/**
 * Notifications of events from the {@link IServerController} interface of a Server.
 * <p>
 * You attach implementations of this interface to instances of {@link Server}
 * by calling {@link Server#addControllerListener(IServerControllerListener)}.
 * <p>
 * A default implementation of this interface is provided by {@link ServerControllerListener}.
 * <p>
 * All event notifications will run on the application's main thread.
 */
public interface IServerControllerListener {

    /**
     * Notification that the system id has changed.
     * @param c the server controller
     * @param systemId the system id
     */
    public void onSystemUpdateId(IServerController c, int systemId);
}
