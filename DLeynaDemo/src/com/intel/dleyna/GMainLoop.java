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
 * This class provides a way to schedule {@link java.lang.Runnable} objects to be
 * executed on a g_main_loop.
 * <p>
 * Create an instance of this class by calling the constructor {@link #GMainLoop()},
 * and then use {@link #idleAdd(Runnable)} to schedule a runnable object.
 * You should use {@link #free()} to reclaim resources when done.
 */
public class GMainLoop {

    private long peer;

    /**
     * This constructor MUST be called on the thread that runs the g_main_loop.
     */
    public GMainLoop() {
        long peer = allocNative();
        if (peer == 0) {
            throw new OutOfMemoryError();
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
            freeNative(peer);
            peer = 0;
        }
    }

    protected void finalize() {
        free();
    }

    public void idleAdd(Runnable r) {
        idleAddNative(peer, r);
    }

    private native long allocNative();

    private native long freeNative(long peer);

    private native void idleAddNative(long peer, Runnable r);
}
