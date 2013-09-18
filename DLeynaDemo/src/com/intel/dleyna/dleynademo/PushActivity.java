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

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.intel.dleyna.lib.DLeynaException;
import com.intel.dleyna.lib.Renderer;
import com.intel.dleyna.lib.RendererManager;
import com.intel.dleyna.lib.RendererManagerListener;

/**
 * This Activity takes media content as input from an Intent, and allows the
 * user to push it to any available DMR.
 */
public class PushActivity extends Activity {

    private static final String TAG = App.TAG;

    private Handler mainHandler = new Handler();
    private ExecutorService workerPool = Executors.newSingleThreadExecutor();

    private String mediaType;
    private String mediaPath;

    private RendererManager rendererMgr;
    private Renderer[] renderers = new Renderer[0];
    private Renderer renderer;
    private String rendererObjPath;

    public void onCreate(Bundle state) {
        super.onCreate(state);
        getMediaArgsFromIntent();
        if (App.LOG) Log.i(TAG, String.format("PushActivity: onCreate: relaunch=%b type=%s path=%s",
                state, mediaType, mediaPath));
        setContentView(R.layout.push);
        if (mediaPath != null) {
            connectToService();
            if (state == null) {
                // We are not being relaunched.
                launchChooser();
            } else {
                // Relaunched. We might already have the chosen renderer.
                rendererObjPath = state.getString(RendererChoiceActivity.KEY_OBJ_PATH);
            }
        }
    }

    protected void onStart() {
        super.onStart();
        if (App.LOG) Log.i(TAG, "PushActivity: onStart");
    }

    protected void onResume() {
        super.onResume();
        if (App.LOG) Log.i(TAG, "PushActivity: onResume");
        if (mediaPath == null) {
            Toast.makeText(this, "No media!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    protected void onSaveInstanceState (Bundle state) {
        super.onSaveInstanceState(state);
        if (rendererObjPath != null) {
            state.putString(RendererChoiceActivity.KEY_OBJ_PATH, rendererObjPath);

        }
    }

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
                updateListAndLookForChosen();
             }
            public void onDisconnected() {
                if (App.LOG) Log.i(TAG, "PushActivity: onDisconnected");
            }
            public void onRendererFound(Renderer r) {
                if (App.LOG) Log.i(TAG, "PushActivity: onRendererFound");
                updateListAndLookForChosen();
            }
            public void onRendererLost(Renderer r) {
                if (App.LOG) Log.i(TAG, "PushActivity: onRendererLost");
                updateListAndLookForChosen();
            }
        });
        rendererMgr.connect(this);
    }

    private void launchChooser() {
        Intent intent = new Intent(this, RendererChoiceActivity.class);
        startActivityForResult(intent, RendererChoiceActivity.REQ_SINGLE);
    }

    protected void onActivityResult(int request, int result, Intent intent) {
        if (App.LOG) Log.i(TAG, String.format("PushActivity: onActivityResult: req=%d res=%d",
                request, result));
        switch (result) {
        case Activity.RESULT_CANCELED:
            finish();
            break;
        case Activity.RESULT_OK:
            rendererObjPath = intent.getStringExtra(RendererChoiceActivity.KEY_OBJ_PATH);
            lookForChosen();
            if (App.LOG) Log.i(TAG, String.format("PushActivity: onActivityResult: objPath=%s", rendererObjPath));
            break;
        default:
            break;
        }
    }

    private void updateListAndLookForChosen() {
        // We're on the UI thread.
        workerPool.execute(new Runnable() {
            public void run() {
                // We're on a worker thread.
                try {
                    final Renderer[] rv = rendererMgr.getRenderers();
                    mainHandler.post(new Runnable() {
                        public void run() {
                            // We're on the UI thread.
                            renderers = rv;
                            lookForChosen();
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

    private void lookForChosen() {
        // We're on the UI thread.
        if (renderer == null) {
            if (rendererObjPath != null) {
                for (Renderer r : renderers) {
                    if (rendererObjPath.equals(r.getObjectPath())) {
                        renderer = r;
                        hostAndPlay(mediaPath, renderer); // TODO: a real controller UI
                    }
                }
            }
        }
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
}
