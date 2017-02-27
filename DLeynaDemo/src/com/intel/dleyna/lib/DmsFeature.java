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


/** DMS Feature */
public class DmsFeature implements Parcelable {
    String name;
    String version;
    String[] objectPaths;

    public DmsFeature(String name, String version, String[] objectPaths) {
        super();
        this.name = name;
        this.version = version;
        this.objectPaths = objectPaths;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(version);
        out.writeStringArray(objectPaths);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("name=").append(name);
        sb.append(", version=").append(version);
        sb.append("objectPaths=").append(objectPaths);
        return sb.toString();
    }

    public static final Parcelable.Creator<DmsFeature> CREATOR
            = new Parcelable.Creator<DmsFeature>() {
        public DmsFeature createFromParcel(Parcel in) {
            return new DmsFeature(in);
        }

        public DmsFeature[] newArray(int size) {
            return new DmsFeature[size];
        }
    };
    
    private DmsFeature(Parcel in) {
        name = in.readString();
        version = in.readString();
        objectPaths = in.createStringArray();
        in.readStringArray(objectPaths);
    }
}
