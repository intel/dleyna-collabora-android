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
 */

package com.intel.dleyna.lib;

import java.util.Map;

/**
 * Asynchronous events from the controller interface of a renderer.
 * <p>
 * You attach implementations of this interface to instances of {@link IRendererController} by calling
 * {@link IRendererController#addListener(IRendererControllerEvents)}.
 * <p>
 * A default implementation of this interface is provided by {@link Renderer.ControllerEvents}.
 */
public interface IRendererControllerEvents {

    /**
     * Notification that the playback status has changed.
     * @param status the new playback status: "Playing", "Paused", or "Stopped"
     * @param c the controller
     */
    public void onPlaybackStatusChanged(IRendererController c, String status);

    /**
     * Notification that the playback rate has changed.
     * @param c the controller
     * @param rate the new playback rate
     */
    public void onRateChanged(IRendererController c, double rate);

    /**
     * Notification that the metadata of the current element has changed.
     * @param c the controller
     * @param metadata the new metadata
     */
    public void onMetadataChanged(IRendererController c, Map<String,Object> metadata);

    /**
     * Notification that the volume level has changed.
     * @param c
     * @param volume the new volume level
     */
    public void onVolumeChanged(IRendererController c, double volume);

    /**
     * Notification that the minimum playback rate has changed.
     * @param c the controller
     * @param rate the new minimum playback rate
     */
    public void onMinimumRateChanged(IRendererController c, long rate);

    /**
     * Notification that the maximum playback rate has changed.
     * @param c the controller
     * @param rate the new maximum playback rate
     */
    public void onMaximumRateChanged(IRendererController c, long rate);

    /**
     * Notification that this renderer's ability to do
     * {@link IRendererController#next()} has changed.
     * @param c the controller
     * @param value true if and only if this renderer can now go to the next track
     */
    public void onCanGoNextChanged(IRendererController c, boolean value);

    /**
     * Notification that this renderer's ability to do
     * {@link IRendererController#previous()} has changed.
     * @param value true if and only if this renderer can now go to the previous track
     */
    public void onCanGoPreviousChanged(IRendererController c, boolean value);

    /**
     * Notification that this renderer's number of tracks has changed.
     * @param c the controller
     * @param n the new number of tracks
     */
    public void onNumberOfTracksChanged(IRendererController c, int n);

    /**
     * Notification that the current track number has changed.
     * @param c the controller
     * @param track the new current track number
     */
    public void onTrackChanged(IRendererController c, int track);

    /**
     * Notification that the supported playback rates have changed.
     * @param c the controller
     * @param speeds the new set of supported playback rates
     */
    public void onTransportPlaySpeedsChanged(IRendererController c, double[] speeds);
    /**
     * Notification that this renderer's mute state has changed.
     * @param c the controller
     * @param value the new mute state
     */
    public void onMuteChanged(IRendererController c, boolean value);
}
