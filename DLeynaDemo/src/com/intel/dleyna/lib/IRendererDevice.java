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

import android.os.RemoteException;

/**
 * The Device portion of the Renderer API:
 * methods for obtaining information about a Renderer such as it's name, type,
 * supported formats, etc.;
 * and a method for canceling pending operations on a Renderer.
 * <p>
 * Since Renderers are usually on remote devices, and thus prone to arbitrary delays,
 * you should never invoke the methods of this interface from the UI thread.
 * Perhaps the simplest way to manage the required multi-threading is to use an
 * {@link android.os.AsyncTask}.
 */
public interface IRendererDevice {

    /**
     * Get the UPnP device type.
     * <p>
     * Example: {@code "urn:schemas-upnp-org:device:MediaRenderer:1"}.
     * @return the UPnP device type
     * @throws RemoteException no connection to the background renderer service
     * @throws DLeynaException failure reported by the background renderer service
     */
    public String getDeviceType() throws RemoteException, DLeynaException;

    /**
     * Get this Renderer's unique device name.
     * @return the unique device name
     * @throws RemoteException no connection to the background renderer service
     * @throws DLeynaException failure reported by the background renderer service
     */
    public String getUniqueDeviceName() throws RemoteException, DLeynaException;

    /**
     * Get this Renderer's friendly name.
     * @return the friendly name
     * @throws RemoteException no connection to the background renderer service
     * @throws DLeynaException failure reported by the background renderer service
     */
    public String getFriendlyName() throws RemoteException, DLeynaException;

    /**
     * Get a URL pointing to an icon that graphically identifies this Renderer.
     * @return the icon URL, or null if this property is not implemented.
     * @throws RemoteException no connection to the background renderer service
     * @throws DLeynaException failure reported by the background renderer service
     */
    public String getIconURL() throws RemoteException, DLeynaException;

    /**
     * Get the name of this Renderer's manufacturer.
     * @return the manufacturer's name
     * @throws RemoteException no connection to the background renderer service
     * @throws DLeynaException failure reported by the background renderer service
     */
    public String getManufacturer() throws RemoteException, DLeynaException;

    /**
     * Get a URL pointing to this Renderer's manufacturer's web site.
     * @return the manufacturer's URL, or null if this property is not implemented.
     * @throws RemoteException no connection to the background renderer service
     * @throws DLeynaException failure reported by the background renderer service
     */
    public String getManufacturerURL() throws RemoteException, DLeynaException;

    /**
     * Get a description of this Renderer.
     * @return the description, or null if this property is not implemented.
     * @throws RemoteException no connection to the background renderer service
     * @throws DLeynaException failure reported by the background renderer service
     */
    public String getModelDescription() throws RemoteException, DLeynaException;

    /**
     * Get this Renderer's model name.
     * @return the model name
     * @throws RemoteException no connection to the background renderer service
     * @throws DLeynaException failure reported by the background renderer service
     */
    public String getModelName() throws RemoteException, DLeynaException;

    /**
     * Get this Renderer's model number.
     * @return the model number, or null if this property is not implemented.
     * @throws RemoteException no connection to the background renderer service
     * @throws DLeynaException failure reported by the background renderer service
     */
    public String getModelNumber() throws RemoteException, DLeynaException;

    /**
     * Get this Renderer's serial number.
     * @return the serial number, or null if this property is not implemented.
     * @throws RemoteException no connection to the background renderer service
     * @throws DLeynaException failure reported by the background renderer service
     */
    public String getSerialNumber() throws RemoteException, DLeynaException;

    /**
     * Get this Renderer's presentation URL: a link to its HTML management interface.
     * @return the presentation URL, or null if this property is not implemented.
     * @throws RemoteException no connection to the background renderer service
     * @throws DLeynaException failure reported by the background renderer service
     */
    public String getPresentationURL() throws RemoteException, DLeynaException;

    /**
     * Get a string that identifies all of the file formats and network protocol combinations
     * that this Renderer supports.
     * <p>
     * The protocol info string is a comma-separated list of protocol info values.
     * Each protocol info value consists of four fields separated by colons.
     * For example,
     * <blockquote>
     * {@code "http-get:*:audio/mp4:DLNA.ORG_PN=AMR_WBplus, http-get:*:image/jpeg:DLNA.ORG_PN=JPEG_MED"}
     * </blockquote>
     * indicates that this Renderer is capable of retrieving via HTTP
     * and rendering MP4 audio files and JPEG image files.
     * <p>
     * For an extensive description of protocol info strings,
     * see <a href="http://www.upnp.org/">the UPnP Connection Manager Service Template document</a>
     * and <a href="http://www.dlna.org/">the DLNA Guidelines</a>.
     * @return the protocol info string
     * @throws RemoteException no connection to the background renderer service
     * @throws DLeynaException failure reported by the background renderer service
     */
    public String getProtocolInfo() throws RemoteException, DLeynaException;

    /**
     * Get the device icon.
     * @return the device icon.
     */
    public Icon getIcon() throws RemoteException, DLeynaException;

    /**
     * Cancel all pending method invocations this client has issued to this Renderer.
     * <p>
     * The background renderer service maintains one task queue per client per Renderer.
     * Method invocations are placed into the relevant queue when received by the service,
     * and the invocations in a given queue are executed sequentially.
     * This method cancels all invocations in the queue for this client and this Renderer.
     * The canceled invocations will fail with an appropriate error.
     * <p>
     * Note that this method cancels invocations of methods of <i>any</i> Renderer interface,
     * not just invocations of methods of this interface.
     * For example, pending invocations of methods of the {@link IRendererController}
     * interface will also be cancelled.
     * @throws RemoteException no connection to the background renderer service
     * @throws DLeynaException failure reported by the background renderer service
     */
    public void cancel() throws RemoteException, DLeynaException;
}
