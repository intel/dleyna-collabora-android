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

import android.os.RemoteException;

/**
 * The Controller portion of the Renderer API.
 */
public interface IRendererController {

    /**
     * Register for notification of events from this controller.
     * @param events an instance of your implementation of {@link IRendererControllerEvents}.
     */
    public void addListener(IRendererControllerEvents events);

    /**
     * Skip to the next track in the tracklist.
     * <p>
     * If there is no next track, and endless playback and track repeat are off, stop playback.
     * If playback is paused or stopped, do nothing.
     * <p>
     * This method has no effect if {@link #getCanGoNext()} returns false.
     * @throws RemoteException no connection to the background renderer service
     */
    public void next() throws RemoteException;

    /**
     * Skip to the previous track in the tracklist.
     * <p>
     * If there is no previous track, and endless playback and track repeat are off, stop playback.
     * If playback is paused or stopped, do nothing.
     * <p>
     * This method has no effect if {@link #getCanGoPrevious()} returns false.
     * @throws RemoteException no connection to the background renderer service
     */
    public void previous() throws RemoteException;

    /**
     * Pause playback.
     * <p>
     * If playback is already paused, this has no effect.
     * Calling {@link #play()} after this should cause playback to resume from the same position.
     * <p>
     * This method has no effect if {@link #getCanPause()} returns false.
     * @throws RemoteException no connection to the background renderer service
     */
    public void pause() throws RemoteException;

    /**
     * Start, resume, or pause playback.
     * <p>
     * If playback is stopped, this starts playback.
     * If playback is paused, this resumes playback.
     * If playback is playing, this pauses playback.
     * <p>
     * This method has no effect if {@link #getCanPause()} returns false.
     * @throws RemoteException no connection to the background renderer service
     */
    public void playPause() throws RemoteException;

    /**
     * Stop playback.
     * <p>
     * If playback is already stopped, this has no effect.
     * Calling {@link #play()} after this should cause playback to start from the beginning of the track.
     * <p>
     * This method has no effect if {@link #getCanControl()} returns false.
     * @throws RemoteException no connection to the background renderer service
     */
    public void stop() throws RemoteException;

    /**
     * Start or resume playback.
     * <p>
     * If already playing, this has no effect.
     * If paused, playback resumes from the current position.
     * If there is no track to play, this has no effect.
     * <p>
     * This method has no effect if {@link #getCanPlay()} returns false.
     * @throws RemoteException no connection to the background renderer service
     */
    public void play() throws RemoteException;

    /**
     * Seek in the current track, relative to the current position,
     * by the given number of microseconds.
     * <p>
     * Positive values seek forward, negative values backward.
     * Trying to seek backward beyond the start of the track results in seeking the start of the track.
     * Trying to seek forward beyond the end of the track is equivalent to calling {@link #next()}.
     * <p>
     * This method has no effect if {@link #getCanSeek()} returns false.
     * @param offset the offset in microseconds
     * @throws RemoteException no connection to the background renderer service
     */
    public void seek(long offset) throws RemoteException;

    /**
     * Set the position in the current track to the given value in microseconds.
     * <p>
     * This method has no effect if {@link #getCanSeek()} returns false.
     * @param position absolute position in microseconds
     * @throws RemoteException no connection to the background renderer service
     */
    public void setPosition(long position) throws RemoteException;

    /**
     * Open the track specified by given URI.
     * <p>
     * @param uri the URI of the track to open
     * @throws RemoteException no connection to the background renderer service
     */
    public void openUri(String uri) throws RemoteException;

    /**
     * Get the current playback status.
     * @return "Playing", "Paused", or "Stopped"
     * @throws RemoteException no connection to the background renderer service
     */
    public String getPlaybackStatus() throws RemoteException;

    /**
     * Get the current playback rate.
     * <p>
     * The current playback rate is a value between those returned by {@link #getMinimumRate()}
     * and {@link #getMaximumRate()}.
     * Note that this should never return zero; paused playback is indicated by
     * {@link #getPlaybackStatus()} returning "Paused".
     * @return the current playback rate
     * @throws RemoteException no connection to the background renderer service
     */
    public double getRate() throws RemoteException;

    /**
     * Set the playback rate.
     * <p>
     * The specified rate must be between the values returned by {@link #getMinimumRate()}
     * and {@link #getMaximumRate()}, and should not be zero.
     * Specifying zero may have the same effect as calling {@link #pause()},
     * but in any case does not change the playback rate value.
     * @param rate the new playback rate
     * @throws RemoteException no connection to the background renderer service
     */
    public void setRate(double rate) throws RemoteException;

    /**
     * Get the metadata of the current element.
     * <p>
     * The metadata is a mapping of attribute names to values.
     * <p>
     * TODO: elaborate
     * <p>
     * @return the metadata
     * @throws RemoteException no connection to the background renderer service
     */
    public Map<String,Object> getMetadata() throws RemoteException;

