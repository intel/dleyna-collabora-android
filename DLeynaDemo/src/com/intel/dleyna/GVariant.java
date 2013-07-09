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

package com.intel.dleyna;

/**
 * This is a wrapper class for the native GVariant class.
 * <p>
 * Each instance of this class holds a private reference to
 * an instance of the native GVariant class.
 * <p>
 * To construct an instance of this class,
 * use one of the static newXxx() methods.
 * <p>
 * To convert an instance of this class to a Java value,
 * use one of the getXxx() methods.
 */
public class GVariant {

    // Pointer to the native GVariant object
    private long peer;

    /**
     * Construct a new instance given the native instance.
     * @param peer the native instance
     */
    public GVariant(long peer) {
        if (peer == 0) {
            throw new IllegalArgumentException();
        }
        refSink(peer);
        this.peer = peer;
    }

    /**
     * Free the native peer object.
     * The user should do this as soon as the peer is no longer needed,
     * but it will be done at garbage collection time if necessary.
     */
    public void free() {
        if (peer != 0) {
            unref(peer);
            peer = 0;
        }
    }

    private void unref() {
        unref(peer);
    }

    private static native void refSink(long peer);

    private static native void unref(long peer);

    protected void finalize() {
        free();
    }

    /**
     * Construct a new instance of type boolean.
     * @param value the boolean value
     * @return the new instance
     */
    public static GVariant newBoolean(boolean value) {
        return new GVariant(newBooleanNative(value));
    }

    private static native long newBooleanNative(boolean value);

    /**
     * Construct a new instance of type uint32.
     * @param value the uint32 value
     * @return the new instance
     */
    public static GVariant newUInt32(int value) {
        return new GVariant(newUInt32Native(value));
    }

    private static native long newUInt32Native(int value);

    /**
     * Construct a new instance of type double.
     * @param value the double value
     * @return the new instance
     */
    public static GVariant newDouble(double value) {
        return new GVariant(newDoubleNative(value));
    }

    private static native long newDoubleNative(double value);

    /**
     * Construct a new instance of type string.
     * @param value the string value
     * @return the new instance
     */
    public static GVariant newString(String value) {
        return new GVariant(newStringNative(value));
    }

    private static native long newStringNative(String value);

    /**
     * Construct a new instance of type object-path (which is a string in a particular format).
     * @param value the object_path value
     * @return the new instance
     */
    public static GVariant newObjectPath(String value) {
        return new GVariant(newObjectPathNative(value));
    }

    private static native long newObjectPathNative(String value);

    /**
     * Construct a new instance of type array-of-double.
     * @param value the array-of-double value
     * @return the new instance
     */
    public static GVariant newArrayOfDouble(double[] value) {
        GVariant[] gva = new GVariant[value.length];
        for (int i = 0; i < value.length; i++) {
            gva[i] = newDouble(value[i]);
        }
        return newArray(gva, GVariantType.newBasic(GVariantType.DOUBLE));
    }

    /**
     * Construct a new instance of type array-of-string.
     * @param value the array-of-string value
     * @return the new instance
     */
    public static GVariant newArrayOfString(String[] value) {
        GVariant[] gva = new GVariant[value.length];
        for (int i = 0; i < value.length; i++) {
            gva[i] = newString(value[i]);
        }
        return newArray(gva, GVariantType.newBasic(GVariantType.STRING));
    }


    /**
     * Construct a new instance of type array-of-object-path.
     * @param value the array-of-object-path value
     * @return the new instance
     */
    public static GVariant newArrayOfObjectPath(String[] value) {
        GVariant[] gva = new GVariant[value.length];
        for (int i = 0; i < value.length; i++) {
            gva[i] = newObjectPath(value[i]);
        }
        return newArray(gva, GVariantType.newBasic(GVariantType.OBJECT_PATH));
    }

    private static GVariant newArray(GVariant[] gva, GVariantType elemType) {
        GVariant result = new GVariant(newArrayNative(gva, elemType.getPeer()));
        for (int i = 0; i < gva.length; i++) {
            gva[i].free();
        }
        elemType.free();
        return result;
    }

    private static native long newArrayNative(GVariant[] gva, long elemType);

    /**
     * @return the value of this object, which must be of type boolean.
     */
    public boolean getBoolean() {
        return getBooleanNative(peer);
    }

    private static native boolean getBooleanNative(long peer);

    /**
     * @return the value of this object, which must be of type uint32.
     */
    public int getUInt32() {
        return getUInt32Native(peer);
    }

    private static native int getUInt32Native(long peer);

    /**
     * @return the value of this object, which must be of type double.
     */
    public double getDouble() {
        return getDoubleNative(peer);
    }

    private static native double getDoubleNative(long peer);

    /**
     * @return the value of this object, which must be of type string or object-path.
     */
    public String getString() {
        return getStringNative(peer);
    }

    private static native String getStringNative(long peer);

    /**
     * @return the value of this object, which must be of type array-of-double.
     */
    public double[] getArrayOfDouble() {
        return getArrayOfDoubleNative(peer);
    }

    private static native double[] getArrayOfDoubleNative(long peer);

    /**
     * @return the value of this object, which must be of type array-of-string
     * or array-of-object-path.
     */
    public String[] getArrayOfString() {
        return getArrayOfStringNative(peer);
    }

    private static native String[] getArrayOfStringNative(long peer);

    /**
     * Return the the child at the given index, à la g_variant_get_child_value().
     * <p>
     * This GVariant object must be a container (variant, maybe, array, tuple, or dictionary).
     * @param index index of the child to return
     * @return the child at the given index
     */
    public GVariant getChildValue(int index) {
        GVariant child = new GVariant(getChildValueNative(peer, index));
        // TODO: verify this: I think the ref count got bumped when we obtained it by
        // g_variant_get_child_value(). It gets bumped again in our new GVariant().
        // So we have to decrement the ref count here.
        child.unref();
        return child;
    }

    private static native long getChildValueNative(long peer, int index);
}
