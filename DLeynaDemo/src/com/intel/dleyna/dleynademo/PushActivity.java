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
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
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

    View controllerLayout;
    RendererControlUI controller;

    public void onCreate(Bundle state) {
        super.onCreate(state);
        getMediaArgsFromIntent();
        if (App.LOG) Log.i(TAG, String.format("PushActivity: onCreate: relaunch=%b type=%s path=%s",
                state, mediaType, mediaPath));
        setContentView(R.layout.push);
        controllerLayout = findViewById(R.id.controllerLayout);
        controllerLayout.setVisibility(View.INVISIBLE);
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
        stopAndUnhost(renderer, mediaPath);
        if (controller != null) {
            controller.stop();
        }
    }

    protected void onStop() {
        super.onStop();
        if (App.LOG) Log.i(TAG, "PushActivity: onStop");
    }

    protected void onDestroy() {
        super.onDestroy();
        if (App.LOG) Log.i(TAG, "PushActivity: onDestroy");
        disconnectFromService();
        workerPool.shutdown();
    }

    private void connectToService() {
        rendererMgr = new RendererManager(new RendererManagerListener() {
            public void onConnected() {
                if (App.LOG) Log.i(TAG, "PushActivity: onConnected");
                if (renderer == null) {
                    updateListAndLookForChosen();
                }
             }
            public void onDisconnected() {
                if (App.LOG) Log.i(TAG, "PushActivity: onDisconnected");
            }
            public void onRendererFound(Renderer r) {
                if (App.LOG) Log.i(TAG, "PushActivity: onRendererFound");
                if (renderer == null) {
                    updateListAndLookForChosen();
                }
            }
            public void onRendererLost(Renderer r) {
                if (App.LOG) Log.i(TAG, "PushActivity: onRendererLost");
                if (renderer == null) {
                    updateListAndLookForChosen();
                }
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
            lookForChosenRenderer();
            if (App.LOG) Log.i(TAG, String.format("PushActivity: onActivityResult: objPath=%s", rendererObjPath));
            break;
        default:
            break;
        }
    }

    private void updateListAndLookForChosen() {
        workerPool.execute(new Doable() { protected void doIt() throws RemoteException, DLeynaException {
            // We're on the worker thread.
            final Renderer[] rv = rendererMgr.getRenderers();
            mainHandler.post(new Runnable() { public void run() {
                // We're on the UI thread.
                renderers = rv;
                lookForChosenRenderer();
            }});
        }});
    }

    private void lookForChosenRenderer() {
         if (renderer == null) {
            if (rendererObjPath != null) {
                for (Renderer r : renderers) {
                    if (rendererObjPath.equals(r.getObjectPath())) {
                        // We have the renderer chosen by the user.
                        renderer = r;
                        onRendererChosen();
                    }
                }
            }
        }
    }

    private void onRendererChosen() {
        preventOrientationRelaunches();
        getWindow().setBackgroundDrawableResource(android.R.drawable.screen_background_dark);
        controller = new RendererControlUI(
                renderer,
                (SeekBar)findViewById(R.id.positionBar),
                (ImageButton)findViewById(R.id.playButton),
                (ImageButton)findViewById(R.id.pauseButton));
        controllerLayout.setVisibility(View.VISIBLE);
        hostAndOpen(renderer, mediaPath);
    }

    private void hostAndOpen(final Renderer r, final String mPath) {
        if (App.LOG) Log.i(TAG, "PushActivity: hostAndOpen");
        workerPool.execute(new Doable() { protected void doIt() throws RemoteException, DLeynaException {
            // We're on the worker thread.
            if (r != null) {
                String url = r.hostFile(mediaPath);
                r.openUri(url);
                mainHandler.post(new Runnable() { public void run() {
                    // We're on the UI thread.
                    onHostedAndOpened();
                }});
            }
        }});
    }

    private void onHostedAndOpened() {
        if (App.LOG) Log.i(TAG, "PushActivity: onHostedAndOpenend");
    }

    private void stopAndUnhost(final Renderer r, final String mPath) {
        workerPool.execute(new Doable() { protected void doIt() throws RemoteException, DLeynaException {
            // We're on the worker thread.
            if (r != null) {
                r.stop();
                r.removeFile(mPath);
                if (App.LOG) Log.i(TAG, "PushActivity: stopAndUnhost: DONE");
            }
        }});
    }

    private void disconnectFromService() {
        if (rendererMgr != null) {
            rendererMgr.disconnect();
        }
    }

    /**
     * This prevents further relaunches caused by the user rotating the device.
     * It sets the allowed orientation to be the current orientation or 180 degrees
     * from that. (An orientation change of 180 degrees does not cause a relaunch.)
     */
    private void preventOrientationRelaunches() {
        setRequestedOrientation(
            getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ?
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT :
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
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

    private static abstract class Doable implements Runnable {
        public final void run() {
            try {
                doIt();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (DLeynaException e) {
                e.printStackTrace();
            }
        }
        protected abstract void doIt() throws RemoteException, DLeynaException;
    }
}
