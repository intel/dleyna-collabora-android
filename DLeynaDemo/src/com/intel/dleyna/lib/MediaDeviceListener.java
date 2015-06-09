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
import android.util.Log;

/**
 * A default implementation of {@link IMediaDeviceListener} whose
 * methods do nothing.
 * <p>
 * If you are only interested in a subset of the events of the interface,
 * you may find it easier to extend this class than to implement the entire interface.
 */
public class MediaDeviceListener implements IMediaDeviceListener {

    public void onContainerUpdateIds(IMediaDevice device, ContainerUpdateId[] containerUpdateIds) {
    }

    public void onDevicePropertiesChanged(IMediaDevice device, Object[] changes) {
    }

    public void onUploadUpdate(IMediaDevice device, int id, String status, long length, long total) {
    }
}
