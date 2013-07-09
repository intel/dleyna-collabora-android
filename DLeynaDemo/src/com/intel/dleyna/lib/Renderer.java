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

import java.util.LinkedList;
import java.util.List;

import android.os.Bundle;
import android.os.RemoteException;

/**
 * Instances of this class correspond to Digital Media Renderers
 * on local networks attached to this device (or on the device itself).
 * <p>
 * The methods of this class have been partitioned into three interfaces
 * that represent three semi-independent aspects of a renderer:
 * {@link IRendererDevice}, {@link IRendererController}, and {@link IRendererPushHost}.
 * <p>
 * Use the {@link RendererManager} to discover instances of this class.
 * <p>
 * The connection to the background renderer service that you initiate with
 * {@link RendererManager#connect(android.content.Context)} must be established for the methods of this
 * class to succeed. Otherwise, they will throw a {@link RemoteException}.
 */
public class Renderer implements IRendererDevice, IRendererController, IRendererPushHost {

    /** Identifies this renderer to the background renderer service. */
    private final String objectPath;

    private List<IRendererControllerEvents> controllerListeners = new LinkedList<IRendererControllerEvents>();

    /** The package-visible constructor */
    Renderer(String objectPath) {
        this.objectPath = objectPath;
    }

    List<IRendererControllerEvents> getControllerListeners() {
        return controllerListeners;
    }

    public String getObjectPath() {
        return objectPath;
    }

    /*-----------------+
     | IRendererDevice |
     +-----------------*/

    public String getDeviceType() throws RemoteException {
        return null;
    }

    public String getUniqueDeviceName() throws RemoteException {
        return null;
    }

    public String getFriendlyName() throws RemoteException {
        return null;
    }

    public String getIconURL() throws RemoteException {
        return null;
    }

    public String getManufacturer() throws RemoteException {
        return null;
    }

    public String getManufacturerURL() throws RemoteException {
        return null;
    }

    public String getModelDescription() throws RemoteException {
        return null;
    }

    public String getModelName() throws RemoteException {
        return null;
    }

    public String getModelNumber() throws RemoteException {
        return null;
    }

    public String getSerialNumber() throws RemoteException {
        return null;
    }

    public String getPresentationURL() throws RemoteException {
        return null;
    }

    public String getProtocolInfo() throws RemoteException {
        return null;
    }

    public void cancel() throws RemoteException {
    }

    public Icon getIcon() {
         return null;
    }

    /*---------------------+
     | IRendererController |
     +---------------------*/

    public void addListener(IRendererControllerEvents events) {
        controllerListeners.add(events);
    }

    public void next() throws RemoteException {
    }

    public void previous() throws RemoteException {
    }

    public void pause() throws RemoteException {
    }

    public void playPause() throws RemoteException {
    }

    public void stop() throws RemoteException {
    }

    public void play() throws RemoteException {
    }

    public void seek(long offset) throws RemoteException {
    }

    public void setPosition(long position) throws RemoteException {
    }

    public void openUri(String uri) throws RemoteException {
    }

    public String getPlaybackStatus() throws RemoteException {
        return null;
    }

    public double getRate() throws RemoteException {
        return 0;
    }

    public void setRate(double rate) throws RemoteException {
    }

    public Bundle getMetadata() throws RemoteException {
        return null;
    }

    public double getVolume() throws RemoteException {
        return 0;
    }

    public void setVolume(double volume) throws RemoteException {
    }

    public long getPosition() throws RemoteException {
        return 0;
    }

    public long getMinimumRate() throws RemoteException {
        return 0;
    }

    public long getMaximumRate() throws RemoteException {
        return 0;
    }

    public boolean getCanGoNext() throws RemoteException {
        return false;
    }

    public boolean getCanGoPrevious() throws RemoteException {
        return false;
    }

    public boolean getCanPlay() throws RemoteException {
        return false;
    }

    public boolean getCanPause() throws RemoteException {
        return false;
    }

    public boolean getCanSeek() throws RemoteException {
        return false;
    }

    public boolean getCanControl() throws RemoteException {
        return false;
    }

    public int getNumberOfTracks() throws RemoteException {
        return 0;
    }

    public void goToTrack(int track) throws RemoteException {
    }

    public int getCurrentTrack() throws RemoteException {
        return 0;
    }

    public void openUriEx(String uri, String metadata) throws RemoteException {
    }

    public double[] getTransportPlaySpeeds() throws RemoteException {
        return null;
    }

    public boolean getMute() throws RemoteException {
        return false;
    }

    public void setMute(boolean value) throws RemoteException {
    }

    /*-------------------+
     | IRendererPushHost |
     +-------------------*/

    public String hostFile(String path) throws RemoteException {
        return null;
    }

    public void removeFile(String path) throws RemoteException {
    }

    /*-----------+
     | Callbacks |
     +-----------*/

    /**
     * A default implementation of {@link IRendererControllerEvents} whose
     * methods do nothing.
     * <p>
     * If you are only interested in a subset of the events of the interface,
     * you may find it easier to extend this class than to implement the entire interface.
     */
    public static class ControllerEvents implements IRendererControllerEvents {

        public void onPlaybackStatusChanged(IRendererController c, String status) {
        }

        public void onRateChanged(IRendererController c, double rate) {
        }

        public void onMetadataChanged(IRendererController c, Bundle metadata) {
        }

        public void onVolumeChanged(IRendererController c, double volume) {
        }

        public void onMinimumRateChanged(IRendererController c, long rate) {
        }

        public void onMaximumRateChanged(IRendererController c, long rate) {
        }

        public void onCanGoNextChanged(IRendererController c, boolean value) {
        }

        public void onCanGoPreviousChanged(IRendererController c, boolean value) {
        }

        public void onNumberOfTracksChanged(IRendererController c, int n) {
        }

        public void onTrackChanged(IRendererController c, int track) {
        }

        public void onTransportPlaySpeedsChanged(IRendererController c, double[] speeds) {
        }

        public void onMuteChanged(IRendererController c, boolean value) {
        }
    }

    /*--------------------+
    | For RendererManager |
    +---------------------*/

    private boolean used;

    boolean getIsObsolete() {
        return this.used;
    }

    void setIsObsolete(boolean used) {
        this.used = used;
    }
}
