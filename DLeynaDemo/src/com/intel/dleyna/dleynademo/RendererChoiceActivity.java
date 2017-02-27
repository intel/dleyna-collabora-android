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

package com.intel.dleyna.dleynademo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

import com.intel.dleyna.lib.DLeynaException;
import com.intel.dleyna.lib.Renderer;
import com.intel.dleyna.lib.RendererManager;
import com.intel.dleyna.lib.RendererManagerListener;

/**
 * An Activity that displays a dynamic list of all currently known renderers,
 * allows the user to choose one of them, and then returns the chosen renderer
 * to the Activity that launched this one.
 */
public class RendererChoiceActivity extends ListActivity {

    private static final String TAG = App.TAG;

    /** Launch request code: choose a single renderer. */
    public static final int REQ_SINGLE = 1;

    /** Key for the renderer object path returned as an Extra */
    public static final String KEY_OBJ_PATH = "com.intel.dleyna.KeyObjPath";

    // Non-zero for testing only.
    // TODO: remove some day.
    private static final int NFAKE = 0;

    private Handler mainHandler = new Handler();
    private ExecutorService workerPool = Executors.newSingleThreadExecutor();

    private RendererManager rendererMgr;
    private RendererDope[] renderersDope;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (App.LOG) Log.i(TAG, String.format("RendererChoiceActivity: onCreate"));
        setContentView(R.layout.renderer_discovery);
        getListView().setOnItemClickListener(itemClickListener);
        connectToService();
    }

    protected void onStart() {
        super.onStart();
        if (App.LOG) Log.i(TAG, "RendererChoiceActivity: onStart");
    }

    protected void onResume() {
        super.onResume();
        if (App.LOG) Log.i(TAG, "RendererChoiceActivity: onResume");
    }

    private OnItemClickListener itemClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            if (App.LOG) Log.i(TAG, String.format("RendererChoiceActivity: onItemClick: pos=%d id=%d",
                    position, id));
            getListView().setEnabled(false);
            Renderer r = renderersDope[position].renderer;
            Intent intent = new Intent();
            intent.putExtra(KEY_OBJ_PATH, r.getObjectPath());
            setResult(Activity.RESULT_OK, intent);
            finish();        }
    };

    protected void onPause() {
        super.onPause();
        if (App.LOG) Log.i(TAG, "RendererChoiceActivity: onPause");
    }

    protected void onStop() {
        super.onStop();
        if (App.LOG) Log.i(TAG, "RendererChoiceActivity: onStop");
    }

    protected void onDestroy() {
        super.onDestroy();
        if (App.LOG) Log.i(TAG, "RendererChoiceActivity: onDestroy");
        disconnectFromService();
        workerPool.shutdown();
    }

    private void connectToService() {
        rendererMgr = new RendererManager(new RendererManagerListener() {
            public void onConnected() {
                if (App.LOG) Log.i(TAG, "RendererChoiceActivity: onConnected");
                updateList();
            }
            public void onDisconnected() {
                if (App.LOG) Log.i(TAG, "RendererChoiceActivity: onDisconnected");
            }
            public void onRendererFound(Renderer r) {
                if (App.LOG) Log.i(TAG, "RendererChoiceActivity: onRendererFound");
                updateList();
            }
            public void onRendererLost(Renderer r) {
                if (App.LOG) Log.i(TAG, "RendererChoiceActivity: onRendererLost");
                updateList();
            }
        });
        rendererMgr.connect(this);
    }

    private void updateList() {
        // We're on the UI thread.
        workerPool.execute(new Runnable() {
            public void run() {
                // We're on a worker thread.
                try {
                    Renderer[] rv = rendererMgr.getRenderers();
                    final RendererDope[] rdv = new RendererDope[rv.length + NFAKE];
                    for (int i=0; i < rv.length; i++) {
                        rdv[i] = new RendererDope();
                        rdv[i].renderer = rv[i];
                        rdv[i].friendlyName = rv[i].getFriendlyName();
                    }
                    for (int i=0; i < NFAKE; i++) {
                        rdv[rv.length + i] = new RendererDope();
                        rdv[rv.length + i].renderer = null;
                        rdv[rv.length + i].friendlyName = "Fake Renderer " + i;
                    }
                    mainHandler.post(new Runnable() {
                        public void run() {
                            // We're on the UI thread.
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                    RendererChoiceActivity.this,
                                    android.R.layout.simple_list_item_1);
                            for (int i=0; i < rdv.length; i++) {
                                adapter.add(rdv[i].friendlyName);
                            }
                            RendererChoiceActivity.this.setListAdapter(adapter);
                            renderersDope = rdv;
                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (DLeynaException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void disconnectFromService() {
        if (rendererMgr != null) {
            rendererMgr.disconnect();
        }
    }

    private static class RendererDope {
        public Renderer renderer;
        public String friendlyName;
    }
}
