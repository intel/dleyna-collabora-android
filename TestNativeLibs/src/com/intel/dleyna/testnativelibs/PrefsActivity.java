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

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public class PrefsActivity extends PreferenceActivity {

    Prefs prefs = Prefs.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPreferenceScreen(createPrefsScreen());
        setDependencies(); // dependencies can only be set AFTER setPreferenceScreen()
    }

    private PreferenceScreen createPrefsScreen() {
        PreferenceScreen ps = getPreferenceManager().createPreferenceScreen(this);
        for (Tests.Enum test : Tests.Enum.values()) {
            ps.addPreference(createEnableTestPref(test, Prefs.getTestEnabledKey(test)));
        }
        return ps;
    }

    private CheckBoxPreference createEnableTestPref(Tests.Enum test, String key) {
        CheckBoxPreference pref = new CheckBoxPreference(this);
        pref.setPersistent(true);
        pref.setKey(key);
        pref.setDefaultValue(prefs.getTestEnabledValue(test));
        pref.setTitle(test.nameInLowerCase());
        pref.setSummary(test.desc);
        return pref;
    }

    private void setDependencies() {
        // Would do, e.g.:
        // somethingPref.setDependency("keyOfSomeOtherPref");
    }
}
