/*
 * dLeyna
 *
 * Copyright (C) 2013-2017 Intel Corporation. All rights reserved.
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

package com.intel.dleyna;

import java.util.HashMap;

import android.util.SparseArray;

/**
 * Everything you never wanted to know about a remote object.
 */
public class RemoteObject {
    public final int id;
    public final String objectPath;
    public final boolean isRoot;
    public final String ifaceName;
    public final long dispatchCb;

    public RemoteObject(int id, String objectPath, boolean isRoot, String ifaceName,long dispatchCb) {
        this.id = id;
        this.objectPath = objectPath;
        this.isRoot = isRoot;
        this.ifaceName = ifaceName;
        this.dispatchCb = dispatchCb;
    }

    /**
     * A set of remote objects, searchable by either integer id or (objectPath, ifaceName).
     * All methods are thread-safe.
     */
    public static class Store {

        private SparseArray<RemoteObject> objectsById = new SparseArray<RemoteObject>();

        private HashMap<String,RemoteObject> objectsByName = new HashMap<String,RemoteObject>();

        public synchronized void add(RemoteObject ro) {
            objectsById.append(ro.id, ro);
            objectsByName.put(ro.ifaceName + ro.objectPath, ro);
        }

        public synchronized void remove(int id) {
            RemoteObject ro = objectsById.get(id);
            if (ro != null) {
                objectsById.delete(id);
                objectsByName.remove(ro.ifaceName + ro.objectPath);
            }
        }

        public synchronized RemoteObject getById(int id) {
            return objectsById.get(id);
        }

        public synchronized RemoteObject getByName(String objectPath, String ifaceName) {
            return objectsByName.get(ifaceName + objectPath);
        }
    }
}
