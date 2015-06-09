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

import android.os.Bundle;
import android.os.RemoteException;

/**
 * The Device portion of the Server API:
 * methods for obtaining information about a Server such as it's name, type,
 * supported formats, etc.;
 * and a method for canceling pending operations on a Server.
 * <p>
 * Since Servers are usually on remote devices, and thus prone to arbitrary delays,
 * you should never invoke the methods of this interface from the UI thread.
 * Perhaps the simplest way to manage the required multi-threading is to use an
 * {@link android.os.AsyncTask}.
 */
public interface IMediaDevice {

    /** Upload status */
    public static final String UPLOAD_CANCELLED = "CANCELLED";
    public static final String UPLOAD_COMPLETED = "COMPLETED";
    public static final String UPLOAD_ERROR = "ERROR";
    public static final String UPLOAD_IN_PROGRESS = "IN_PROGRESS";

    /**
     * Register for notification of events from this media device.
     * <p>
     * All event notifications will run on the application's main thread.
     * @param listener an instance of your implementation of {@link IMediaDeviceListener}.
     */
    public void addMediaDeviceListener(IMediaDeviceListener listener);

    /**
     * Get the Server's location.
     * @return the location
     * @throws RemoteException no connection to the background server service
     * @throws DLeynaException failure reported by the background server service
     */
    public String getLocation() throws RemoteException, DLeynaException;

    /**
     * Get this Server's unique device name.
     * @return the unique device name
     * @throws RemoteException no connection to the background server service
     * @throws DLeynaException failure reported by the background server service
     */
    public String getUniqueDeviceName() throws RemoteException, DLeynaException;

    /**
     * Get this Server's root unique device name.
     * @return the root unique device name
     * @throws RemoteException no connection to the background server service
     * @throws DLeynaException failure reported by the background server service
     */
    public String getRootUniqueDeviceName() throws RemoteException, DLeynaException;

    /**
     * Get the UPnP device type.
     * <p>
     * Example: {@code "urn:schemas-upnp-org:device:MediaServer:1"}.
     * @return the UPnP device type
     * @throws RemoteException no connection to the background server service
     * @throws DLeynaException failure reported by the background server service
     */
    public String getDeviceType() throws RemoteException, DLeynaException;

    /**
     * Get this Server's friendly name.
     * @return the friendly name
     * @throws RemoteException no connection to the background server service
     * @throws DLeynaException failure reported by the background server service
     */
    public String getFriendlyName() throws RemoteException, DLeynaException;

    /**
     * Get the name of this Server's manufacturer.
     * @return the manufacturer's name
     * @throws RemoteException no connection to the background server service
     * @throws DLeynaException failure reported by the background server service
     */
    public String getManufacturer() throws RemoteException, DLeynaException;

    /**
     * Get a URL pointing to this Server's manufacturer's web site.
     * @return the manufacturer's URL, or null if this property is not implemented.
     * @throws RemoteException no connection to the background server service
     * @throws DLeynaException failure reported by the background server service
     * @throws DLeynaUnknownPropertyException this Server does not support this optional property
     */
    public String getManufacturerUrl() throws RemoteException, DLeynaException,
            DLeynaUnknownPropertyException;

    /**
     * Get a description of this Server.
     * @return the description, or null if this property is not implemented.
     * @throws RemoteException no connection to the background server service
     * @throws DLeynaException failure reported by the background server service
     * @throws DLeynaUnknownPropertyException this Server does not support this optional property
     */
    public String getModelDescription() throws RemoteException, DLeynaException,
            DLeynaUnknownPropertyException;

    /**
     * Get this Server's model name.
     * @return the model name
     * @throws RemoteException no connection to the background server service
     * @throws DLeynaException failure reported by the background server service
     */
    public String getModelName() throws RemoteException, DLeynaException;

    /**
     * Get this Server's model number.
     * @return the model number, or null if this property is not implemented.
     * @throws RemoteException no connection to the background server service
     * @throws DLeynaException failure reported by the background server service
     * @throws DLeynaUnknownPropertyException this Server does not support this optional property
     */
    public String getModelNumber() throws RemoteException, DLeynaException,
            DLeynaUnknownPropertyException;


    /**
     * Get a URL pointing to this Server's model's web site.
     * @return the model's URL, or null if this property is not implemented.
     * @throws RemoteException no connection to the background server service
     * @throws DLeynaException failure reported by the background server service
     * @throws DLeynaUnknownPropertyException this Server does not support this optional property
     */
    public String getModelUrl() throws RemoteException, DLeynaException,
            DLeynaUnknownPropertyException;

    /**
     * Get this Server's serial number.
     * @return the serial number, or null if this property is not implemented.
     * @throws RemoteException no connection to the background server service
     * @throws DLeynaException failure reported by the background server service
     * @throws DLeynaUnknownPropertyException this Server does not support this optional property
     */
    public String getSerialNumber() throws RemoteException, DLeynaException,
            DLeynaUnknownPropertyException;

    /**
     * Get this Server's presentation URL: a link to its HTML management interface.
     * @return the presentation URL, or null if this property is not implemented.
     * @throws RemoteException no connection to the background server service
     * @throws DLeynaException failure reported by the background server service
     * @throws DLeynaUnknownPropertyException this Server does not support this optional property
     */
    public String getPresentationUrl() throws RemoteException, DLeynaException,
            DLeynaUnknownPropertyException;

    /**
     * Get a URL pointing to an icon that graphically identifies this Server.
     * @return the icon URL, or null if this property is not implemented.
     * @throws RemoteException no connection to the background server service
     * @throws DLeynaException failure reported by the background server service
     * @throws DLeynaUnknownPropertyException this Server does not support this optional property
     */
    public String getIconUrl() throws RemoteException, DLeynaException,
            DLeynaUnknownPropertyException;

