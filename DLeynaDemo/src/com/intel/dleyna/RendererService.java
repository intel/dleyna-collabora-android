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

package com.intel.dleyna;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.util.Log;
import android.util.SparseArray;

import com.intel.dleyna.lib.IRendererCallback;
import com.intel.dleyna.lib.IRendererService;
import com.intel.dleyna.lib.Icon;

public class RendererService extends Service implements IConnector {

    private static final boolean LOG = true;
    private static final String TAG = "RendererService";

    private static final String DAEMON_THREAD_NAME = "RendererDaemon";

    private static final String MANGER_OBJECT_PATH = "/com/intel/dLeynaRenderer";

    /**
     * The current set of remote objects.
     * The first object to show up should be the Manager object
     * (though we don't presume so),
     * and it should be around until shutdown.
     * Renderer objects come and go.
     */
    private SparseArray<RemoteObject> objects = new SparseArray<RemoteObject>();

    /**
     * We attribute integer id-s to remote objects starting with 1.
     */
    private int lastAssignededObjectId;

    /** The manager object's id. */
    private int managerObjectId;

    public RendererService() {
        JNI.initialize();
        JNI.cleanTempDir();
        // FIXME: move the call to cleanTempDir() into the Application instance
        // of the service app when we split things up someday. You can't just
        // call it from each service process.
    }

