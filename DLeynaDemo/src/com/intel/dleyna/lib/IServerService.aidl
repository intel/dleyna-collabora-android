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
 */

package com.intel.dleyna.lib;

import com.intel.dleyna.lib.ContainerUpdateId;
import com.intel.dleyna.lib.DmsFeature;
import com.intel.dleyna.lib.Icon;
import com.intel.dleyna.lib.IServerClient;

interface IServerService {

    // Client registration.
    void registerClient(IServerClient client);

    // Client de-registration.
    void unregisterClient(IServerClient client);
    
    /*---------------+
     | ServerManager |
     +---------------*/

    String[] getServers(IServerClient client, out Bundle extras);
    String getVersion(IServerClient client, out Bundle extras);
    void rescan(IServerClient client, out Bundle extras);

    /*--------------+
     | IMediaDevice |
     +--------------*/

    String getLocation(IServerClient client, String objectPath, out Bundle extras);
    String getUniqueDeviceName(IServerClient client, String objectPath, out Bundle extras);
    String getRootUniqueDeviceName(IServerClient client, String objectPath, out Bundle extras);
    String getDeviceType(IServerClient client, String objectPath, out Bundle extras);
    String getFriendlyName(IServerClient client, String objectPath, out Bundle extras);
    String getManufacturer(IServerClient client, String objectPath, out Bundle extras);
    String getManufacturerUrl(IServerClient client, String objectPath, out Bundle extras);
    String getModelDescription(IServerClient client, String objectPath, out Bundle extras);
    String getModelName(IServerClient client, String objectPath, out Bundle extras);
    String getModelNumber(IServerClient client, String objectPath, out Bundle extras);
    String getModelUrl(IServerClient client, String objectPath, out Bundle extras);
    String getSerialNumber(IServerClient client, String objectPath, out Bundle extras);
    String getPresentationUrl(IServerClient client, String objectPath, out Bundle extras);
    String getIconUrl(IServerClient client, String objectPath, out Bundle extras);
    Bundle getDlnaCaps(IServerClient client, String objectPath, out Bundle extras);
    String[] getSearchCaps(IServerClient client, String objectPath, out Bundle extras);
    String[] getSortCaps(IServerClient client, String objectPath, out Bundle extras);
    String[] getSortExtCaps(IServerClient client, String objectPath, out Bundle extras);
    DmsFeature[] getFeatureList(IServerClient client, String objectPath, out Bundle extras);
    String getServiceResetToken(IServerClient client, String objectPath, out Bundle extras);
    int getSystemUpdateId(IServerClient client, String objectPath, out Bundle extras);
    boolean isSleeping(IServerClient client, String objectPath, out Bundle extras);
    String getProtocolInfo(IServerClient client, String objectPath, out Bundle extras);
    Icon getIcon(IServerClient client, String objectPath, out Bundle extras);
    void cancel(IServerClient client, String objectPath, out Bundle extras);
    Bundle[] browseObjects(IServerClient client, String objectPath, out Bundle extras);

    /*------------------+
     | IMediaContainer2 |
     +------------------*/

    Bundle[] listChildrenEx(IServerClient client, String objectPath, out Bundle extras);
    Bundle[] searchObjectsEx(IServerClient client, String objectPath, out Bundle extras);

    // TODO: additional methods 

    /*---------------+
     | IMediaObject2 |
     +---------------*/

    String getMetadata(IServerClient client, String objectPath, out Bundle extras);

    // TODO: additional methods 

    /*-------------------+
     | IServerController |
     +-------------------*/

    // TODO: ? 

    /*-------------+
     | IServerDemo |
     +-------------*/

    Bundle[] getChildren(IServerClient client, String objectPath, out Bundle extras);

    // TODO: ? 
}
