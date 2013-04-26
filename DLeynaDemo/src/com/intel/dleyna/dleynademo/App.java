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

package com.intel.dleyna.dleynademo;

import android.app.Application;
import android.util.Log;

public class App extends Application {

    /** Whether to log. */
    public static final boolean LOG = true;

    /** Tag for logging. */
    public static final String TAG = "DLeynaDemo";

    /** The singleton instance. */
    private static App instance;

    /** Return the singleton instance of this App. */
    public static App getInstance() {
        return instance;
    }

    public void onCreate() {
        if (LOG) Log.i(App.TAG, "App: onCreate: " + this);
        super.onCreate();
        instance = this;
    }
}