    /**
     * Get the Server's device capabilities.
     * @return the device capabilities.
     * @throws RemoteException no connection to the background server service
     * @throws DLeynaException failure reported by the background server service
     * @throws DLeynaUnknownPropertyException this Server does not support this optional property
     */
    public Bundle getDlnaCaps() throws RemoteException, DLeynaException, DLeynaUnknownPropertyException;

    /**
     * Get the Server's list of property names that can be used in search queries.
     * @return the list of property names, "*" indicates any property name, an empty list indicates not supported.
     * @throws RemoteException no connection to the background server service
     * @throws DLeynaException failure reported by the background server service
     * @throws DLeynaUnknownPropertyException this Server does not support this optional property
     */
    public String[] getSearchCaps() throws RemoteException, DLeynaException, DLeynaUnknownPropertyException;

    /**
     * Get the Server's list of property names that can be used to sort Search() or Browse() results.
     * @return the list of property names, "*" indicates any property name, an empty list indicates not supported.
     * @throws RemoteException no connection to the background server service
     * @throws DLeynaException failure reported by the background server service
     * @throws DLeynaUnknownPropertyException this Server does not support this optional property
     */
    public String[] getSortCaps() throws RemoteException, DLeynaException, DLeynaUnknownPropertyException;

    /**
     * Get the Server's list of sort modifiers that can be used to sort Search() or Browse() results.
     * @return the list of sort modifiers, an empty list indicates not supported.
     * @throws RemoteException no connection to the background server service
     * @throws DLeynaException failure reported by the background server service
     * @throws DLeynaUnknownPropertyException this Server does not support this optional property
     */
    public String[] getSortExtCaps() throws RemoteException, DLeynaException, DLeynaUnknownPropertyException;

    /**
     * Get the Server's list of DMS features.
     * @return the list of DMS features.
     * @throws RemoteException no connection to the background server service
     * @throws DLeynaException failure reported by the background server service
     * @throws DLeynaUnknownPropertyException this Server does not support this optional property
     */
    public DmsFeature[] getFeatureList() throws RemoteException, DLeynaException, DLeynaUnknownPropertyException;
    
    /**
     * Get the Server's current service reset token.
     * @return the service reset token.
     * @throws RemoteException no connection to the background server service
     * @throws DLeynaException failure reported by the background server service
     * @throws DLeynaUnknownPropertyException this Server does not support this optional property
     */

    public String getServiceResetToken() throws RemoteException, DLeynaException, DLeynaUnknownPropertyException;
    
    /**
     * Get the Server's most recent system id.
     * @return the system update id.
     * @throws RemoteException no connection to the background server service
     * @throws DLeynaException failure reported by the background server service
     * @throws DLeynaUnknownPropertyException this Server does not support this optional property
     */
    public int getSystemUpdateId() throws RemoteException, DLeynaException, DLeynaUnknownPropertyException;

    /**
     * Get the Server's sleeping state.
     * @return true if the server is sleeping, false otherwise.
     * @throws RemoteException no connection to the background server service
     * @throws DLeynaException failure reported by the background server service
     * @throws DLeynaUnknownPropertyException this Server does not support this optional property
     */
    public boolean isSleeping() throws RemoteException, DLeynaException, DLeynaUnknownPropertyException;
    /**
     * Get a string that identifies all of the file formats and network protocol combinations
     * that this Server supports.
     * <p>
     * The protocol info string is a comma-separated list of protocol info values.
     * Each protocol info value consists of four fields separated by colons.
     * For example,
     * <blockquote>
     * {@code "http-get:*:audio/mp4:DLNA.ORG_PN=AMR_WBplus, http-get:*:image/jpeg:DLNA.ORG_PN=JPEG_MED"}
     * </blockquote>
     * indicates that this Server is capable of retrieving via HTTP
     * and serving MP4 audio files and JPEG image files.
     * <p>
     * For an extensive description of protocol info strings,
     * see <a href="http://www.upnp.org/">the UPnP Connection Manager Service Template document</a>
     * and <a href="http://www.dlna.org/">the DLNA Guidelines</a>.
     * @return the protocol info string
     * @throws RemoteException no connection to the background server service
     * @throws DLeynaException failure reported by the background server service
     */
    public String getProtocolInfo() throws RemoteException, DLeynaException;

    /**
     * Get the device icon.
     * @return the device icon.
     */
    public Icon getIcon() throws RemoteException, DLeynaException;

    /**
     * Cancel all pending method invocations this client has issued to this Server.
     * <p>
     * The background server service maintains one task queue per client per Server.
     * Method invocations are placed into the relevant queue when received by the service,
     * and the invocations in a given queue are executed sequentially.
     * This method cancels all invocations in the queue for this client and this Server.
     * The canceled invocations will fail with an appropriate error.
     * <p>
     * Note that this method cancels invocations of methods of <i>any</i> Server interface,
     * not just invocations of methods of this interface.
     * For example, pending invocations of methods of the {@link IServerController}
     * interface will also be cancelled.
     * @throws RemoteException no connection to the background server service
     * @throws DLeynaException failure reported by the background server service
     */
    public void cancel() throws RemoteException, DLeynaException;

    /**
     * Get the Server's contained objects.
     * @return the contained objects.
     * @throws RemoteException no connection to the background server service
     * @throws DLeynaException failure reported by the background server service
     */
    public Bundle[] browseObjects() throws RemoteException, DLeynaException;
}
