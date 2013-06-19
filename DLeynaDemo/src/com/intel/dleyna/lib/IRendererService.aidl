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
import com.intel.dleyna.lib.IRendererCallback;

interface IRendererService {

    // Client registration.
    void registerClient(IRendererCallback cb);

    // Client de-registration.
    void unregisterClient(IRendererCallback cb);

    /*-----------------+
     | IRendererDevice |
     +-----------------*/

    String getDeviceType(String objectPath);
    String getUniqueDeviceName(String objectPath);
    String getFriendlyName(String objectPath);
    String getIconURL(String objectPath);
    String getManufacturer(String objectPath);
    String getManufacturerURL(String objectPath);
    String getModelDescription(String objectPath);
    String getModelName(String objectPath);
    String getModelNumber(String objectPath);
    String getSerialNumber(String objectPath);
    String getPresentationURL(String objectPath);
    String getProtocolInfo(String objectPath);
    Icon getIcon(String objectPath);
    void cancel(String objectPath);

    /*---------------------+
     | IRendererController |
     +---------------------*/

    void next(String objectPath);
    void previous(String objectPath);
    void pause(String objectPath);
    void playPause(String objectPath);
    void stop(String objectPath);
    void play(String objectPath);
    void seek(String objectPath, long offset);
    void setPosition(String objectPath, long position);
    void openUri(String objectPath, String uri);
    String getPlaybackStatus(String objectPath);
    double getRate(String objectPath);
    void setRate(String objectPath, double rate);
    Bundle getMetadata(String objectPath);
    double getVolume(String objectPath);
    void setVolume(String objectPath, double volume);
    long getPosition(String objectPath);
    long getMinimumRate(String objectPath);
    long getMaximumRate(String objectPath);
    boolean getCanGoNext(String objectPath);
    boolean getCanGoPrevious(String objectPath);
    boolean getCanPlay(String objectPath);
    boolean getCanPause(String objectPath);
    boolean getCanSeek(String objectPath);
    boolean getCanControl(String objectPath);
    int getNumberOfTracks(String objectPath);
    void goToTrack(String objectPath, int track);
    int getCurrentTrack(String objectPath);
    void openUriEx(String objectPath, String uri, String metadata);
    double[] getTransportPlaySpeeds(String objectPath);
    boolean getMute(String objectPath);
    void setMute(String objectPath, boolean value);

    /*-------------------+
     | IRendererPushHost |
     +-------------------*/

    String hostFile(String objectPath, String path);
    void removeFile(String objectPath, String path);

}
