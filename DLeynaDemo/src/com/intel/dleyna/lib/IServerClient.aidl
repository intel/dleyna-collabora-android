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
 *
 */

package com.intel.dleyna.lib;

import com.intel.dleyna.lib.ContainerUpdateId;
import com.intel.dleyna.lib.DmsFeature;

interface IServerClient {

    /*------------------------+
     | IServerManagerListener |
     +------------------------*/

    void onServerFound(String objectPath);
    void onServerLost(String objectPath);
    void onLastChange(String objectPath, in Bundle props);

    /*---------------------------+
     | IServerControllerListener |
     +---------------------------*/

    void onControllerPropertiesChanged(String objectPath, in Bundle props);

    /*----------------------+
     | IMediaDeviceListener |
     +----------------------*/

    void onContainerUpdateIds(String objectPath, in ContainerUpdateId[] updates);
    void onDevicePropertiesChanged(String objectPath, in Bundle props);
    void onUploadUpdate(String objectPath, int id, String status, long length, long total);

    /*------------------------------+
     | IContainerControllerListener |
     +------------------------------*/

    //void onUpdated(String objectPath); // TODO: FIXME path or id
    //void onUpdated(String objectPath, in Bundle props);
}
