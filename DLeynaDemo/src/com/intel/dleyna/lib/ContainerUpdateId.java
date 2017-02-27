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
 */

package com.intel.dleyna.lib;

import android.os.Parcel;
import android.os.Parcelable;


/** Container Update Ids */
public class ContainerUpdateId implements Parcelable {
    String objectPath;
    int id;

    public ContainerUpdateId(String objectPath, int id) {
        super();
        this.objectPath = objectPath;
        this.id = id;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(objectPath);
        out.writeInt(id);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("objectPath=").append(objectPath);
        sb.append(", id=").append(id);
        return sb.toString();
    }

    public static final Parcelable.Creator<ContainerUpdateId> CREATOR
            = new Parcelable.Creator<ContainerUpdateId>() {
        public ContainerUpdateId createFromParcel(Parcel in) {
            return new ContainerUpdateId(in);
        }

        public ContainerUpdateId[] newArray(int size) {
            return new ContainerUpdateId[size];
        }
    };
    
    private ContainerUpdateId(Parcel in) {
        objectPath = in.readString();
        id = in.readInt();
    }
}
