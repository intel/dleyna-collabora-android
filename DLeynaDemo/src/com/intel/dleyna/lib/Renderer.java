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
    }

    public void previous() throws RemoteException, DLeynaException {
    }

    public void pause() throws RemoteException, DLeynaException {
    }

    public void playPause() throws RemoteException, DLeynaException {
    }

    public void stop() throws RemoteException, DLeynaException {
    }

    public void play() throws RemoteException, DLeynaException {
    }

    public void seek(long offset) throws RemoteException, DLeynaException {
    }

    public void setPosition(long position) throws RemoteException, DLeynaException {
    }

    public void openUri(String uri) throws RemoteException, DLeynaException {
    }

    public String getPlaybackStatus() throws RemoteException, DLeynaException {
        return null;
    }

    public double getRate() throws RemoteException, DLeynaException {
        return 0;
    }

    public void setRate(double rate) throws RemoteException, DLeynaException {
    }

    public Bundle getMetadata() throws RemoteException, DLeynaException {
        return null;
    }

    public double getVolume() throws RemoteException, DLeynaException {
        return 0;
    }

    public void setVolume(double volume) throws RemoteException, DLeynaException {
    }

    public long getPosition() throws RemoteException, DLeynaException {
        return 0;
    }

    public long getMinimumRate() throws RemoteException, DLeynaException {
        return 0;
    }

    public long getMaximumRate() throws RemoteException, DLeynaException {
        return 0;
    }

    public boolean getCanGoNext() throws RemoteException, DLeynaException {
        return false;
    }

    public boolean getCanGoPrevious() throws RemoteException, DLeynaException {
        return false;
    }

    public boolean getCanPlay() throws RemoteException, DLeynaException {
        return false;
    }

    public boolean getCanPause() throws RemoteException, DLeynaException {
        return false;
    }

    public boolean getCanSeek() throws RemoteException, DLeynaException {
        return false;
    }

    public boolean getCanControl() throws RemoteException, DLeynaException {
        return false;
    }

    public int getNumberOfTracks() throws RemoteException, DLeynaException {
        return 0;
    }

    public void goToTrack(int track) throws RemoteException, DLeynaException {
    }

    public int getCurrentTrack() throws RemoteException, DLeynaException {
        return 0;
    }

    public void openUriEx(String uri, String metadata) throws RemoteException, DLeynaException {
    }

    public double[] getTransportPlaySpeeds() throws RemoteException, DLeynaException {
        return null;
    }

    public boolean getMute() throws RemoteException, DLeynaException {
        return false;
    }

    public void setMute(boolean value) throws RemoteException, DLeynaException {
    }

    /*-------------------+
     | IRendererPushHost |
     +-------------------*/

    public String hostFile(String path) throws RemoteException, DLeynaException {
        return null;
    }

    public void removeFile(String path) throws RemoteException, DLeynaException {
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
