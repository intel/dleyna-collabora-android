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
 * <p>
 * Since Renderers are usually on remote devices, and thus prone to arbitrary delays,
 * you should never invoke methods that interact with Renderers from the UI thread.
 * Perhaps the simplest way to manage the required multi-threading is to use an
 * {@link android.os.AsyncTask}.
 */
public class Renderer implements IRendererDevice, IRendererController, IRendererPushHost {

    /** Our manager. */
    private RendererManager manager;

    /** Identifies this renderer to the background renderer service. */
    private final String objectPath;

    private List<IRendererControllerListener> controllerListeners = new LinkedList<IRendererControllerListener>();

    /** The package-visible constructor */
    Renderer(RendererManager manager, String objectPath) {
        this.manager = manager;
        this.objectPath = objectPath;
    }

    List<IRendererControllerListener> getControllerListeners() {
        return controllerListeners;
    }

    public String getObjectPath() {
        return objectPath;
    }

    /*-----------------+
     | IRendererDevice |
     +-----------------*/

    public String getDeviceType() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        String result = service.getDeviceType(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
        return result;
    }

    public String getUniqueDeviceName() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        String result = service.getUniqueDeviceName(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
        return result;
    }

    public String getFriendlyName() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        String result = service.getFriendlyName(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
        return result;
    }

    public String getIconURL() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        String result = service.getIconURL(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
        return result;
    }

    public String getManufacturer() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        String result = service.getManufacturer(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
        return result;
    }

    public String getManufacturerURL() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        String result = service.getManufacturerURL(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
        return result;
    }

    public String getModelDescription() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        String result = service.getModelDescription(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
        return result;
    }

    public String getModelName() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        String result = service.getModelName(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
        return result;
    }

    public String getModelNumber() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        String result = service.getModelNumber(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
        return result;
    }

    public String getSerialNumber() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        String result = service.getSerialNumber(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
        return result;
    }

    public String getPresentationURL() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        String result = service.getPresentationURL(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
        return result;
    }

    public String getProtocolInfo() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        String result = service.getProtocolInfo(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
        return result;
    }

    public void cancel() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        service.cancel(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
    }

    public Icon getIcon() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        Icon result = service.getIcon(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
        return result;
    }

    /*---------------------+
     | IRendererController |
     +---------------------*/

    public void addControllerListener(IRendererControllerListener listener) {
        controllerListeners.add(listener);
    }

    public void next() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        service.next(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
    }

    public void previous() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        service.previous(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
    }

    public void pause() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        service.pause(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
    }

    public void playPause() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        service.playPause(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
    }

    public void stop() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        service.stop(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
    }

    public void play() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        service.play(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
    }

    public void seek(long offset) throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        service.seek(client, objectPath, offset, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
    }

    public void setPosition(long position) throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        service.setPosition(client, objectPath, position, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
    }

    public void openUri(String uri) throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        service.openUri(client, objectPath, uri, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
    }

    public String getPlaybackStatus() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        String result = service.getPlaybackStatus(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
        return result;
    }

    public double getRate() throws RemoteException, DLeynaException {
        return 0;
    }

    public void setRate(double rate) throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        service.setRate(client, objectPath, rate, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
    }

    public Bundle getMetadata() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        Bundle result = service.getMetadata(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
        return result;
    }

    public double getVolume() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        double result = service.getVolume(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
        return result;
    }

    public void setVolume(double volume) throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        service.setVolume(client, objectPath, volume, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
    }

    public long getPosition() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        long result = service.getPosition(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
        return result;
    }

    public double getMinimumRate() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        double result = service.getMinimumRate(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
        return result;
    }

    public double getMaximumRate() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        double result = service.getMaximumRate(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
        return result;
    }

    public boolean getCanGoNext() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        boolean result = service.getCanGoNext(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
        return result;
    }

    public boolean getCanGoPrevious() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        boolean result = service.getCanGoPrevious(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
        return result;
    }

    public boolean getCanPlay() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        boolean result = service.getCanPlay(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
        return result;
    }

    public boolean getCanPause() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        boolean result = service.getCanPause(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
        return result;
    }

    public boolean getCanSeek() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        boolean result = service.getCanSeek(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
        return result;
    }

    public boolean getCanControl() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        boolean result = service.getCanControl(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
        return result;
    }

    public int getNumberOfTracks() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        int result = service.getNumberOfTracks(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
        return result;
    }

    public void goToTrack(int track) throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        service.goToTrack(client, objectPath, track, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
    }

    public int getCurrentTrack() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        int result = service.getCurrentTrack(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
        return result;
    }

    public void openUriEx(String uri, String metadata) throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        service.openUriEx(client, objectPath, uri, metadata, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
    }

    public double[] getTransportPlaySpeeds() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        double[] result = service.getTransportPlaySpeeds(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
        return result;
    }

    public boolean getMute() throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        boolean result = service.getMute(client, objectPath, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
        return result;
    }

    public void setMute(boolean value) throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        service.setMute(client, objectPath, value, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
    }

    /*-------------------+
     | IRendererPushHost |
     +-------------------*/

    public String hostFile(String path) throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        String result = service.hostFile(client, objectPath, path, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
        return result;
    }

    public void removeFile(String path) throws RemoteException, DLeynaException {
        IRendererService service = manager.getRendererService();
        IRendererClient client = manager.getRendererClient();
        Bundle extras = new Bundle();
        service.removeFile(client, objectPath, path, extras);
        if (extras.containsKey(Extras.KEY_ERR_MSG)) {
            throw new DLeynaException(extras.getString(Extras.KEY_ERR_MSG));
        }
    }

    /*---------------------+
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
