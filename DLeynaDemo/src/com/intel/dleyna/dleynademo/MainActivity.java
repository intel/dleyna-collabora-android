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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.intel.dleyna.lib.DLeynaException;
import com.intel.dleyna.lib.DLeynaUnknownPropertyException;
import com.intel.dleyna.lib.IRendererController;
import com.intel.dleyna.lib.Renderer;
import com.intel.dleyna.lib.RendererControllerListener;
import com.intel.dleyna.lib.RendererManager;
import com.intel.dleyna.lib.RendererManagerListener;

/**
 * This is the main Activity of the dLeyna demo app.
 * @author Tom Keel
 */
public class MainActivity extends Activity {

    private static final String TAG = App.TAG;

    // States of the connection to the background service.
    private static final int CONN_DISCONNECTED = 0;
    private static final int CONN_CONNECTING = 1;
    private static final int CONN_CONNECTED = 2;
    private static final int CONN_DISCONNECTING = 3;

    private int connState = CONN_DISCONNECTED;

    private AsyncTask<Renderer, String, Void> listTask;
    private boolean listTaskBusy = false;

    private Prefs prefs = Prefs.getInstance();

    // Widgets.
    private TextView ttyTextView;
    private Button clearButton;
    private Button connectButton;
    private Button disconnButton;
    private Button listButton;
    private Button cancelButton;
    private Button rescanButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (App.LOG) Log.i(TAG, "MainActivity: onCreate");
        setContentView(R.layout.main);
        ttyTextView = (TextView) findViewById(R.id.tty_text_view);
        ttyTextView.setMovementMethod(new ScrollingMovementMethod()); // makes it scrollable...
        clearButton = (Button) findViewById(R.id.clear_button);
        clearButton.setOnClickListener(clearButtonListener);
        connectButton = (Button) findViewById(R.id.connect_button);
        connectButton.setOnClickListener(connectButtonListener);
        disconnButton = (Button) findViewById(R.id.disconnect_button);
        disconnButton.setOnClickListener(disconnectButtonListener);
        listButton = (Button) findViewById(R.id.list_renderers_button);
        listButton.setOnClickListener(listButtonListener);
        cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(cancelButtonListener);
        rescanButton = (Button) findViewById(R.id.rescan_button);
        rescanButton.setOnClickListener(rescanButtonListener);
        showHelp();
    }

    protected void onStart() {
        super.onStart();
        if (App.LOG) Log.i(TAG, "MainActivity: onStart");
    }

    protected void onResume() {
        super.onResume();
        if (App.LOG) Log.i(TAG, "MainActivity: onResume");
        setEnabledStateOfWidgets();
    }

    protected void onPause() {
        super.onPause();
        if (App.LOG) Log.i(TAG, "MainActivity: onPause");
    }

    protected void onStop() {
        super.onStop();
        if (App.LOG) Log.i(TAG, "MainActivity: onStop");
    }

    protected void onDestroy() {
        super.onDestroy();
        if (App.LOG) Log.i(TAG, "MainActivity: onDestroy");
        rendererMgr.disconnect();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_options, menu);
        menu.findItem(R.id.mi_settings).setIcon(android.R.drawable.ic_menu_preferences);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.mi_settings:
            startActivity(new Intent(this, PrefsActivity.class));
            return true;
        case R.id.mi_dleyna_server:
            startActivity(new Intent(this, DleynaServerActivity.class));
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private final OnClickListener clearButtonListener = new OnClickListener() {
        public void onClick(View v) {
            ttyTextView.setText(null);
        }
    };

    private final OnClickListener connectButtonListener = new OnClickListener() {
        public void onClick(View v) {
            connState = CONN_CONNECTING;
            setEnabledStateOfWidgets();
            rendererMgr.connect(MainActivity.this);
        }
    };

    private final OnClickListener disconnectButtonListener = new OnClickListener() {
        public void onClick(View v) {
            connState = CONN_DISCONNECTING;
            setEnabledStateOfWidgets();
            rendererMgr.disconnect();
        }
    };

    private final OnClickListener listButtonListener = new OnClickListener() {
        public void onClick(View v) {
            Renderer[] renderers = null;
            try {
                renderers = rendererMgr.getRenderers();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (DLeynaException e) {
                e.printStackTrace();
            }
            if (renderers == null || renderers.length == 0) {
                writeTty("No renderers.\n");
            } else {
                listTask = new AsyncTask<Renderer, String, Void>() {
                    private Renderer r = null;
                    protected void onPreExecute() {
                        listTaskBusy = true;
                        setEnabledStateOfWidgets();
                    }
                    protected Void doInBackground(Renderer... renderers) {
                        for (int i=0; i < renderers.length; i++) {
                            r = renderers[i];
                            publishProgress((i+1) + "/" + renderers.length + " " + r.getObjectPath());
                            try {
                                publishProgress("\tDeviceType: " + r.getDeviceType());
                                publishProgress("\tUniqueDeviceName: " + r.getUniqueDeviceName());
                                publishProgress("\tFriendlyName: " + r.getFriendlyName());
                                try {
                                    publishProgress("\tIconURL: " + r.getIconURL());
                                } catch (DLeynaUnknownPropertyException e) {
                                    publishProgress("\tIconURL: <NONE>");
                                }
                                publishProgress("\tManufacturer: " + r.getManufacturer());
                                try {
                                    publishProgress("\tManufacturerURL: " + r.getManufacturerURL());
                                } catch (DLeynaUnknownPropertyException e) {
                                    publishProgress("\tManufacturerURL: <NONE>");
                                }
                                try {
                                    publishProgress("\tModelDescription: " + r.getModelDescription());
                                } catch (DLeynaUnknownPropertyException e) {
                                    publishProgress("\tModelDescription: <NONE>");
                                }
                                publishProgress("\tModelName: " + r.getModelName());
                                try {
                                    publishProgress("\tModelNumber: " + r.getModelNumber());
                                } catch (DLeynaUnknownPropertyException e) {
                                    publishProgress("\tModelNumber: <NONE>");
                                }
                                try {
                                    publishProgress("\tSerialNumber: " + r.getSerialNumber());
                                } catch (DLeynaUnknownPropertyException e) {
                                    publishProgress("\tSerialNumber: <NONE>");
                                }
                                Bundle metaData = r.getMetadata();
                                try {
                                    publishProgress("\tMetaData: TrackID: " + metaData.getString(
                                            IRendererController.META_DATA_KEY_TRACK_ID));
                                } catch  (Exception e) {
                                    publishProgress("\tMetaData: TrackID: <NONE>");
                                }

                                try {
                                    publishProgress("\tPresentationURL: " + r.getPresentationURL());
                                } catch (DLeynaUnknownPropertyException e) {
                                    publishProgress("\tPresentationURL: <NONE>");
                                }
                                publishProgress("\tProtocolInfo: " + r.getProtocolInfo());
                            } catch (RemoteException e) {
                                publishProgress("\t" + "ERROR: no service");
                                e.printStackTrace();
                            } catch (DLeynaException e) {
                                publishProgress("\t" + "ERROR: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }
                    protected void onProgressUpdate(String... s) {
                        writeTty(s[0] + "\n");
                    }
                    protected void onPostExecute(Void v) {
                        listTaskBusy = false;
                        setEnabledStateOfWidgets();
                    }
                    protected void onCancelled() {
                        listTaskBusy = false;
                        setEnabledStateOfWidgets();
                    }
                }.execute(renderers);
            }
        }
    };

    private final OnClickListener cancelButtonListener = new OnClickListener() {
        public void onClick(View v) {
            Renderer[] renderers = null;
            try {
                renderers = rendererMgr.getRenderers();
                for (Renderer r : renderers) {
                    r.cancel();
                }
                listTask.cancel(true);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (DLeynaException e) {
                e.printStackTrace();
            }
        }
    };

    private final OnClickListener rescanButtonListener = new OnClickListener() {
        public void onClick(View v) {
            try {
                rendererMgr.rescan();
                writeTty("OK rescan sent.\n");
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (DLeynaException e) {
                e.printStackTrace();
            }
        }
    };

    private RendererManager rendererMgr = new RendererManager(new RendererManagerListener() {

        public void onConnected() {
            if (App.LOG) Log.i(TAG, "MainActivity: onConnected");
            connState = CONN_CONNECTED;
            setEnabledStateOfWidgets();
            writeTty("Connected.\n");
        }

        public void onDisconnected() {
            if (App.LOG) Log.i(TAG, "MainActivity: onDisconnected");
            connState = CONN_DISCONNECTED;
            setEnabledStateOfWidgets();
            writeTty("Disconnected.\n");
        }

        public void onRendererFound(final Renderer r) {
            writeTty("Found Renderer: " + r.getObjectPath() + '\n');
            r.addControllerListener(new RendererControllerListener() {
                String objPath = r.getObjectPath();
                public void onPlaybackStatusChanged(IRendererController c, String status) {
                    writeTty("(!) " + objPath + " PlaybackStatus: " + status + "\n");
                }
                public void onRateChanged(IRendererController c, double rate) {
                    writeTty("(!) " + objPath + " Rate: " + rate + "\n");
                }
                public void onMetadataChanged(IRendererController c, Bundle metadata) {
                    String trackId = metadata.getString(IRendererController.META_DATA_KEY_TRACK_ID);
                    writeTty("(!) " + objPath + " Metadata: TrackID: " + trackId + "\n");
                }
                public void onVolumeChanged(IRendererController c, double volume) {
                    writeTty("(!) " + objPath + " Volume: " + volume + "\n");
                }
                public void onMinimumRateChanged(IRendererController c, double rate) {
                    writeTty("(!) " + objPath + " MinimumRate: " + rate + "\n");
                }
                public void onMaximumRateChanged(IRendererController c, double rate) {
                    writeTty("(!) " + objPath + " MaximumRate: " + rate + "\n");
                }
                public void onCanGoNextChanged(IRendererController c, boolean value) {
                    writeTty("(!) " + objPath + " CanGoNext: " + value + "\n");
                }
                public void onCanGoPreviousChanged(IRendererController c, boolean value) {
                    writeTty("(!) " + objPath + " CanGoPrevious: " + value + "\n");
                }
                public void onTrackChanged(IRendererController c, int track) {
                    writeTty("(!) " + objPath + " Track: " + track + "\n");
                }
                public void onPositionChanged(IRendererController c, long position) {
                    writeTty("(!) " + objPath + " Position: " + position + "\n");
                }
                public void onCanPlayChanged(IRendererController c, boolean value) {
                    writeTty("(!) " + objPath + " CanPlay: " + value + "\n");
                }
                public void onCanPauseChanged(IRendererController c, boolean value) {
                    writeTty("(!) " + objPath + " CanPause: " + value + "\n");
                }
                public void onCanSeekChanged(IRendererController c, boolean value) {
                    writeTty("(!) " + objPath + " CanSeek: " + value + "\n");
                }
                public void onCanControlChanged(IRendererController c, boolean value) {
                    writeTty("(!) " + objPath + " CanControl: " + value + "\n");
                }
                public void onTransportPlaySpeedsChanged(IRendererController c, double[] speeds) {
                    writeTty("(!) " + objPath + " TransportPlaySpeeds: " + speeds + "\n");
                    for (double speed : speeds) {
                        writeTty("  (!) " + speed + "\n");
                    }
                }
                public void onCurrentTrackChanged(IRendererController c, int track) {
                    writeTty("(!) " + objPath + " CurrentTrack: " + track + "\n");
                }
                public void onNumberOfTracksChanged(IRendererController c, int n) {
                    writeTty("(!) " + objPath + " NumberOfTracks: " + n + "\n");
                }
                public void onMuteChanged(IRendererController c, boolean value) {
                    writeTty("(!) " + objPath + " Mute: " + value + "\n");
                }
            });
        }

        public void onRendererLost(final Renderer r) {
            writeTty("Lost Renderer: " + r.getObjectPath() + '\n');
        }
    });

    private void setEnabledStateOfWidgets() {
        clearButton.setEnabled(true);
        connectButton.setEnabled(connState == CONN_DISCONNECTED);
        disconnButton.setEnabled(connState == CONN_CONNECTED && !listTaskBusy);
        listButton.setEnabled(connState == CONN_CONNECTED && !listTaskBusy);
        rescanButton.setEnabled(connState == CONN_CONNECTED && !listTaskBusy);
        cancelButton.setEnabled(connState == CONN_CONNECTED && listTaskBusy);
    }

    private void writeTty(CharSequence cs) {
        ttyTextView.append(cs);
        ttyTextView.bringPointIntoView(ttyTextView.length() - 1);
    }

    private static final String help =
        "DLeyna Renderer Test App.\n\n" +
        "Use the 'Conn' and 'Disc' buttons to connect/disconnect to/from the background service. " +
        "The 'List' button will list the properties of any known renderers. " +
        "The 'Scan' button will request a upnp rescan.\n\n" +
        "From the options menu, DLeyna Server Demo is available.\n\n" +
        "For a more interesting user experience, go to the Gallery app, " +
        "share or play a video or photo, and choose DLeyna as the vehicle.\n\n";

    private void showHelp() {
        ttyTextView.append(help);
    }
}
