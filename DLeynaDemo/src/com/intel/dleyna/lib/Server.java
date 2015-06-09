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
 */

package com.intel.dleyna.lib;

import java.util.LinkedList;
import java.util.List;

import android.os.Bundle;
import android.os.RemoteException;

/**
 * Instances of this class correspond to Digital Media Servers
 * on local networks attached to this device (or on the device itself).
 * <p>
 * The methods of this class have been partitioned into three interfaces
 * that represent three semi-independent aspects of a media server:
 * {@link IMediaDevice}, {@link IMediaContainer2}, and {@link IServerController}.
 * <p>
 * Use the {@link ServerManager} to discover instances of this class.
 * <p>
 * The connection to the background server service that you initiate with
 * {@link ServerManager#connect(android.content.Context)} must be established for the methods of this
 * class to succeed. Otherwise, they will throw a {@link RemoteException}.
 * <p>
 * Since Servers are usually on remote devices, and thus prone to arbitrary delays,
 * you should never invoke methods that interact with Servers from the UI thread.
 * Perhaps the simplest way to manage the required multi-threading is to use an
 * {@link android.os.AsyncTask}.
 */
public class Server implements IMediaDevice, IMediaContainer2, IServerController {

    /** Our manager. */
    private ServerManager manager;

    /** Identifies this server to the background server service. */
    private final String objectPath;

    private List<IServerControllerListener> controllerListeners = new LinkedList<IServerControllerListener>();
    private List<IMediaDeviceListener> mediaDeviceListeners = new LinkedList<IMediaDeviceListener>();

    /** The package-visible constructor */
    Server(ServerManager manager, String objectPath) {
        this.manager = manager;
        this.objectPath = objectPath;
    }

    List<IServerControllerListener> getControllerListeners() {
        return controllerListeners;
    }

    List<IMediaDeviceListener> getMediaDeviceListeners() {
        return mediaDeviceListeners;
    }

    public String getObjectPath() {
        return objectPath;
    }

    /*--------------+
     | IMediaDevice |
     +--------------*/

    public void addMediaDeviceListener(IMediaDeviceListener listener) {
        mediaDeviceListeners.add(listener);
    }

    public String getLocation() throws RemoteException, DLeynaException {
        IServerService service = manager.getServerService();
        IServerClient client = manager.getServerClient();
        Bundle extras = new Bundle();
        String result = service.getLocation(client, objectPath, extras);
        Extras.throwExceptionIfError(extras);
        return result;
    }

    public String getUniqueDeviceName() throws RemoteException, DLeynaException {
        IServerService service = manager.getServerService();
        IServerClient client = manager.getServerClient();
        Bundle extras = new Bundle();
        String result = service.getUniqueDeviceName(client, objectPath, extras);
        Extras.throwExceptionIfError(extras);
        return result;
    }

    public String getRootUniqueDeviceName() throws RemoteException, DLeynaException {
        IServerService service = manager.getServerService();
        IServerClient client = manager.getServerClient();
        Bundle extras = new Bundle();
        String result = service.getRootUniqueDeviceName(client, objectPath, extras);
        Extras.throwExceptionIfError(extras);
        return result;
    }

    public String getDeviceType() throws RemoteException, DLeynaException {
        IServerService service = manager.getServerService();
        IServerClient client = manager.getServerClient();
        Bundle extras = new Bundle();
        String result = service.getDeviceType(client, objectPath, extras);
        Extras.throwExceptionIfError(extras);
        return result;
    }

    public String getFriendlyName() throws RemoteException, DLeynaException {
        IServerService service = manager.getServerService();
        IServerClient client = manager.getServerClient();
        Bundle extras = new Bundle();
        String result = service.getFriendlyName(client, objectPath, extras);
        Extras.throwExceptionIfError(extras);
        return result;
    }

    public String getManufacturer() throws RemoteException, DLeynaException {
        IServerService service = manager.getServerService();
        IServerClient client = manager.getServerClient();
        Bundle extras = new Bundle();
        String result = service.getManufacturer(client, objectPath, extras);
        Extras.throwExceptionIfError(extras);
        return result;
    }