    public IBinder onBind(Intent intent) {
        if (LOG) Log.i(TAG, "onBind");

        lastAssignededObjectId = 0;
        managerObjectId = 0;

        // Create and start the daemon thread.
        if (daemonThread != null) {
            if (LOG) Log.e(TAG, "onBind: zombie daemon thread!");
        }
        daemonThread = new Thread(daemonRunnable, DAEMON_THREAD_NAME);
        daemonThread.setDaemon(true);
        daemonThread.start();

        // Wait until the manager object shows up.
        synchronized(this) {
            while (managerObjectId == 0) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                }
            }
        }
        return binder;
    }

    public boolean onUnbind(Intent intent) {
        if (LOG) Log.i(TAG, "onUnbind");
        dleynaMainLoopQuit();
        // Wait for the daemon thread to exit.
        try {
            daemonThread.join();
        } catch (InterruptedException e) {
        }
        daemonThread = null;
        if (LOG) Log.i(TAG, "onUnbind: EXIT");
        return false;
    }

    private Thread daemonThread;

    private Runnable daemonRunnable = new Runnable() {
        public void run() {
            if (LOG) Log.i(TAG, Thread.currentThread().getName() + ": entering");
            setJNIEnv();
            int status = dleynaMainLoopStart();
            if (LOG) Log.i(TAG, Thread.currentThread().getName() + ": exiting: status=" + status);
        }
    };

    /**
     * This is called on the daemon thread.
     * It creates and enters the g_main_loop.
     * @return 0 success, non-zero the gmain_loop didn't start
     */
    public native int dleynaMainLoopStart();

    /**
     * This is called on this service's main thread.
     * It causes the daemon thread to exit the g_main_loop.
     */
    public native void dleynaMainLoopQuit();

    private final IBinder binder = new IRendererService.Stub() {

        private RemoteCallbackList<IRendererCallback> callbacks =
                new RemoteCallbackList<IRendererCallback>();

        public void registerClient(IRendererCallback cb) {
            if (LOG) Log.i(TAG, "registerClient");
            callbacks.register(cb);
        }

        public void unregisterClient(IRendererCallback cb) {
            if (LOG) Log.i(TAG, "unregisterClient");
            callbacks.unregister(cb);
        }

//      How to broadcast callbacks:
//      {
//          int n = callbacks.beginBroadcast();
//          for (int i = 0; i < n; i++) {
//              try {
//                  callbacks.getBroadcastItem(i).aMethod();
//              } catch (RemoteException e) {
//                  // The RemoteCallbackList removes dead objects.
//              }
//          }
//          callbacks.finishBroadcast();
//      }

        /*-----------------+
         | IRendererDevice |
         +-----------------*/

        public String getDeviceType(String objectPath) {
            return null;
        }

        public String getUniqueDeviceName(String objectPath) {
            return null;
        }

        public String getFriendlyName(String objectPath) {
            return null;
        }

        public String getIconURL(String objectPath) {
            return null;
        }

        public String getManufacturer(String objectPath) {
            return null;
        }

        public String getManufacturerURL(String objectPath) {
            return null;
        }

        public String getModelDescription(String objectPath) {
            return null;
        }

        public String getModelName(String objectPath) {
            return null;
        }

        public String getModelNumber(String objectPath) {
            return null;
        }

        public String getSerialNumber(String objectPath) {
            return null;
        }

        public String getPresentationURL(String objectPath) {
            return null;
        }

        public String getProtocolInfo(String objectPath) {
            return null;
        }

        public Icon getIcon(String objectPath) {
            return null;
        }

        public void cancel(String objectPath) {
        }

        /*--------------------+
        | IRendererController |
        +---------------------*/

        public void next(String objectPath) {
        }

        public void previous(String objectPath) {
        }

        public void pause(String objectPath) {
        }

        public void playPause(String objectPath) {
        }

        public void stop(String objectPath) {
        }

        public void play(String objectPath) {
        }

        public void seek(String objectPath, long offset) {
        }

        public void setPosition(String objectPath, long position) {
        }

        public void openUri(String objectPath, String uri) {
        }

        public String getPlaybackStatus(String objectPath) {
            return null;
        }

        public double getRate(String objectPath) {
            return 0;
        }

        public void setRate(String objectPath, double rate) {
        }

        public Bundle getMetadata(String objectPath) {
            return null;
        }

        public double getVolume(String objectPath) {
            return 0;
        }

        public void setVolume(String objectPath, double volume) {
        }

        public long getPosition(String objectPath) {
            return 0;
        }

        public long getMinimumRate(String objectPath) {
            return 0;
        }

        public long getMaximumRate(String objectPath) {
            return 0;
        }

        public boolean getCanGoNext(String objectPath) {
            return false;
        }

        public boolean getCanGoPrevious(String objectPath) {
            return false;
        }

        public boolean getCanPlay(String objectPath) {
            return false;
        }

        public boolean getCanPause(String objectPath) {
            return false;
        }

        public boolean getCanSeek(String objectPath) {
            return false;
        }

        public boolean getCanControl(String objectPath) {
            return false;
        }

        public int getNumberOfTracks(String objectPath) {
            return 0;
        }

        public void goToTrack(String objectPath, int track) {
        }

        public int getCurrentTrack(String objectPath) {
            return 0;
        }

        public void openUriEx(String objectPath, String uri, String metadata) {
        }

        public double[] getTransportPlaySpeeds(String objectPath) {
            return null;
        }

        public boolean getMute(String objectPath) {
            return false;
        }

        public void setMute(String objectPath, boolean value) {
        }

        /*-------------------+
         | IRendererPushHost |
         +-------------------*/

        public String hostFile(String objectPath, String path) {
            return null;
        }

        public void removeFile(String objectPath, String path) {
        }
    };

    /*------------+
     | IConnector |
     +------------*/

    public native void setJNIEnv();

    public boolean initialize(String serverInfo, String rootInfo, int errorQuark) {
        if (LOG) Log.i(TAG, "initialize");
        return true;
    }

    public void shutdown() {
        if (LOG) Log.i(TAG, "shutdown");
    }

    public void connect(String serverName, long connectedCb, long disconnectedCb) {
        if (LOG) Log.i(TAG, "connect");
    }

    public void disconnect() {
        if (LOG) Log.i(TAG, "disconnect");
    }

    public boolean watchClient(String clientName) {
        if (LOG) Log.i(TAG, "watchClient");
        return false;
    }

    public void unwatchClient(String clientName) {
        if (LOG) Log.i(TAG, "unwatchClient");
    }

    public void setClientLostCallback(long lostCb) {
        if (LOG) Log.i(TAG, "setClientLostCallback");
    }

    public int publishObject(long connectorId, String objectPath, boolean isRoot,
            int interfaceIndex, long dispatchCb) {
        if (LOG) Log.i(TAG, "publishObject: " + objectPath);

        // Add this object to the collection.
        lastAssignededObjectId++;
        RemoteObject ro = new RemoteObject(lastAssignededObjectId, connectorId, objectPath,
                isRoot, interfaceIndex, dispatchCb);
        objects.append(ro.id, ro);

        // If it's the manager object, note its id and unblock the main thread in onBind().
        if (objectPath.equals(MANGER_OBJECT_PATH)) {
            synchronized (this) {
                managerObjectId = lastAssignededObjectId;
                this.notify();
            }
        }

       return ro.id;
    }

    public int publishSubtree(long connectorId, String objectPath, long[] dispatchCb,
            long interfaceFilterCb) {
        if (LOG) Log.i(TAG, "publishSubtree");
        return 0;
    }

    public void unpublishObject(long connectorId, int ObjectId) {
        if (LOG) Log.i(TAG, "unpublishObject");
    }

    public void unpublishSubtree(long connectorId, int objectId) {
        if (LOG) Log.i(TAG, "unpublishSubtree");
    }

    public void returnResponse(long messageId, Object parameters) {
        if (LOG) Log.i(TAG, "unpublishSubtree");
    }

    public void returnError(long messageId, int errorCode) {
        if (LOG) Log.i(TAG, "returnResponse");
    }

    public boolean notify(long connectorId, String objectPath, String interfaceName,
            String notificationName, Object parameters, int errorCode) {
        if (LOG) Log.i(TAG, "notify");
        return false;
    }
}
