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

    String[] getRenderers(IRendererClient client);
    void rescan(IRendererClient client);

    /*-----------------+
     | IRendererDevice |
     +-----------------*/

    String getDeviceType(IRendererClient client, String objectPath);
    String getUniqueDeviceName(IRendererClient client, String objectPath);
    String getFriendlyName(IRendererClient client, String objectPath);
    String getIconURL(IRendererClient client, String objectPath);
    String getManufacturer(IRendererClient client, String objectPath);
    String getManufacturerURL(IRendererClient client, String objectPath);
    String getModelDescription(IRendererClient client, String objectPath);
    String getModelName(IRendererClient client, String objectPath);
    String getModelNumber(IRendererClient client, String objectPath);
    String getSerialNumber(IRendererClient client, String objectPath);
    String getPresentationURL(IRendererClient client, String objectPath);
    String getProtocolInfo(IRendererClient client, String objectPath);
    Icon getIcon(IRendererClient client, String objectPath);
    void cancel(IRendererClient client, String objectPath);

    /*---------------------+
     | IRendererController |
     +---------------------*/

    void next(IRendererClient client, String objectPath);
    void previous(IRendererClient client, String objectPath);
    void pause(IRendererClient client, String objectPath);
    void playPause(IRendererClient client, String objectPath);
    void stop(IRendererClient client, String objectPath);
    void play(IRendererClient client, String objectPath);
    void seek(IRendererClient client, String objectPath, long offset);
    void setPosition(IRendererClient client, String objectPath, long position);
    void openUri(IRendererClient client, String objectPath, String uri);
    String getPlaybackStatus(IRendererClient client, String objectPath);
    double getRate(IRendererClient client, String objectPath);
    void setRate(IRendererClient client, String objectPath, double rate);
    Bundle getMetadata(IRendererClient client, String objectPath);
    double getVolume(IRendererClient client, String objectPath);
    void setVolume(IRendererClient client, String objectPath, double volume);
    long getPosition(IRendererClient client, String objectPath);
    long getMinimumRate(IRendererClient client, String objectPath);
    long getMaximumRate(IRendererClient client, String objectPath);
    boolean getCanGoNext(IRendererClient client, String objectPath);
    boolean getCanGoPrevious(IRendererClient client, String objectPath);
    boolean getCanPlay(IRendererClient client, String objectPath);
    boolean getCanPause(IRendererClient client, String objectPath);
    boolean getCanSeek(IRendererClient client, String objectPath);
    boolean getCanControl(IRendererClient client, String objectPath);
    int getNumberOfTracks(IRendererClient client, String objectPath);
    void goToTrack(IRendererClient client, String objectPath, int track);
    int getCurrentTrack(IRendererClient client, String objectPath);
    void openUriEx(IRendererClient client, String objectPath, String uri, String metadata);
    double[] getTransportPlaySpeeds(IRendererClient client, String objectPath);
    boolean getMute(IRendererClient client, String objectPath);
    void setMute(IRendererClient client, String objectPath, boolean value);

    /*-------------------+
     | IRendererPushHost |
     +-------------------*/

    String hostFile(IRendererClient client, String objectPath, String path);
    void removeFile(IRendererClient client, String objectPath, String path);

}