    public String getManufacturerUrl() throws RemoteException, DLeynaException {
        IServerService service = manager.getServerService();
        IServerClient client = manager.getServerClient();
        Bundle extras = new Bundle();
        String result = service.getManufacturerUrl(client, objectPath, extras);
        Extras.throwExceptionIfError(extras);
        return result;
    }

    public String getModelDescription() throws RemoteException, DLeynaException {
        IServerService service = manager.getServerService();
        IServerClient client = manager.getServerClient();
        Bundle extras = new Bundle();
        String result = service.getModelDescription(client, objectPath, extras);
        Extras.throwExceptionIfError(extras);
        return result;
    }

    public String getModelName() throws RemoteException, DLeynaException {
        IServerService service = manager.getServerService();
        IServerClient client = manager.getServerClient();
        Bundle extras = new Bundle();
        String result = service.getModelName(client, objectPath, extras);
        Extras.throwExceptionIfError(extras);
        return result;
    }

    public String getModelNumber() throws RemoteException, DLeynaException {
        IServerService service = manager.getServerService();
        IServerClient client = manager.getServerClient();
        Bundle extras = new Bundle();
        String result = service.getModelNumber(client, objectPath, extras);
        Extras.throwExceptionIfError(extras);
        return result;
    }

    public String getModelUrl() throws RemoteException, DLeynaException {
        IServerService service = manager.getServerService();
        IServerClient client = manager.getServerClient();
        Bundle extras = new Bundle();
        String result = service.getModelUrl(client, objectPath, extras);
        Extras.throwExceptionIfError(extras);
        return result;
    }

    public String getSerialNumber() throws RemoteException, DLeynaException {
        IServerService service = manager.getServerService();
        IServerClient client = manager.getServerClient();
        Bundle extras = new Bundle();
        String result = service.getSerialNumber(client, objectPath, extras);
        Extras.throwExceptionIfError(extras);
        return result;
    }

    public String getPresentationUrl() throws RemoteException, DLeynaException {
        IServerService service = manager.getServerService();
        IServerClient client = manager.getServerClient();
        Bundle extras = new Bundle();
        String result = service.getPresentationUrl(client, objectPath, extras);
        Extras.throwExceptionIfError(extras);
        return result;
    }

    public String getIconUrl() throws RemoteException, DLeynaException {
        IServerService service = manager.getServerService();
        IServerClient client = manager.getServerClient();
        Bundle extras = new Bundle();
        String result = service.getIconUrl(client, objectPath, extras);
        Extras.throwExceptionIfError(extras);
        return result;
    }

    public int getSystemUpdateId() throws RemoteException, DLeynaException {
        IServerService service = manager.getServerService();
        IServerClient client = manager.getServerClient();
        Bundle extras = new Bundle();
        int result = service.getSystemUpdateId(client, objectPath, extras);
        Extras.throwExceptionIfError(extras);
        return result;
    }

    public boolean isSleeping() throws RemoteException, DLeynaException {
        IServerService service = manager.getServerService();
        IServerClient client = manager.getServerClient();
        Bundle extras = new Bundle();
        boolean result = service.isSleeping(client, objectPath, extras);
        Extras.throwExceptionIfError(extras);
        return result;
    }

    public Bundle getDlnaCaps() throws RemoteException, DLeynaException {
        IServerService service = manager.getServerService();
        IServerClient client = manager.getServerClient();
        Bundle extras = new Bundle();
        Bundle result = service.getDlnaCaps(client, objectPath, extras);
        Extras.throwExceptionIfError(extras);
        return result;
    }

    public String[] getSearchCaps() throws RemoteException, DLeynaException {
        IServerService service = manager.getServerService();
        IServerClient client = manager.getServerClient();
        Bundle extras = new Bundle();
        String[] result = service.getSearchCaps(client, objectPath, extras);
        Extras.throwExceptionIfError(extras);
        return result;
    }

    public String[] getSortCaps() throws RemoteException, DLeynaException {
        IServerService service = manager.getServerService();
        IServerClient client = manager.getServerClient();
        Bundle extras = new Bundle();
        String[] result = service.getSortCaps(client, objectPath, extras);
        Extras.throwExceptionIfError(extras);
        return result;
    }

