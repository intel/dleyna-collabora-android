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
import android.os.RemoteException;
import android.util.Log;

import com.intel.dleyna.Connector.Invocation;
import com.intel.dleyna.lib.IRendererClient;
import com.intel.dleyna.lib.IRendererService;
import com.intel.dleyna.lib.Icon;

public class RendererService extends Service implements IConnectorClient {

    private static final boolean LOG = true;
    private static final String TAG = "RendererService";

    private static final String DAEMON_THREAD_NAME = "RendererDaemon";

    private static final String MANAGER_OBJECT_PATH = "/com/intel/dLeynaRenderer";

    private Thread daemonThread;

    private Connector connector;

    private RemoteCallbackList<IRendererClient> clients =
            new RemoteCallbackList<IRendererClient>();

    public RendererService() {
        JNI.initialize();
        JNI.cleanTempDir();
        // FIXME: move the call to cleanTempDir() into the Application instance
        // of the service app when we split things up someday. You can't just
        // call it from each service process.
    }

    public IBinder onBind(Intent intent) {
        if (LOG) Log.i(TAG, "onBind");

        connector = new Connector(this, MANAGER_OBJECT_PATH);

        // Create and start the daemon thread.
        daemonThread = new Thread(daemonRunnable, DAEMON_THREAD_NAME);
        daemonThread.setDaemon(true);
        daemonThread.start();

        // Wait until the manager object shows up.
        connector.waitForManagerObject();
        return binder;
    }

    public boolean onUnbind(Intent intent) {
        if (LOG) Log.i(TAG, "onUnbind");
        dleynaMainLoopQuit(connector);
        // Wait for the daemon thread to exit.
        try {
            daemonThread.join();
        } catch (InterruptedException e) {
        }
        daemonThread = null;
        connector = null;
        if (LOG) Log.i(TAG, "onUnbind: EXIT");
        return false;
    }

    private Runnable daemonRunnable = new Runnable() {
        public void run() {
            if (LOG) Log.i(TAG, Thread.currentThread().getName() + ": entering");
            connector.setJNIEnv();
            int status = dleynaMainLoopStart(connector);
            if (LOG) Log.i(TAG, Thread.currentThread().getName() + ": exiting: status=" + status);
        }
    };

    /**
     * This is called on the daemon thread.
     * It creates and enters the g_main_loop.
     * @return 0 success, non-zero the gmain_loop didn't start
     */
    public native int dleynaMainLoopStart(Connector c);

    /**
     * This is called on this service's main thread.
     * It causes the daemon thread to exit the g_main_loop.
     */
    public native void dleynaMainLoopQuit(Connector c);

