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
 * Tom Keel <thomas.keel@intel.com>
 *
 */

package com.intel.dleyna.lib;

import com.intel.dleyna.lib.Icon;
import com.intel.dleyna.lib.IRendererClient;

interface IRendererService {

    // Client registration.
    void registerClient(IRendererClient client);

    // Client de-registration.
    void unregisterClient(IRendererClient client);
    
    /*-----------------+
     | RendererManager |
     +-----------------*/

    String[] getRenderers(IRendererClient client, out Bundle extras);
    void rescan(IRendererClient client, out Bundle extras);

    /*-----------------+
     | IRendererDevice |
     +-----------------*/

    String getDeviceType(IRendererClient client, String objectPath, out Bundle extras);
    String getUniqueDeviceName(IRendererClient client, String objectPath, out Bundle extras);
    String getFriendlyName(IRendererClient client, String objectPath, out Bundle extras);
    String getIconURL(IRendererClient client, String objectPath, out Bundle extras);
    String getManufacturer(IRendererClient client, String objectPath, out Bundle extras);
    String getManufacturerURL(IRendererClient client, String objectPath, out Bundle extras);
    String getModelDescription(IRendererClient client, String objectPath, out Bundle extras);
    String getModelName(IRendererClient client, String objectPath, out Bundle extras);
    String getModelNumber(IRendererClient client, String objectPath, out Bundle extras);
    String getSerialNumber(IRendererClient client, String objectPath, out Bundle extras);
    String getPresentationURL(IRendererClient client, String objectPath, out Bundle extras);
    String getProtocolInfo(IRendererClient client, String objectPath, out Bundle extras);
    Icon getIcon(IRendererClient client, String objectPath, out Bundle extras);
    void cancel(IRendererClient client, String objectPath, out Bundle extras);

    /*---------------------+
     | IRendererController |
     +---------------------*/

    void next(IRendererClient client, String objectPath, out Bundle extras);
    void previous(IRendererClient client, String objectPath, out Bundle extras);
    void pause(IRendererClient client, String objectPath, out Bundle extras);
    void playPause(IRendererClient client, String objectPath, out Bundle extras);
    void stop(IRendererClient client, String objectPath, out Bundle extras);
    void play(IRendererClient client, String objectPath, out Bundle extras);
    void seek(IRendererClient client, String objectPath, long offset, out Bundle extras);
    void setPosition(IRendererClient client, String objectPath, long position, out Bundle extras);
    void openUri(IRendererClient client, String objectPath, String uri, out Bundle extras);
    String getPlaybackStatus(IRendererClient client, String objectPath, out Bundle extras);
    double getRate(IRendererClient client, String objectPath, out Bundle extras);
    void setRate(IRendererClient client, String objectPath, double rate, out Bundle extras);
    Bundle getMetadata(IRendererClient client, String objectPath, out Bundle extras);
    double getVolume(IRendererClient client, String objectPath, out Bundle extras);
    void setVolume(IRendererClient client, String objectPath, double volume, out Bundle extras);
    long getPosition(IRendererClient client, String objectPath, out Bundle extras);
    double getMinimumRate(IRendererClient client, String objectPath, out Bundle extras);
    double getMaximumRate(IRendererClient client, String objectPath, out Bundle extras);
    boolean getCanGoNext(IRendererClient client, String objectPath, out Bundle extras);
    boolean getCanGoPrevious(IRendererClient client, String objectPath, out Bundle extras);
    boolean getCanPlay(IRendererClient client, String objectPath, out Bundle extras);
    boolean getCanPause(IRendererClient client, String objectPath, out Bundle extras);
    boolean getCanSeek(IRendererClient client, String objectPath, out Bundle extras);
    boolean getCanControl(IRendererClient client, String objectPath, out Bundle extras);
    int getNumberOfTracks(IRendererClient client, String objectPath, out Bundle extras);
    void goToTrack(IRendererClient client, String objectPath, int track, out Bundle extras);
    int getCurrentTrack(IRendererClient client, String objectPath, out Bundle extras);
    void openUriEx(IRendererClient client, String objectPath, String uri, String metadata,
            out Bundle extras);
    double[] getTransportPlaySpeeds(IRendererClient client, String objectPath, out Bundle extras);
    boolean getMute(IRendererClient client, String objectPath, out Bundle extras);
    void setMute(IRendererClient client, String objectPath, boolean value, out Bundle extras);

    /*-------------------+
     | IRendererPushHost |
     +-------------------*/

    String hostFile(IRendererClient client, String objectPath, String path, out Bundle extras);
    void removeFile(IRendererClient client, String objectPath, String path, out Bundle extras);

}