    public String[] getSortExtCaps() throws RemoteException, DLeynaException {
        IServerService service = manager.getServerService();
        IServerClient client = manager.getServerClient();
        Bundle extras = new Bundle();
        String result[] = service.getSortExtCaps(client, objectPath, extras);
        Extras.throwExceptionIfError(extras);
        return result;
    }

    public DmsFeature[] getFeatureList() throws RemoteException, DLeynaException {
        IServerService service = manager.getServerService();
        IServerClient client = manager.getServerClient();
        Bundle extras = new Bundle();
        DmsFeature[] result = service.getFeatureList(client, objectPath, extras);
        Extras.throwExceptionIfError(extras);
        return result;
    }

    public String getServiceResetToken() throws RemoteException, DLeynaException {
        IServerService service = manager.getServerService();
        IServerClient client = manager.getServerClient();
        Bundle extras = new Bundle();
        String result = service.getServiceResetToken(client, objectPath, extras);
        Extras.throwExceptionIfError(extras);
        return result;
    }

    public String getProtocolInfo() throws RemoteException, DLeynaException {
        IServerService service = manager.getServerService();
        IServerClient client = manager.getServerClient();
        Bundle extras = new Bundle();
        String result = service.getProtocolInfo(client, objectPath, extras);
        Extras.throwExceptionIfError(extras);
        return result;
    }

    public Icon getIcon() throws RemoteException, DLeynaException {
        IServerService service = manager.getServerService();
        IServerClient client = manager.getServerClient();
        Bundle extras = new Bundle();
        Icon result = service.getIcon(client, objectPath, extras);
        Extras.throwExceptionIfError(extras);
        return result;
    }

    public void cancel() throws RemoteException, DLeynaException {
        IServerService service = manager.getServerService();
        IServerClient client = manager.getServerClient();
        Bundle extras = new Bundle();
        service.cancel(client, objectPath, extras);
        Extras.throwExceptionIfError(extras);
    }

    public Bundle[] browseObjects() throws RemoteException, DLeynaException {
        IServerService service = manager.getServerService();
        IServerClient client = manager.getServerClient();
        Bundle extras = new Bundle();
        Bundle[] result = service.browseObjects(client, objectPath, extras);
        Extras.throwExceptionIfError(extras);
        return result;
    }

    // TODO: ?

    /*------------------+
     | IMediaContainer2 |
     +------------------*/

    public Bundle[] listChildrenEx() throws RemoteException, DLeynaException {
        IServerService service = manager.getServerService();
        IServerClient client = manager.getServerClient();
        Bundle extras = new Bundle();
        Bundle[] result = service.listChildrenEx(client, objectPath, extras);
        Extras.throwExceptionIfError(extras);
        return result;
    }

    public Bundle[] searchObjectsEx() throws RemoteException, DLeynaException {
        IServerService service = manager.getServerService();
        IServerClient client = manager.getServerClient();
        Bundle extras = new Bundle();
        Bundle[] result = service.searchObjectsEx(client, objectPath, extras);
        Extras.throwExceptionIfError(extras);
        return result;
    }

    // TODO: ?

    /*---------------+
     | IMediaObject2 |
     +---------------*/

    public String getMetadata() throws RemoteException, DLeynaException {
        IServerService service = manager.getServerService();
        IServerClient client = manager.getServerClient();
        Bundle extras = new Bundle();
        String result = service.getMetadata(client, objectPath, extras);
        Extras.throwExceptionIfError(extras);
        return result;
    }

    // TODO: ?

    /*-------------------+
     | IServerController |
     +-------------------*/

    public void addControllerListener(IServerControllerListener listener) {
        controllerListeners.add(listener);
    }

    // TODO: ?
    
   /*-------------+
    | IServerDemo |
    +-------------*/

    public Bundle[] getChildren(String objectPath) throws RemoteException, DLeynaException {
        IServerService service = manager.getServerService();
        IServerClient client = manager.getServerClient();
        Bundle extras = new Bundle();
        Bundle[] result = service.getChildren(client, objectPath, extras);
        Extras.throwExceptionIfError(extras);
        return result;
    }

    // TODO: ?
    
    /*-------------------+
     | For ServerManager |
     +-------------------*/

    private boolean used;

    boolean getIsObsolete() {
        return this.used;
    }

    void setIsObsolete(boolean used) {
        this.used = used;
    }
}
