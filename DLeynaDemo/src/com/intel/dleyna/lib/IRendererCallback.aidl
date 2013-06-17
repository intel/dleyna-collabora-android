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

interface IRendererCallback {

    /*-------------------------+
     | IRendererManager.Events |
     +-------------------------*/

    void onRendererFound(String objectPath);
    void onRendererLost(String objectPath);

    /*---------------------------+
     | IRendererControllerEvents |
     +---------------------------*/

    void onPlaybackStatusChanged(String objectPath, String status);
    void onRateChanged(String objectPath, double rate);
    void onMetadataChanged(String objectPath, in Bundle metadata);
    void onVolumeChanged(String objectPath, double volume);
    void onMinimumRateChanged(String objectPath, long rate);
    void onMaximumRateChanged(String objectPath, long rate);
    void onCanGoNextChanged(String objectPath, boolean value);
    void onCanGoPreviousChanged(String objectPath, boolean value);
    void onNumberOfTracksChanged(String objectPath, int n);
    void onTrackChanged(String objectPath, int track);
    void onTransportPlaySpeedsChanged(String objectPath, in double[] speeds);
    void onMuteChanged(String objectPath, boolean value);
}
