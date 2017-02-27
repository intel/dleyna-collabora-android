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
 */

package com.intel.dleyna.lib;

import android.os.Bundle;

/**
 * A default implementation of {@link IRendererControllerListener} whose
 * methods do nothing.
 * <p>
 * If you are only interested in a subset of the events of the interface,
 * you may find it easier to extend this class than to implement the entire interface.
 */
public class RendererControllerListener implements IRendererControllerListener {

    public void onPlaybackStatusChanged(IRendererController c, String status) {
    }

    public void onRateChanged(IRendererController c, double rate) {
    }

    public void onMetadataChanged(IRendererController c, Bundle metadata) {
    }

    public void onVolumeChanged(IRendererController c, double volume) {
    }

    public void onMinimumRateChanged(IRendererController c, double rate) {
    }

    public void onMaximumRateChanged(IRendererController c, double rate) {
    }

    public void onCanGoNextChanged(IRendererController c, boolean value) {
    }

    public void onCanGoPreviousChanged(IRendererController c, boolean value) {
    }

    public void onTrackChanged(IRendererController c, int track) {
    }

    public void onPositionChanged(IRendererController c, long position) {
    }

    public void onCanPlayChanged(IRendererController c, boolean value) {
    }

    public void onCanPauseChanged(IRendererController c, boolean value) {
    }

    public void onCanSeekChanged(IRendererController c, boolean value) {
    }

    public void onCanControlChanged(IRendererController c, boolean value) {
    }

    public void onTransportPlaySpeedsChanged(IRendererController c, double[] speeds) {
    }

    public void onCurrentTrackChanged(IRendererController c, int track) {
    }

    public void onNumberOfTracksChanged(IRendererController c, int n) {
    }

    public void onMuteChanged(IRendererController c, boolean value) {
    }
}
