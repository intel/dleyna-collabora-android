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

/**
 * Notifications of events from the {@link IMediaDevice} interface of a Server.
 * <p>
 * You attach implementations of this interface to instances of {@link Server}
 * by calling {@link Server#addMediaDeviceListener(IMediaDeviceListener)}.
 * <p>
 * A default implementation of this interface is provided by {@link MediaDeviceListener}.
 * <p>
 * All event notifications will run on the application's main thread.
 */
public interface IMediaDeviceListener {

    /**
     * Notification that container ids have changed.
     * @param device the media device
     * @param containerUpdateIds the container id updates
     */
    public void onContainerUpdateIds(IMediaDevice device, ContainerUpdateId[] containerUpdateIds);

    /**
     * Notification that device properties have changed.
     * @param device the media device
     * @param changes the changes
     */
    public void onDevicePropertiesChanged(IMediaDevice device, Object[] changes);

    /**
     * Notification that an upload has finished.
     * @param device the media device
     * @param id the upload id
     * @param status the upload status: one of
     * {@link IMediaDevice#UPLOAD_CANCELLED},
     * {@link IMediaDevice#UPLOAD_COMPLETED},
     * {@link IMediaDevice#UPLOAD_ERROR}
     * @param length the number of bytes that were uploaded
     * @param total the size (bytes) of the file being uploaded
     */
    public void onUploadUpdate(IMediaDevice device, int id, String status, long length, long total);
}