    /**
     * Get the current volume level.
     * @return the current volume level
     * @throws RemoteException no connection to the background renderer service
     */
    public double getVolume() throws RemoteException;

    /**
     * Set the volume level.
     * @param volume the volume level
     * @throws RemoteException no connection to the background renderer service
     */
    public void setVolume(double volume) throws RemoteException;

    /**
     * Get the current track position in microseconds.
     * @return the current track position in microseconds
     * @throws RemoteException no connection to the background renderer service
     */
    public long getPosition() throws RemoteException;

    /**
     * Get the minimum playback rate.
     * <p>
     * This is the minimum value that {@link #getRate()} can return,
     * and the minimum value you can specify in {@link #setRate(double)}.
     * Note that even if this value is non-positive,
     * you should not attempt set the rate to a non-positive value.
     * This value should be less than or equal to 1.0.
     * @return the minimum playback rate
     * @throws RemoteException no connection to the background renderer service
     */
    public long getMinimumRate() throws RemoteException;

    /**
     * Get the maximum playback rate.
     * <p>
     * This is the maximum value that {@link #getRate()} can return,
     * and the maximum value you can specify in {@link #setRate(double)}.
     * This value should be greater than or equal to 1.0.
     * @return the maximum playback rate
     * @throws RemoteException no connection to the background renderer service
     */
    public long getMaximumRate() throws RemoteException;

    /**
     * Query whether this renderer can currently do {@link #next()}.
     * @return true if and only if can currently do {@link #next()}
     * @throws RemoteException no connection to the background renderer service
     */
    public boolean getCanGoNext() throws RemoteException;

    /**
     * Query whether this renderer can currently do {@link #previous()}.
     * @return true if and only if this renderer can currently do {@link #previous()}
     * @throws RemoteException no connection to the background renderer service
     */
    public boolean getCanGoPrevious() throws RemoteException;

    /**
     * Query whether this renderer can currently do {@link #play()} or {@link #playPause()}.
     * @return true if and only if this renderer can currently do {@link #play()} or {@link #playPause()}
     * @throws RemoteException no connection to the background renderer service
     */
    public boolean getCanPlay() throws RemoteException;
    /**
     * Query whether this renderer can currently do {@link #pause()}.
     * @return true if and only if this renderer can currently do {@link #pause()}
     * @throws RemoteException no connection to the background renderer service
     */
    public boolean getCanPause() throws RemoteException;

    /**
     * Query whether this renderer can currently do {@link #seek(long)}.
     * @return true if and only if this renderer can currently do {@link #seek(long)}
     * @throws RemoteException no connection to the background renderer service
     */
    public boolean getCanSeek() throws RemoteException;

    /**
     * Query whether this renderer can be controlled.
     * <p>
     * This property describes an intrinsic property of this renderer
     * that is not expected to change.
     * <p>
     * If this property is false, then methods that query the ability to perform specific operations,
     * like {@link #getCanPlay()}, {@link #getCanPause()}, etc. all return false,
     * and methods that attempt to control the renderer,
     * like {@link #play()}, {@link #pause()}, {@link #setVolume(double)}, etc.
     * have no effect.
     * @return true if and only if this renderer can can be controlled
     * @throws RemoteException no connection to the background renderer service
     */
    public boolean getCanControl() throws RemoteException;

    /*----------------------------------------------------+
     | org.mpris.MediaPlayer2.Player interface extensions |
     +----------------------------------------------------*/

    /**
     * Get the number of tracks.
     * @return the number of tracks
     * @throws RemoteException no connection to the background renderer service
     */
    public int getNumberOfTracks() throws RemoteException;

    /**
     * Seek to the specified track number.
     * @param track desired track number
     * @throws RemoteException no connection to the background renderer service
     */
    public void goToTrack(int track) throws RemoteException;

    /**
     * Get the current track number.
     * @return the current track number
     * @throws RemoteException no connection to the background renderer service
     */
    public int getCurrentTrack() throws RemoteException;

    /**
     * Open the track specified by given URI and the given metadata.
     * @param uri the URI of the track to open
     * @param metadata the DIDL-Lite XML description of the item to be opened
     * @throws RemoteException no connection to the background renderer service
     * @see #openUri(String)
     */
    public void openUriEx(String uri, String metadata) throws RemoteException;

    /**
     * Get the playback rates supported by this renderer.
     * <p>
     * This returns playback rates that can be used with {@link #setRate(double)}.
     * @return the playback rates supported by this renderer
     * @throws RemoteException no connection to the background renderer service
     */
    public double[] getTransportPlaySpeeds() throws RemoteException;

    /**
     * Get this renderer's current mute state.
     * @return the current mute statue
     * @throws RemoteException no connection to the background renderer service
     */
    public boolean getMute() throws RemoteException;

    /**
     * Set this renderer's mute state.
     * @param value the new mute state
     * @throws RemoteException no connection to the background renderer service
     */
    public void setMute(boolean value) throws RemoteException;
}