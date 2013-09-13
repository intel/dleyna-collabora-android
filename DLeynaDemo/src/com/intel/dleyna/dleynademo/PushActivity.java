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
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Toast;

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

    private Handler mainHandler = new Handler();
    private ExecutorService workerPool = Executors.newSingleThreadExecutor();

    private String mediaType;
    private String mediaPath;

    private RendererManager rendererMgr;
    private RendererDope[] renderersDope;
    private Renderer renderer;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMediaArgsFromIntent();
        if (App.LOG) Log.i(TAG, String.format("PushActivity: onCreate: type=%s path=%s",
                mediaType, mediaPath));
        if (mediaPath == null) {
            Toast.makeText(this, "No media", Toast.LENGTH_LONG).show();
            finish();
        } else {
            setContentView(R.layout.push);
            getListView().setOnItemClickListener(itemClickListener);
            connectToService();
        }
    }

    protected void onStart() {
        super.onStart();
        if (App.LOG) Log.i(TAG, "PushActivity: onStart");
    }

    protected void onResume() {
        super.onResume();
        if (App.LOG) Log.i(TAG, "PushActivity: onResume");
    }

    private OnItemClickListener itemClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            if (App.LOG) Log.i(TAG, String.format("PushActivity: onItemClick: pos=%d id=%d",
                    position, id));
            getListView().setEnabled(false);
            renderer = renderersDope[position].renderer;
            hostAndPlay(mediaPath, renderer);
        }
    };

    protected void onPause() {
        super.onPause();
        if (App.LOG) Log.i(TAG, "PushActivity: onPause");
        stopAndUnhost(rendererMgr, renderer, mediaPath);
    }

    protected void onStop() {
        super.onStop();
        if (App.LOG) Log.i(TAG, "PushActivity: onStop");
    }

    protected void onDestroy() {
        super.onDestroy();
        if (App.LOG) Log.i(TAG, "PushActivity: onDestroy");
        disconnectFromService();
    }

    private void connectToService() {
        rendererMgr = new RendererManager(new RendererManagerListener() {
            public void onConnected() {
                if (App.LOG) Log.i(TAG, "PushActivity: onConnected");
                updateList();
            }
            public void onDisconnected() {
                if (App.LOG) Log.i(TAG, "PushActivity: onDisconnected");
            }
            public void onRendererFound(Renderer r) {
                if (App.LOG) Log.i(TAG, "PushActivity: onRendererFound");
                updateList();
            }
            public void onRendererLost(Renderer r) {
                if (App.LOG) Log.i(TAG, "PushActivity: onRendererLost");
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
                    final RendererDope[] rdv = new RendererDope[rv.length];
                    for (int i=0; i < rv.length; i++) {
                        rdv[i] = new RendererDope();
                        rdv[i].renderer = rv[i];
                        rdv[i].friendlyName = rv[i].getFriendlyName();
                    }
                    mainHandler.post(new Runnable() {
                        public void run() {
                            // We're on the UI thread.
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                    PushActivity.this,
                                    android.R.layout.simple_list_item_1);
                            for (int i=0; i < rdv.length; i++) {
                                adapter.add(rdv[i].friendlyName);
                            }
                            PushActivity.this.setListAdapter(adapter);
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

    private void hostAndPlay(final String mPath, final Renderer r) {
        // We're on the UI thread.
        workerPool.execute(new Runnable() {
            public void run() {
                // We're on a worker thread.
                try {
                    String url = r.hostFile(mPath);
                    if (App.LOG) Log.i(TAG, "PushActivity: onItemClick: " + url);
                    r.openUri(url);
                    r.play();
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (DLeynaException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void stopAndUnhost(final RendererManager rMgr, final Renderer r, final String mPath) {
        // We're on the UI thread.
        workerPool.execute(new Runnable() {
            public void run() {
                // We're on a worker thread.
                if (r != null) {
                    try {
                        r.stop();
                        r.removeFile(mPath);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (DLeynaException e) {
                        e.printStackTrace();
                    }
                    if (App.LOG) Log.i(TAG, "PushActivity: stopAndUnhost: DONE");
                }
            }
        });
    }

    private void disconnectFromService() {
        if (rendererMgr != null) {
            rendererMgr.disconnect();
        }
    }

    private void getMediaArgsFromIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        mediaType = intent.getType();
        Uri uri = null;
        if (action.equals(Intent.ACTION_VIEW)) {
            uri = intent.getData();
        } else if (action.equals(Intent.ACTION_SEND)) {
            uri = (Uri) intent.getExtras().get(Intent.EXTRA_STREAM);
        }
        if (uri != null) {
            mediaPath = getPathNameFromContentUri(uri);
        }
    }

    private String getPathNameFromContentUri(Uri contentUri) {
        String[] projection = new String[] { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, projection, null, null, null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
    }

    private static class RendererDope {
        public Renderer renderer;
        public String friendlyName;
    }
}
