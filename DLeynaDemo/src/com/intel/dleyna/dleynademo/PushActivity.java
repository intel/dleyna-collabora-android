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

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.intel.dleyna.lib.Renderer;
import com.intel.dleyna.lib.RendererManager;

/**
 * This Activity takes media content as input from an Intent,
 * and allows the user to push it to any available DMR.
 */
public class PushActivity extends Activity {

    private static final String TAG = App.TAG;

    private Prefs prefs = Prefs.getInstance();

    // Widgets.
    // (None)

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (App.LOG) Log.i(TAG, "PushActivity: onCreate");
        setContentView(R.layout.push);
    }

    protected void onStart() {
        super.onStart();
        if (App.LOG) Log.i(TAG, "PushActivity: onStart");
        rendererMgr.connect(this);
    }

    protected void onResume() {
        super.onResume();
        if (App.LOG) Log.i(TAG, "PushActivity: onResume");
     }

    protected void onPause() {
        super.onPause();
        if (App.LOG) Log.i(TAG, "PushActivity: onPause");
    }

    protected void onStop() {
        super.onStop();
        if (App.LOG) Log.i(TAG, "PushActivity: onStop");
    }

    protected void onDestroy() {
        super.onDestroy();
        if (App.LOG) Log.i(TAG, "PushActivity: onDestroy");
        rendererMgr.disconnect();
    }

    private RendererManager rendererMgr = RendererManager.getInstance(new RendererManager.Events() {

        public void onConnected() {
        }

        public void onDisconnected() {
        }

        public void onRendererFound(Renderer r) {
        }

        public void onRendererLost(Renderer r) {
        }
    });
}
