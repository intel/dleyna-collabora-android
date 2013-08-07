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

import android.os.Parcel;
import android.os.Parcelable;

/**
 * An icon: a MIME type and some bytes, with a {@link Parcelable} interface.
 */
public class Icon implements Parcelable {

    public String mimeType;
    public byte[] bytes;

    private Icon(Parcel in) {
        mimeType = in.readString();
        bytes = in.createByteArray();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mimeType);
        out.writeByteArray(bytes);
    }

    public static final Parcelable.Creator<Icon> CREATOR = new Parcelable.Creator<Icon>() {

        public Icon createFromParcel(Parcel in) {
            return new Icon(in);
        }

        public Icon[] newArray(int size) {
            return new Icon[size];
        }
    };

}
