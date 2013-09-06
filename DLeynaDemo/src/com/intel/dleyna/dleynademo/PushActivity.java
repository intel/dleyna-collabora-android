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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
 * This Activity takes media content as input from an Intent, and allows the
 * user to push it to any available DMR.
 */
public class PushActivity extends ListActivity {

    private static final String TAG = App.TAG;

    private Handler mainHandler;
    private ExecutorService workerPool;
    private RendererManager rendererMgr;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (App.LOG) Log.i(TAG, "PushActivity: onCreate");
        setContentView(R.layout.push);
        getListView().setOnItemClickListener(itemClickListener);
        mainHandler = new Handler(Looper.getMainLooper());
        workerPool = Executors.newSingleThreadExecutor();
    }

    protected void onStart() {
        super.onStart();
        if (App.LOG) Log.i(TAG, "PushActivity: onStart");
    }

    protected void onResume() {
        super.onResume();
        if (App.LOG) Log.i(TAG, "PushActivity: onResume");
        rendererMgr = new RendererManager(new RendererManagerListener() {
            public void onConnected() {
                updateList();
            }
            public void onDisconnected() {
            }
            public void onRendererFound(Renderer r) {
                updateList();
            }
            public void onRendererLost(Renderer r) {
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
                    RendererDope[] rdv = new RendererDope[rv.length];
                    for (int i=0; i < rv.length; i++) {
                        rdv[i] = new RendererDope();
                        rdv[i].renderer = rv[i];
                        rdv[i].friendlyName = rv[i].getFriendlyName();
                    }
                    final RendererDope[] rdvFinal = rdv;
                    mainHandler.post(new Runnable() {
                        public void run() {
                            // We're on the main thread.
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                    PushActivity.this,
                                    android.R.layout.simple_list_item_1);
                            for (int i=0; i < rdvFinal.length; i++) {
                                adapter.add(rdvFinal[i].friendlyName);
                            }
                            PushActivity.this.setListAdapter(adapter);
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

    protected void onPause() {
        super.onPause();
        if (App.LOG) Log.i(TAG, "PushActivity: onPause");
        rendererMgr.disconnect();
        rendererMgr = null;
    }

    protected void onStop() {
        super.onStop();
        if (App.LOG) Log.i(TAG, "PushActivity: onStop");
    }

    protected void onDestroy() {
        super.onDestroy();
        if (App.LOG) Log.i(TAG, "PushActivity: onDestroy");
    }

    private OnItemClickListener itemClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            if (App.LOG) Log.i(TAG, String.format("PushActivity: onItemClick: pos=%d id=%d", position, id));
            // TODO: Do something in response to the click
        }
    };

    private static class RendererDope {
        public Renderer renderer;
        public String friendlyName;
    }
}
