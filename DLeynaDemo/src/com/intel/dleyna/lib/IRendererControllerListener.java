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

import android.os.Bundle;

/**
 * Notifications of events from the {@link IRendererController} interface of a Renderer.
 * <p>
 * You attach implementations of this interface to instances of {@link Renderer}
 * by calling {@link Renderer#addControllerListener(IRendererControllerListener)}.
 * <p>
 * A default implementation of this interface is provided by {@link RendererControllerListener}.
 * <p>
 * All event notifications will run on the application's main thread.
 */
public interface IRendererControllerListener {

    /**
     * Notification that the playback status has changed.
     * <p>
     * @param status the new playback status: one of
     * {@link IRendererController#PLAYBACK_PLAYING},
     * {@link IRendererController#PLAYBACK_PAUSED},
     * {@link IRendererController#PLAYBACK_STOPPED}
     * @param c the renderer controller
     */
    public void onPlaybackStatusChanged(IRendererController c, String status);

    /**
     * Notification that the playback rate has changed.
     * @param c the renderer controller
     * @param rate the new playback rate
     */
    public void onRateChanged(IRendererController c, double rate);

    /**
     * Notification that the metadata of the current element has changed.
     * @param c the renderer controller
     * @param metadata the new metadata
     */
    public void onMetadataChanged(IRendererController c, Bundle metadata);

    /**
     * Notification that the volume level has changed.
     * @param c the renderer controller
     * @param volume the new volume level
     */
    public void onVolumeChanged(IRendererController c, double volume);

    /**
     * Notification that the position has changed.
     * @param c the renderer controller
     * @param position the position in microseconds
     */
    public void onPositionChanged(IRendererController c, long position);

    /**
     * Notification that the minimum playback rate has changed.
     * @param c the renderer controller
     * @param rate the new minimum playback rate
     */
    public void onMinimumRateChanged(IRendererController c, double rate);

    /**
     * Notification that the maximum playback rate has changed.
     * @param c the renderer controller
     * @param rate the new maximum playback rate
     */
    public void onMaximumRateChanged(IRendererController c, double rate);

    /**
     * Notification that this renderer's ability to do
     * {@link IRendererController#next()} has changed.
     * @param c the renderer controller
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
     * Notification that the ability to play has changed.
     * @param c the renderer controller
     * @param value the new ability-to-play value
     */
    public void onCanPlayChanged(IRendererController c, boolean value);

    /**
     * Notification that the ability to pause has changed.
     * @param c the renderer controller
     * @param value the new ability-to-pause value
     */
    public void onCanPauseChanged(IRendererController c, boolean value);

    /**
     * Notification that the ability to seek has changed.
     * @param c the renderer controller
     * @param value the new ability-to-seek value
     */
    public void onCanSeekChanged(IRendererController c, boolean value);

    /**
     * Notification that the ability to control has changed.
     * @param c the renderer controller
     * @param value the new ability-to-control value
     */
    public void onCanControlChanged(IRendererController c, boolean value);

    /**
     * Notification that the supported playback rates have changed.
     * @param c the renderer controller
     * @param speeds the new set of supported playback rates
     */
    public void onTransportPlaySpeedsChanged(IRendererController c, double[] speeds);

    /**
     * Notification that the current track number has changed.
     * @param c the renderer controller
     * @param track the new current track number
     */
    public void onCurrentTrackChanged(IRendererController c, int track);

    /**
     * Notification that this renderer's number of tracks has changed.
     * @param c the renderer controller
     * @param n the new number of tracks
     */
    public void onNumberOfTracksChanged(IRendererController c, int n);

    /**
     * Notification that this renderer's mute state has changed.
     * @param c the renderer controller
     * @param value the new mute state
     */
    public void onMuteChanged(IRendererController c, boolean value);
}