    private final IBinder binder = new IRendererService.Stub() {

        public void registerClient(IRendererClient client) {
            if (LOG) Log.i(TAG, "registerClient");
            clients.register(client);
        }

        public void unregisterClient(IRendererClient client) {
            if (LOG) Log.i(TAG, "unregisterClient");
            clients.unregister(client);
        }

//      How to broadcast clients:
//      {
//          int n = clients.beginBroadcast();
//          for (int i = 0; i < n; i++) {
//              try {
//                  clients.getBroadcastItem(i).aMethod();
//              } catch (RemoteException e) {
//                  // The RemoteCallbackList removes dead objects.
//              }
//          }
//          clients.finishBroadcast();
//      }

        /*-----------------+
         | RendererManager |
         +-----------------*/

        private static final String INTF_RENDERER_MANAGER = "com.intel.dLeynaRenderer.Manager";

        public String[] getRenderers(IRendererClient client) throws RemoteException {
            RemoteObject mgrObj = connector.getManagerObject();
            if (LOG) Log.i(TAG, "getRenderers: mrgObj=" + mgrObj);
            if (mgrObj == null) {
                return null;
            }
            Invocation invocation = connector.dispatch(client, mgrObj, INTF_RENDERER_MANAGER, "GetRenderers", null);
            String[] result = invocation.result.getArrayOfString();
            if (LOG) Log.i(TAG, "getRenderers: n=" + result.length);
            for (int i=0; i < result.length; i++) {
                if (LOG) Log.i(TAG, "getRenderers: result[" + i + "]=" + result[i]);
            }
            invocation.result.free();
            return result;
        }

        public void rescan(IRendererClient client) throws RemoteException {
            RemoteObject mgrObj = connector.getManagerObject();
            if (LOG) Log.i(TAG, "rescan: mrgObj=" + mgrObj);
            if (mgrObj != null) {
                Invocation invocation = connector.dispatch(client, mgrObj, INTF_RENDERER_MANAGER, "Rescan", null);
                if (LOG) Log.i(TAG, "rescan: result: " + invocation.result);
            }
        }

        /*-----------------+
         | IRendererDevice |
         +-----------------*/

        public String getDeviceType(IRendererClient client, String objectPath) {
            return null;
        }

        public String getUniqueDeviceName(IRendererClient client, String objectPath) {
            return null;
        }

        public String getFriendlyName(IRendererClient client, String objectPath) {
            return null;
        }

        public String getIconURL(IRendererClient client, String objectPath) {
            return null;
        }

        public String getManufacturer(IRendererClient client, String objectPath) {
            return null;
        }

        public String getManufacturerURL(IRendererClient client, String objectPath) {
            return null;
        }

        public String getModelDescription(IRendererClient client, String objectPath) {
            return null;
        }

        public String getModelName(IRendererClient client, String objectPath) {
            return null;
        }

        public String getModelNumber(IRendererClient client, String objectPath) {
            return null;
        }

        public String getSerialNumber(IRendererClient client, String objectPath) {
            return null;
        }

        public String getPresentationURL(IRendererClient client, String objectPath) {
            return null;
        }

        public String getProtocolInfo(IRendererClient client, String objectPath) {
            return null;
        }

        public Icon getIcon(IRendererClient client, String objectPath) {
            return null;
        }

        public void cancel(IRendererClient client, String objectPath) {
        }

        /*--------------------+
        | IRendererController |
        +---------------------*/

        public void next(IRendererClient client, String objectPath) {
        }

        public void previous(IRendererClient client, String objectPath) {
        }

        public void pause(IRendererClient client, String objectPath) {
        }

        public void playPause(IRendererClient client, String objectPath) {
        }

        public void stop(IRendererClient client, String objectPath) {
        }

        public void play(IRendererClient client, String objectPath) {
        }

        public void seek(IRendererClient client, String objectPath, long offset) {
        }

        public void setPosition(IRendererClient client, String objectPath, long position) {
        }

        public void openUri(IRendererClient client, String objectPath, String uri) {
        }

        public String getPlaybackStatus(IRendererClient client, String objectPath) {
            return null;
        }

        public double getRate(IRendererClient client, String objectPath) {
            return 0;
        }

        public void setRate(IRendererClient client, String objectPath, double rate) {
        }

        public Bundle getMetadata(IRendererClient client, String objectPath) {
            return null;
        }

        public double getVolume(IRendererClient client, String objectPath) {
            return 0;
        }

        public void setVolume(IRendererClient client, String objectPath, double volume) {
        }

        public long getPosition(IRendererClient client, String objectPath) {
            return 0;
        }

        public long getMinimumRate(IRendererClient client, String objectPath) {
            return 0;
        }

        public long getMaximumRate(IRendererClient client, String objectPath) {
            return 0;
        }

        public boolean getCanGoNext(IRendererClient client, String objectPath) {
            return false;
        }

        public boolean getCanGoPrevious(IRendererClient client, String objectPath) {
            return false;
        }

        public boolean getCanPlay(IRendererClient client, String objectPath) {
            return false;
        }

        public boolean getCanPause(IRendererClient client, String objectPath) {
            return false;
        }

        public boolean getCanSeek(IRendererClient client, String objectPath) {
            return false;
        }

        public boolean getCanControl(IRendererClient client, String objectPath) {
            return false;
        }

        public int getNumberOfTracks(IRendererClient client, String objectPath) {
            return 0;
        }

        public void goToTrack(IRendererClient client, String objectPath, int track) {
        }

        public int getCurrentTrack(IRendererClient client, String objectPath) {
            return 0;
        }

        public void openUriEx(IRendererClient client, String objectPath, String uri, String metadata) {
        }

        public double[] getTransportPlaySpeeds(IRendererClient client, String objectPath) {
            return null;
        }

        public boolean getMute(IRendererClient client, String objectPath) {
            return false;
        }

        public void setMute(IRendererClient client, String objectPath, boolean value) {
        }

        /*-------------------+
         | IRendererPushHost |
         +-------------------*/

        public String hostFile(IRendererClient client, String objectPath, String path) {
            return null;
        }

        public void removeFile(IRendererClient client, String objectPath, String path) {
        }
    };

    /*------------------+
     | IConnectorClient |
     +------------------*/

    public boolean onNotify(String objPath, String ifaceName, String notifName, long params,
            long gErrPtr) {

        GVariant gvParams = new GVariant(params);

        if (LOG) Log.i(TAG, "onNotify: " + objPath + " " + ifaceName + " " + notifName + " " +
                gvParams.getTypeString());

        if (notifName.equals("FoundRenderer")) {
            GVariant gvObjPathRenderer = gvParams.getChildAtIndex(0);
            String objPathRenderer = gvObjPathRenderer.getString();
            gvObjPathRenderer.free();
            if (LOG) Log.i(TAG, "onNotify: FoundRenderer: " + objPathRenderer);

            int n = clients.beginBroadcast();
            for (int i = 0; i < n; i++) {
                try {
                    clients.getBroadcastItem(i).onRendererFound(objPathRenderer);
                } catch (RemoteException e) {
                    // The RemoteCallbackList removes dead objects.
                }
            }
            clients.finishBroadcast();

        } else if (notifName.equals("LostRenderer")) {
                GVariant gvObjPathRenderer = gvParams.getChildAtIndex(0);
                String objPathRenderer = gvObjPathRenderer.getString();
                gvObjPathRenderer.free();
                if (LOG) Log.i(TAG, "onNotify: LostRenderer: " + objPathRenderer);

                int n = clients.beginBroadcast();
                for (int i = 0; i < n; i++) {
                    try {
                        clients.getBroadcastItem(i).onRendererLost(objPathRenderer);
                    } catch (RemoteException e) {
                        // The RemoteCallbackList removes dead objects.
                    }
                }
                clients.finishBroadcast();

        } else if (notifName.equals("PropertiesChanged")) {
            if (LOG) Log.i(TAG, "onNotify: PropertiesChanged:");
        }

        gvParams.free();
        return true;
    }
}
