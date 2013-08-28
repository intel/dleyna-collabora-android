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

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

    private MyAsyncTask asyncTask;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (App.LOG) Log.i(TAG, "PushActivity: onCreate");
        setContentView(R.layout.push);
        getListView().setOnItemClickListener(itemClickListener);
    }

    protected void onStart() {
        super.onStart();
        if (App.LOG) Log.i(TAG, "PushActivity: onStart");
    }

    protected void onResume() {
        super.onResume();
        if (App.LOG) Log.i(TAG, "PushActivity: onResume");
        asyncTask = new MyAsyncTask();
        asyncTask.execute();
    }

    protected void onPause() {
        super.onPause();
        if (App.LOG) Log.i(TAG, "PushActivity: onPause");
        asyncTask.cancel(true);
        asyncTask = null;
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

    private class MyAsyncTask extends AsyncTask<Void, RendererDope, Void> {

        private static final int SCAN_PERIOD = 30000; // time between rescans, msec
        private static final int SLEEP_PERIOD = 3000; // time per nap, msec
        private static final int NAPS_PER_SCAN = SCAN_PERIOD / SLEEP_PERIOD;

        private boolean connected = false;

        private RendererManager rendererMgr = new RendererManager(new RendererManagerListener() {
            public void onConnected() {
                MyAsyncTask.this.connected = true;
            }
            public void onDisconnected() {
                MyAsyncTask.this.connected = false;
                MyAsyncTask.this.cancel(true);
            }
        });

        protected void onPreExecute() {
            rendererMgr.connect(PushActivity.this);
        }

        protected Void doInBackground(Void... v) {
            while (!connected && !isCancelled()) {
                sleep(100);
            }
            try {
                int k = 0;
                while (!isCancelled()) {
                    publishProgress(getRenderersDope());
                    if (k == 0) {
                        rendererMgr.rescan();
                    }
                    sleep(SLEEP_PERIOD);
                    k = (k + 1) % NAPS_PER_SCAN;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (DLeynaException e) {
                e.printStackTrace();
            }
            return null;
        }

        private RendererDope[] getRenderersDope() throws RemoteException, DLeynaException {
            RendererDope[] rdv = null;
            Renderer[] rv = rendererMgr.getRenderers();
            if (rv != null && rv.length > 0) {
                rdv = new RendererDope[rv.length];
                for (int i=0; i < rv.length; i++) {
                    rdv[i] = new RendererDope();
                    rdv[i].renderer = rv[i];
                    rdv[i].friendlyName = rv[i].getFriendlyName();
                }
            }
            return rdv;
        }

        private void sleep(int msec) {
            try { Thread.sleep(msec); } catch (InterruptedException e) {}
        }

        protected void onProgressUpdate(RendererDope... renderersDope) {
            if (renderersDope != null ) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(PushActivity.this,
                        android.R.layout.simple_list_item_1);
                for (int i=0; i < renderersDope.length; i++) {
                    adapter.add(renderersDope[i].friendlyName);
                }
                PushActivity.this.setListAdapter(adapter);
            }
        }

        protected void onPostExecute(Void v) {
            cleanUp();
        }

        protected void onCancelled() {
            cleanUp();
        }

        private void cleanUp() {
            if (connected) {
                rendererMgr.disconnect();
            }
        }
    }

    private static class RendererDope {
        public Renderer renderer;
        public String friendlyName;
    }
}
