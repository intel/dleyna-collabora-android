package com.intel.dleyna.dleynademo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.intel.dleyna.lib.DLeynaException;
import com.intel.dleyna.lib.IRendererController;
import com.intel.dleyna.lib.IRendererControllerListener;
import com.intel.dleyna.lib.Renderer;
import com.intel.dleyna.lib.RendererControllerListener;

public class RendererControlUI {

    private static final String TAG = App.TAG;

    private static final int PB_STOPPED = 0;
    private static final int PB_PAUSED = 1;
    private static final int PB_PLAYING = 2;

    private Handler mainHandler = new Handler();
    private ExecutorService workerPool = Executors.newSingleThreadExecutor();

    private Renderer renderer;
    private SeekBar positionBar;
    private ImageButton playButton;
    private ImageButton pauseButton;

    private int playbackStatus;
    private int duration;
    private int position;

    public RendererControlUI(
            Renderer renderer,
            SeekBar positionBar,
            ImageButton playButton,
            ImageButton pauseButton) {

        this.renderer = renderer;
        this.positionBar = positionBar;
        this.playButton = playButton;
        this.pauseButton = pauseButton;

        renderer.addControllerListener(listener);
        positionBar.setOnSeekBarChangeListener(positionBarListener);
        playButton.setOnClickListener(playButtonListener);
        pauseButton.setOnClickListener(pauseButtonListener);

        positionBar.setEnabled(false);
        playButton.setEnabled(false);
        pauseButton.setEnabled(false);

        positionThread.start();
    }

    public void stop() {
        positionBar.setEnabled(false);
        playButton.setEnabled(false);
        pauseButton.setEnabled(false);
        stopPositionThread();
        workerPool.shutdown();
    }

    private final SeekBar.OnSeekBarChangeListener positionBarListener = new SeekBar.OnSeekBarChangeListener() {

        private int posFromUser;

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                posFromUser = progress;
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            if (playbackStatus == PB_PAUSED) {
                positionBar.setEnabled(false);
                playButton.setEnabled(false);
                workerPool.execute(new Doable() { protected void doIt() throws RemoteException, DLeynaException {
                    // We're on the worker thread.
                    if (App.LOG) Log.i(TAG, "onStopTrackingTouch: pos=" + posFromUser);
                    renderer.setPosition(((long)posFromUser) * 1000);
                    mainHandler.post(new Runnable() { public void run() {
                        // We're on the UI thread.
                        position = posFromUser;
                        updateWidgets();
                    }});
                }});
            }
        }
    };

    private final OnClickListener playButtonListener = new OnClickListener() {
        public void onClick(View v) {
            playButton.setEnabled(false);
            workerPool.execute(new Doable() { protected void doIt() throws RemoteException, DLeynaException {
                // We're on the worker thread.
                renderer.play();
                mainHandler.post(new Runnable() { public void run() {
                    // We're on the UI thread.
                    playButton.setEnabled(true);
                }});
            }});
        }
    };

    private final OnClickListener pauseButtonListener = new OnClickListener() {
        public void onClick(View v) {
            pauseButton.setEnabled(false);
            workerPool.execute(new Doable() { protected void doIt() throws RemoteException, DLeynaException {
                // We're on the worker thread.
                renderer.pause();
                mainHandler.post(new Runnable() { public void run() {
                    // We're on the UI thread.
                    pauseButton.setEnabled(true);
                }});
            }});
        }
    };

    private IRendererControllerListener listener = new RendererControllerListener() {

        public void onPlaybackStatusChanged(IRendererController c, String status) {
            if (App.LOG) Log.i(TAG, "onPlaybackStatusChg: " + status);

            if (status.equals(IRendererController.PLAYBACK_PAUSED)) {
                playbackStatus = PB_PAUSED;
            } else if (status.equals(IRendererController.PLAYBACK_PLAYING)) {
                playbackStatus = PB_PLAYING;
            } else {
                playbackStatus = PB_STOPPED;
            }
            updateWidgets();
        }

        public void onMetadataChanged(IRendererController c, Bundle metadata) {
            String trackId = metadata.getString(IRendererController.META_DATA_KEY_TRACK_ID);
            duration = (int) (metadata.getLong(IRendererController.META_DATA_KEY_TRACK_LENGTH, 0) / 1000);
            updateWidgets();
            if (App.LOG) Log.i(TAG, String.format("onMetadataChg: duration=%d trackId=%s", duration, trackId));
        }

        public void onCurrentTrackChanged(IRendererController c, int track) {
            if (App.LOG) Log.i(TAG, "onCurrentTrackChg: " + track);
        }

        public void onNumberOfTracksChanged(IRendererController c, int n) {
            if (App.LOG) Log.i(TAG, "onNumberOfTracksChg: " + n);
        }

        public void onPositionChanged(IRendererController c, long position) {
            if (App.LOG) Log.i(TAG, "onPositionChg: " + position / 1000);
        }
    };

    private volatile boolean positionThreadStopFlag = false;

    private Thread positionThread = new Thread(new Runnable() {
        public void run() {
            while (!positionThreadStopFlag) {
                if (playbackStatus == PB_PLAYING) {
                    workerPool.execute(new Doable() { protected void doIt() throws RemoteException, DLeynaException {
                        // We're on the worker thread.
                        if (playbackStatus == PB_PLAYING) {
                            try {
                                final int pos = (int) (renderer.getPosition() / 1000);
                                if (App.LOG) Log.i(TAG, "positionThread: pos=" + pos);
                                mainHandler.post(new Runnable() { public void run() {
                                    // We're on the UI thread.
                                    position = pos;
                                    updateWidgets();
                                }});
                            } catch (RemoteException e) {
                                positionThreadStopFlag = true;
                            } catch (DLeynaException e) {
                                positionThreadStopFlag = true;
                            }
                        }
                    }});
                    if (!positionThreadStopFlag) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
        }
    });

    private void stopPositionThread() {
        positionThreadStopFlag = true;
    }

    private void updateWidgets() {
        positionBar.setMax(duration);
        positionBar.setProgress(position);
        if (playbackStatus == PB_STOPPED) {
            positionBar.setEnabled(false);
            pauseButton.setVisibility(View.INVISIBLE);
            playButton.setVisibility(View.VISIBLE);
            playButton.setEnabled(true);
        } else if (playbackStatus == PB_PAUSED) {
            positionBar.setEnabled(true);
            pauseButton.setVisibility(View.INVISIBLE);
            playButton.setVisibility(View.VISIBLE);
            playButton.setEnabled(true);
        } else if (playbackStatus == PB_PLAYING) {
            positionBar.setEnabled(false);
            playButton.setVisibility(View.INVISIBLE);
            pauseButton.setVisibility(View.VISIBLE);
            pauseButton.setEnabled(true);
        }
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
