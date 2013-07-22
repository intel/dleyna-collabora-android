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

    /** Our manager. */
    private RendererManager manager;

    /** Identifies this renderer to the background renderer service. */
    private final String objectPath;

    private List<IRendererControllerEvents> controllerListeners = new LinkedList<IRendererControllerEvents>();

    /** The package-visible constructor */
    Renderer(RendererManager manager, String objectPath) {
        this.manager = manager;
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
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        return service.getDeviceType(client, objectPath);
    }

    public String getUniqueDeviceName() throws RemoteException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        return service.getUniqueDeviceName(client, objectPath);
    }

    public String getFriendlyName() throws RemoteException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        return service.getFriendlyName(client, objectPath);
    }

    public String getIconURL() throws RemoteException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        return service.getIconURL(client, objectPath);
    }

    public String getManufacturer() throws RemoteException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        return service.getManufacturer(client, objectPath);
    }

    public String getManufacturerURL() throws RemoteException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        return service.getManufacturerURL(client, objectPath);
    }

    public String getModelDescription() throws RemoteException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        return service.getModelDescription(client, objectPath);
    }

    public String getModelName() throws RemoteException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        return service.getModelName(client, objectPath);
    }

    public String getModelNumber() throws RemoteException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        return service.getModelNumber(client, objectPath);
    }

    public String getSerialNumber() throws RemoteException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        return service.getSerialNumber(client, objectPath);
    }

    public String getPresentationURL() throws RemoteException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        return service.getPresentationURL(client, objectPath);
    }

    public String getProtocolInfo() throws RemoteException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        return service.getProtocolInfo(client, objectPath);
    }

    public void cancel() throws RemoteException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        service.cancel(client, objectPath);
    }

    public Icon getIcon() throws RemoteException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        return service.getIcon(client, objectPath);
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
