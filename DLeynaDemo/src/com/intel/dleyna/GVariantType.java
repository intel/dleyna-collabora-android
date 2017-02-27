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

/**
 * This is a wrapper class for the native GVariantType class.
 */
public class GVariantType {

    // Basic types
    public static final char BOOLEAN     = 'b';
    public static final char UINT32      = 'u';
    public static final char INT64       = 'x';
    public static final char DOUBLE      = 'd';
    public static final char STRING      = 's';
    public static final char OBJECT_PATH = 'o';

    // Composite types.
    public static final char ARRAY       = 'a';
    public static final char TUPLE       = 'r';

    // Pointer to the native GVariantType object.
    private long peer;

    // Construct a new instance given the native peer instance.
    private GVariantType(long peer) {
        if (peer == 0) {
            throw new IllegalArgumentException();
        }
        this.peer = peer;
    }

    /**
     * Free the native peer object.
     * The user should do this as soon as the peer is no longer needed,
     * but it will be done at garbage collection time if necessary.
     */
    public void free() {
        if (peer != 0) {
            free(peer);
            peer = 0;
        }
    }

    private static native void free(long peer);

    protected void finalize() {
        free();
    }

    public static GVariantType newBasic(char basicType) {
        return new GVariantType(newBasicNative(basicType));
    }

    private static native long newBasicNative(char basicType);

    public static GVariantType newArray(GVariantType elementType) {
        return new GVariantType(newArrayNative(elementType.getPeer()));
    }

    private static native long newArrayNative(long elementType);

    public long getPeer() {
        return peer;
    }
}
