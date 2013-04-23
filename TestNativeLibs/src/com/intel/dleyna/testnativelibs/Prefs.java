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

package com.intel.dleyna.testnativelibs;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * Persistent preferences for this application.
 * <p>
 * Use {@link #getInstance()} to obtain the singleton instance of this class.
 * <p>
 * Use {@link #getTestEnabledKey(Tests.Enum)} to get the preference key of a particular test.
 * <p>
 * Use {@link #getTestEnabledValue(Tests.Enum)} to get the preference value of a particular test.
 */
public class Prefs {

    private static final String KEY_ENABLED = "ENABLED_";

    private static final boolean DEFAULT_ENABLED = true;

    private static Prefs instance = null;

    // Shared preferences instance that is unique for this application.
    private SharedPreferences sp;

    /** Return the singleton instance. */
    public static Prefs getInstance() {
        if (instance == null) {
            instance = new Prefs();
            instance.sp = PreferenceManager.getDefaultSharedPreferences(App.getInstance().getApplicationContext());
        }
        return instance;
    }

    // Hide the default constructor.
    private Prefs() {
    }

    /** Get the preference key of the given test. */
    public static String getTestEnabledKey(Tests.Enum test) {
        return KEY_ENABLED + test.name();
    }

    public boolean getTestEnabledValue(Tests.Enum test) {
        return sp.getBoolean(getTestEnabledKey(test), DEFAULT_ENABLED);
    }

    public void setAllTestEnabledValues(boolean value) {
        Editor editor = sp.edit();
        for (Tests.Enum test : Tests.Enum.values()) {
            editor.putBoolean(getTestEnabledKey(test), value);
        }
        editor.commit();
    }
}

