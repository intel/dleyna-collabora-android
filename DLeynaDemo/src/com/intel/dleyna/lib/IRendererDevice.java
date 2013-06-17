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
 * The Device portion of the Renderer API.
 */
public interface IRendererDevice {

    /**
     * Get the UPnP device type.
     * <p>
     * Example: {@code "urn:schemas-upnp-org:device:MediaRenderer:1"}.
     * @return the UPnP device type
     * @throws RemoteException no connection to the background renderer service
     */
    public String getDeviceType() throws RemoteException;

    /**
     * Get this renderer's unique device name.
     * @return the unique device name
     * @throws RemoteException no connection to the background renderer service
     */
    public String getUniqueDeviceName() throws RemoteException;

    /**
     * Get this renderer's friendly name.
     * @return the friendly name
     * @throws RemoteException no connection to the background renderer service
     */
    public String getFriendlyName() throws RemoteException;

    /**
     * Get a URL pointing to an icon that graphically identifies this renderer.
     * @return the icon URL, or null if this property is not implemented.
     * @throws RemoteException no connection to the background renderer service
     */

    public String getIconURL() throws RemoteException;
    /**
     * Get the name of this renderer's manufacturer.
     * @return the manufacturer's name
     * @throws RemoteException no connection to the background renderer service
     */
    public String getManufacturer() throws RemoteException;

    /**
     * Get a URL pointing to this renderer's manufacturer's web site.
     * @return the manufacturer's URL, or null if this property is not implemented.
     * @throws RemoteException no connection to the background renderer service
     */
    public String getManufacturerURL() throws RemoteException;

    /**
     * Get a description of this renderer.
     * @return the description, or null if this property is not implemented.
     * @throws RemoteException no connection to the background renderer service
     */
    public String getModelDescription() throws RemoteException;

    /**
     * Get this renderer's model name.
     * @return the model name
     * @throws RemoteException no connection to the background renderer service
     */
    public String getModelName() throws RemoteException;

    /**
     * Get this renderer's model number.
     * @return the model number, or null if this property is not implemented.
     * @throws RemoteException no connection to the background renderer service
     */
    public String getModelNumber() throws RemoteException;

    /**
     * Get this renderer's serial number.
     * @return the serial number, or null if this property is not implemented.
     * @throws RemoteException no connection to the background renderer service
     */
    public String getSerialNumber() throws RemoteException;

    /**
     * Get this renderer's presentation URL: a link to its HTML management interface.
     * @return the presentation URL, or null if this property is not implemented.
     * @throws RemoteException no connection to the background renderer service
     */
    public String getPresentationURL() throws RemoteException;

    /**
     * Get a string that identifies all of the file formats and network protocol combinations
     * that this renderer supports.
     * <p>
     * The protocol info string is a comma-separated list of protocol info values.
     * Each protocol info value consists of four fields separated by colons.
     * For example,
     * <blockquote>
     * {@code "http-get:*:audio/mp4:DLNA.ORG_PN=AMR_WBplus, http-get:*:image/jpeg:DLNA.ORG_PN=JPEG_MED"}
     * </blockquote>
     * indicates that this renderer is capable of retrieving via HTTP
     * and rendering MP4 audio files and JPEG image files.
     * <p>
     * For an extensive description of protocol info strings,
     * see <a href="http://www.upnp.org/">the UPnP Connection Manager Service Template document</a>
     * and <a href="http://www.dlna.org/">the DLNA Guidelines</a>.
     * @return the protocol info string
     * @throws RemoteException no connection to the background renderer service
     */
    public String getProtocolInfo() throws RemoteException;

    /**
     * Get the device icon.
     * @return the device icon.
     */
    public Icon getIcon() throws RemoteException;

    /**
     * Cancel all outstanding commands this client has issued to this renderer.
     * <p>
     * The background renderer service maintains one task queue per client per renderer.
     * Commands are placed into the relevant queue when received by the service,
     * and the commands in a given queue are executed sequentially.
     * This method cancels all commands in the queue for this client and this renderer.
     * The canceled commands will fail with an appropriate error.
     * @throws RemoteException no connection to the background renderer service
     */
    public void cancel() throws RemoteException;
}