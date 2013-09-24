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
import com.intel.dleyna.lib.Extras;
import com.intel.dleyna.lib.IRendererClient;
import com.intel.dleyna.lib.IRendererService;
import com.intel.dleyna.lib.Icon;
import com.intel.dleyna.lib.RendererControllerProps;

public class RendererService extends Service implements IConnectorClient {

    private static final boolean LOG = true;
    private static final String TAG = "RendererService";

    private static final String DAEMON_THREAD_NAME = "RendererDaemon";

    private static final String MANAGER_OBJECT_PATH = "/com/intel/dLeynaRenderer";

    private static final String ERR_NO_MANAGER = "No manager object " + MANAGER_OBJECT_PATH;

    private static final String IFACE_MANAGER    = "com.intel.dLeynaRenderer.Manager";
    private static final String IFACE_DEVICE     = "com.intel.dLeynaRenderer.RendererDevice";
    private static final String IFACE_CONTROLLER = "org.mpris.MediaPlayer2.Player";
    private static final String IFACE_PUSH_HOST  = "com.intel.dLeynaRenderer.PushHost";
    private static final String IFACE_DBUS_PROP  = "org.freedesktop.DBus.Properties";

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

        connector = new Connector(this, MANAGER_OBJECT_PATH, IFACE_MANAGER);

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

        // The methods herein are called on some arbitrary binder thread.

        public void registerClient(IRendererClient client) {
            if (LOG) Log.i(TAG, "registerClient");
            clients.register(client);
        }

        public void unregisterClient(IRendererClient client) {
            if (LOG) Log.i(TAG, "unregisterClient");
            clients.unregister(client);
        }

        /*-----------------+
         | RendererManager |
         +-----------------*/

        public String[] getRenderers(IRendererClient client, Bundle extras) {
            String[] result = null;
            RemoteObject mo = connector.getRemoteObject(MANAGER_OBJECT_PATH, IFACE_MANAGER);
            if (mo != null) {
                Invocation invo = connector.dispatch(client, mo, IFACE_MANAGER, "GetRenderers", null);
                if (invo.success) {
                    result = invo.result.getArrayOfString();
                    invo.result.free();
                } else {
                    extras.putInt(Extras.KEY_ERR_CODE, invo.errCode);
                    extras.putString(Extras.KEY_ERR_MSG, invo.errMessage);
                }
            }
            return result;
        }

        public void rescan(IRendererClient client, Bundle extras) {
            RemoteObject mo = connector.getRemoteObject(MANAGER_OBJECT_PATH, IFACE_MANAGER);
            if (mo != null) {
                Invocation invo = connector.dispatch(client, mo, IFACE_MANAGER, "Rescan", null);
                if (!invo.success) {
                    extras.putInt(Extras.KEY_ERR_CODE, invo.errCode);
                    extras.putString(Extras.KEY_ERR_MSG, invo.errMessage);
                }
            }
        }

        /*-----------------+
         | IRendererDevice |
         +-----------------*/

        public String getDeviceType(IRendererClient client, String objectPath, Bundle extras) {
            return getStringDBusProperty(client, objectPath, IFACE_DEVICE, "DeviceType", extras);
        }

        public String getUniqueDeviceName(IRendererClient client, String objectPath, Bundle extras) {
            return getStringDBusProperty(client, objectPath, IFACE_DEVICE, "UDN", extras);
        }

        public String getFriendlyName(IRendererClient client, String objectPath, Bundle extras) {
            return getStringDBusProperty(client, objectPath, IFACE_DEVICE, "FriendlyName", extras);
        }

        public String getIconURL(IRendererClient client, String objectPath, Bundle extras) {
            return getStringDBusProperty(client, objectPath, IFACE_DEVICE, "IconURL", extras);
        }

        public String getManufacturer(IRendererClient client, String objectPath, Bundle extras) {
            return getStringDBusProperty(client, objectPath, IFACE_DEVICE, "Manufacturer", extras);
        }

        public String getManufacturerURL(IRendererClient client, String objectPath, Bundle extras) {
            return getStringDBusProperty(client, objectPath, IFACE_DEVICE, "ManufacturerUrl", extras);
        }

        public String getModelDescription(IRendererClient client, String objectPath, Bundle extras) {
            return getStringDBusProperty(client, objectPath, IFACE_DEVICE, "ModelDescription", extras);
        }

        public String getModelName(IRendererClient client, String objectPath, Bundle extras) {
            return getStringDBusProperty(client, objectPath, IFACE_DEVICE, "ModelName", extras);
        }

        public String getModelNumber(IRendererClient client, String objectPath, Bundle extras) {
            return getStringDBusProperty(client, objectPath, IFACE_DEVICE, "ModelNumber", extras);
        }

        public String getSerialNumber(IRendererClient client, String objectPath, Bundle extras) {
            return getStringDBusProperty(client, objectPath, IFACE_DEVICE, "SerialNumber", extras);
        }

        public String getPresentationURL(IRendererClient client, String objectPath, Bundle extras) {
            return getStringDBusProperty(client, objectPath, IFACE_DEVICE, "PresentationURL", extras);
        }

        public String getProtocolInfo(IRendererClient client, String objectPath, Bundle extras) {
            return getStringDBusProperty(client, objectPath, IFACE_DEVICE, "ProtocolInfo", extras);
        }

        public Icon getIcon(IRendererClient client, String objectPath, Bundle extras) {
            // TODO
            return null;
        }

        public void cancel(IRendererClient client, String objectPath, Bundle extras) {
            RemoteObject ro = connector.getRemoteObject(objectPath, IFACE_DEVICE);
            if (ro != null) {
                if (LOG) Log.w(TAG, "*** CANCEL ***");
                Invocation invo = connector.dispatch(client, ro, IFACE_DEVICE, "Cancel", null);
                if (!invo.success) {
                    extras.putInt(Extras.KEY_ERR_CODE, invo.errCode);
                    extras.putString(Extras.KEY_ERR_MSG, invo.errMessage);
                }
            }
        }

        /*---------------------+
         | IRendererController |
         +---------------------*/

        public void next(IRendererClient client, String objectPath, Bundle extras) {
            doVoidMethodVoid(client, objectPath, IFACE_CONTROLLER, "Next", extras);
       }

        public void previous(IRendererClient client, String objectPath, Bundle extras) {
            doVoidMethodVoid(client, objectPath, IFACE_CONTROLLER, "Previous", extras);
        }

        public void pause(IRendererClient client, String objectPath, Bundle extras) {
            doVoidMethodVoid(client, objectPath, IFACE_CONTROLLER, "Pause", extras);
        }

        public void playPause(IRendererClient client, String objectPath, Bundle extras) {
            doVoidMethodVoid(client, objectPath, IFACE_CONTROLLER, "PlayPause", extras);
        }

        public void stop(IRendererClient client, String objectPath, Bundle extras) {
            doVoidMethodVoid(client, objectPath, IFACE_CONTROLLER, "Stop", extras);
        }

        public void play(IRendererClient client, String objectPath, Bundle extras) {
            doVoidMethodVoid(client, objectPath, IFACE_CONTROLLER, "Play", extras);
        }

        public void seek(IRendererClient client, String objectPath, long offset, Bundle extras) {
            doVoidMethodLong(client, objectPath, IFACE_CONTROLLER, "Seek", offset, extras);
        }

        public void setPosition(IRendererClient client, String objectPath, long position, Bundle extras) {
            doVoidMethodLong(client, objectPath, IFACE_CONTROLLER, "SetPosition", position, extras);
        }

        public void openUri(IRendererClient client, String objectPath, String uri, Bundle extras) {
            doVoidMethodString(client, objectPath, IFACE_CONTROLLER, "OpenUri", uri, extras);
        }

        public String getPlaybackStatus(IRendererClient client, String objectPath, Bundle extras) {
            return getStringDBusProperty(client, objectPath, IFACE_CONTROLLER, "PlaybackStatus", extras);
        }

        public double getRate(IRendererClient client, String objectPath, Bundle extras) {
            return getDoubleDBusProperty(client, objectPath, IFACE_CONTROLLER, "Rate", extras);
        }

        public void setRate(IRendererClient client, String objectPath, double rate, Bundle extras) {
            // TODO
        }

        public Bundle getMetadata(IRendererClient client, String objectPath, Bundle extras) {
            return getDictDBusProperty(client, objectPath, IFACE_CONTROLLER, "Metadata", extras);
        }

        public double getVolume(IRendererClient client, String objectPath, Bundle extras) {
            return getDoubleDBusProperty(client, objectPath, IFACE_CONTROLLER, "Volume", extras);
        }

        public void setVolume(IRendererClient client, String objectPath, double volume, Bundle extras) {
            // TODO
        }

        public long getPosition(IRendererClient client, String objectPath, Bundle extras) {
            return getLongDBusProperty(client, objectPath, IFACE_CONTROLLER, "Position", extras);
        }

        public double getMinimumRate(IRendererClient client, String objectPath, Bundle extras) {
            return getDoubleDBusProperty(client, objectPath, IFACE_CONTROLLER, "MinimumRate", extras);
        }

        public double getMaximumRate(IRendererClient client, String objectPath, Bundle extras) {
            return getDoubleDBusProperty(client, objectPath, IFACE_CONTROLLER, "MaximumRate", extras);
        }

        public boolean getCanGoNext(IRendererClient client, String objectPath, Bundle extras) {
            return getBooleanDBusProperty(client, objectPath, IFACE_CONTROLLER, "CanGoNext", extras);
        }

        public boolean getCanGoPrevious(IRendererClient client, String objectPath, Bundle extras) {
            return getBooleanDBusProperty(client, objectPath, IFACE_CONTROLLER, "CanGoPrevious", extras);
        }

        public boolean getCanPlay(IRendererClient client, String objectPath, Bundle extras) {
            return getBooleanDBusProperty(client, objectPath, IFACE_CONTROLLER, "CanPlay", extras);
        }

        public boolean getCanPause(IRendererClient client, String objectPath, Bundle extras) {
            return getBooleanDBusProperty(client, objectPath, IFACE_CONTROLLER, "CanPause", extras);
        }

        public boolean getCanSeek(IRendererClient client, String objectPath, Bundle extras) {
            return getBooleanDBusProperty(client, objectPath, IFACE_CONTROLLER, "CanSeek", extras);
        }

        public boolean getCanControl(IRendererClient client, String objectPath, Bundle extras) {
            return getBooleanDBusProperty(client, objectPath, IFACE_CONTROLLER, "CanControl", extras);
        }

        public int getNumberOfTracks(IRendererClient client, String objectPath, Bundle extras) {
            return getUInt32DBusProperty(client, objectPath, IFACE_CONTROLLER, "NumberOfTracks", extras);
        }

        public void goToTrack(IRendererClient client, String objectPath, int track, Bundle extras) {
            // TODO
        }

        public int getCurrentTrack(IRendererClient client, String objectPath, Bundle extras) {
            return getUInt32DBusProperty(client, objectPath, IFACE_CONTROLLER, "CurrentTrack", extras);
        }

        public void openUriEx(IRendererClient client, String objectPath, String uri, String metadata, Bundle extras) {
            doVoidMethodStringString(client, objectPath, IFACE_CONTROLLER, "UriEx", uri, metadata, extras);
        }

        public double[] getTransportPlaySpeeds(IRendererClient client, String objectPath, Bundle extras) {
            return null; // TODO
        }

        public boolean getMute(IRendererClient client, String objectPath, Bundle extras) {
            return getBooleanDBusProperty(client, objectPath, IFACE_CONTROLLER, "Mute", extras);
        }

        public void setMute(IRendererClient client, String objectPath, boolean value, Bundle extras) {
            // TODO
        }

        /*-------------------+
         | IRendererPushHost |
         +-------------------*/

        public String hostFile(IRendererClient client, String objectPath, String path, Bundle extras) {
            return doStringMethodString(client, objectPath, IFACE_PUSH_HOST, "HostFile", path, extras);
        }

        public void removeFile(IRendererClient client, String objectPath, String path, Bundle extras) {
            doVoidMethodString(client, objectPath, IFACE_PUSH_HOST, "RemoveFile", path, extras);
        }

        /*--------------------*/

        private String getStringDBusProperty(IRendererClient client, String objectPath, String iface,
                String propName, Bundle extras) {
            if (LOG) logGetDBusProp("getStringDBusProp", objectPath, iface, propName);
            String result = null;
            RemoteObject ro = connector.getRemoteObject(objectPath, IFACE_DBUS_PROP);
            if (ro != null) {
                GVariant args = GVariant.newTupleStringString(iface, propName);
                Invocation invo = connector.dispatch(client, ro, IFACE_DBUS_PROP, "Get", args);
                args.free();
                if (invo.success) {
                    result = invo.result.getChildAtIndex(0).getString();
                    invo.result.free();
                } else {
                    extras.putInt(Extras.KEY_ERR_CODE, invo.errCode);
                    extras.putString(Extras.KEY_ERR_MSG, invo.errMessage);
                }
            }
            if (LOG) Log.i(TAG, "getStringDBusProp: result=" + result);
            return result;
        }

        private boolean getBooleanDBusProperty(IRendererClient client, String objectPath, String iface,
                String propName, Bundle extras) {
            if (LOG) logGetDBusProp("getBooleanDBusProp", objectPath, iface, propName);
            boolean result = false;
            RemoteObject ro = connector.getRemoteObject(objectPath, IFACE_DBUS_PROP);
            if (ro != null) {
                GVariant args = GVariant.newTupleStringString(iface, propName);
                Invocation invo = connector.dispatch(client, ro, IFACE_DBUS_PROP, "Get", args);
                args.free();
                if (invo.success) {
                    result = invo.result.getChildAtIndex(0).getBoolean();
                    invo.result.free();
                } else {
                    extras.putInt(Extras.KEY_ERR_CODE, invo.errCode);
                    extras.putString(Extras.KEY_ERR_MSG, invo.errMessage);
                }
            }
            if (LOG) Log.i(TAG, "getBooleanDBusProp: result=" + result);
            return result;
        }
    };

    private double getDoubleDBusProperty(IRendererClient client, String objectPath, String iface,
            String propName, Bundle extras) {
        if (LOG) logGetDBusProp("getDoubleDBusProp", objectPath, iface, propName);
        double result = 0;
        RemoteObject ro = connector.getRemoteObject(objectPath, IFACE_DBUS_PROP);
        if (ro != null) {
            GVariant args = GVariant.newTupleStringString(iface, propName);
            Invocation invo = connector.dispatch(client, ro, IFACE_DBUS_PROP, "Get", args);
            args.free();
            if (invo.success) {
                result = invo.result.getChildAtIndex(0).getDouble();
                invo.result.free();
            } else {
                extras.putInt(Extras.KEY_ERR_CODE, invo.errCode);
                extras.putString(Extras.KEY_ERR_MSG, invo.errMessage);
            }
        }
        if (LOG) Log.i(TAG, "getDoubleDBusProp: result=" + result);
        return result;
    }

    private long getLongDBusProperty(IRendererClient client, String objectPath, String iface,
            String propName, Bundle extras) {
        if (LOG) logGetDBusProp("getLongDBusProp", objectPath, iface, propName);
        long result = 0;
        RemoteObject ro = connector.getRemoteObject(objectPath, IFACE_DBUS_PROP);
        if (ro != null) {
            GVariant args = GVariant.newTupleStringString(iface, propName);
            Invocation invo = connector.dispatch(client, ro, IFACE_DBUS_PROP, "Get", args);
            args.free();
            if (invo.success) {
                result = invo.result.getChildAtIndex(0).getInt64();
                invo.result.free();
            } else {
                extras.putInt(Extras.KEY_ERR_CODE, invo.errCode);
                extras.putString(Extras.KEY_ERR_MSG, invo.errMessage);
            }
        }
        if (LOG) Log.i(TAG, "getLongDBusProp: result=" + result);
        return result;
    }

    private int getUInt32DBusProperty(IRendererClient client, String objectPath, String iface,
            String propName, Bundle extras) {
        if (LOG) logGetDBusProp("getUInt32DBusProp", objectPath, iface, propName);
        int result = 0;
        RemoteObject ro = connector.getRemoteObject(objectPath, IFACE_DBUS_PROP);
        if (ro != null) {
            GVariant args = GVariant.newTupleStringString(iface, propName);
            Invocation invo = connector.dispatch(client, ro, IFACE_DBUS_PROP, "Get", args);
            args.free();
            if (invo.success) {
                result = invo.result.getChildAtIndex(0).getUInt32();
                invo.result.free();
            } else {
                extras.putInt(Extras.KEY_ERR_CODE, invo.errCode);
                extras.putString(Extras.KEY_ERR_MSG, invo.errMessage);
            }
        }
        if (LOG) Log.i(TAG, "getUInt32DBusProp: result=" + result);
        return result;
    }

    private Bundle getDictDBusProperty(IRendererClient client, String objectPath, String iface,
            String propName, Bundle extras) {
        if (LOG) logGetDBusProp("getDictDBusProp", objectPath, iface, propName);
        Bundle result = null;

        RemoteObject ro = connector.getRemoteObject(objectPath, IFACE_DBUS_PROP);
        if (ro != null) {
            GVariant args = GVariant.newTupleStringString(iface, propName);
            Invocation invo = connector.dispatch(client, ro, IFACE_DBUS_PROP, "Get", args);
            args.free();
            if (invo.success) {
                result = makeBundleFromDictionary(invo.result.getChildAtIndex(0));
                invo.result.free();
            } else {
                extras.putInt(Extras.KEY_ERR_CODE, invo.errCode);
                extras.putString(Extras.KEY_ERR_MSG, invo.errMessage);
            }
        }

        if (LOG) Log.i(TAG, "getDictDBusProp: result=" + result);
        return result;
    }

    private void logGetDBusProp(String method, String objPath, String iface, String prop) {
        Log.i(TAG, String.format("%s: prop=%s obj=%s iface=%s", method, prop, objPath, iface));
    }

    private void doVoidMethodVoid(IRendererClient client, String objectPath, String iface,
            String method, Bundle extras) {
        if (LOG) logDoMethod("doVoidMethodVoid", objectPath, iface, method);
        RemoteObject ro = connector.getRemoteObject(objectPath, iface);
        if (ro != null) {
            Invocation invo = connector.dispatch(client, ro, iface, method, null);
            if (!invo.success) {
                extras.putInt(Extras.KEY_ERR_CODE, invo.errCode);
                extras.putString(Extras.KEY_ERR_MSG, invo.errMessage);
            }
        }
    }

    private void doVoidMethodString(IRendererClient client, String objectPath, String iface,
            String method, String arg, Bundle extras) {
        if (LOG) logDoMethod("doVoidMethodString", objectPath, iface, method);
        RemoteObject ro = connector.getRemoteObject(objectPath, iface);
        if (ro != null) {
            GVariant gvArgs = GVariant.newTupleString(arg);
            Invocation invo = connector.dispatch(client, ro, iface, method, gvArgs);
            gvArgs.free();
            if (!invo.success) {
                extras.putInt(Extras.KEY_ERR_CODE, invo.errCode);
                extras.putString(Extras.KEY_ERR_MSG, invo.errMessage);
            }
        }
    }

    private String doStringMethodString(IRendererClient client, String objectPath, String iface,
            String method, String arg, Bundle extras) {
        if (LOG) logDoMethod("doStringMethodString", objectPath, iface, method);
        String result = null;
        RemoteObject ro = connector.getRemoteObject(objectPath, iface);
        if (ro != null) {
            GVariant gvArgs = GVariant.newTupleString(arg);
            Invocation invo = connector.dispatch(client, ro, iface, method, gvArgs);
            gvArgs.free();
            if (invo.success) {
                result = invo.result.getString();
                invo.result.free();
            } else {
                extras.putInt(Extras.KEY_ERR_CODE, invo.errCode);
                extras.putString(Extras.KEY_ERR_MSG, invo.errMessage);
            }
        }
        if (LOG) Log.i(TAG, "doStringMethodString: result=" + result);
        return result;
    }

    private void doVoidMethodLong(IRendererClient client, String objectPath, String iface,
            String method, long arg, Bundle extras) {
        if (LOG) logDoMethod("doVoidMethodLong", objectPath, iface, method);
        RemoteObject ro = connector.getRemoteObject(objectPath, iface);
        if (ro != null) {
            GVariant gvArgs = GVariant.newLong(arg); // TODO: this must be in a tuple!
            Invocation invo = connector.dispatch(client, ro, iface, method, gvArgs);
            gvArgs.free();
            if (!invo.success) {
                extras.putInt(Extras.KEY_ERR_CODE, invo.errCode);
                extras.putString(Extras.KEY_ERR_MSG, invo.errMessage);
            }
        }
    }

    private void doVoidMethodStringString(IRendererClient client, String objectPath, String iface,
            String method, String arg1, String arg2, Bundle extras) {
        if (LOG) logDoMethod("doVoidMethodStringString", objectPath, iface, method);
        RemoteObject ro = connector.getRemoteObject(objectPath, iface);
        if (ro != null) {
            GVariant gvArgs = GVariant.newTupleStringString(arg1, arg2);
            Invocation invo = connector.dispatch(client, ro, iface, method, gvArgs);
            gvArgs.free();
            if (!invo.success) {
                extras.putInt(Extras.KEY_ERR_CODE, invo.errCode);
                extras.putString(Extras.KEY_ERR_MSG, invo.errMessage);
            }
        }
    }

    private void logDoMethod(String method, String objPath, String iface, String method2) {
        Log.i(TAG, String.format("%s: method=%s obj=%s iface=%s", method, method2, objPath, iface));
    }

    /*------------------+
     | IConnectorClient |
     +------------------*/

    public void onNotify(String objPath, String ifaceName, String notifName, long params) {
        if (LOG) Log.i(TAG, "onNotify: I=" + ifaceName + " N=" + notifName + " O=" + objPath);
        GVariant gvParams = new GVariant(params);
        if (ifaceName.equals(IFACE_MANAGER)) {
            if (notifName.equals("FoundRenderer")) {
                onRendererFound(gvParams);
            } else if (notifName.equals("LostRenderer")) {
                onRendererLost(gvParams);
            }
        } else if (ifaceName.equals(IFACE_DBUS_PROP)) {
            if (notifName.equals("PropertiesChanged")) {
                onDBusPropertiesChanged(objPath, gvParams);
            }
        }
        gvParams.free();
    }

    private void onRendererFound(GVariant gvParams) {
        GVariant gvObjPathRenderer = gvParams.getChildAtIndex(0);
        String objPathRenderer = gvObjPathRenderer.getString();
        gvObjPathRenderer.free();
        if (LOG) Log.i(TAG, "RendererFound: " + objPathRenderer);
        int n = clients.beginBroadcast();
        for (int i = 0; i < n; i++) {
            try {
                clients.getBroadcastItem(i).onRendererFound(objPathRenderer);
            } catch (RemoteException e) {
            }
        }
        clients.finishBroadcast();
    }

    private void onRendererLost(GVariant gvParams) {
        GVariant gvObjPathRenderer = gvParams.getChildAtIndex(0);
        String objPathRenderer = gvObjPathRenderer.getString();
        gvObjPathRenderer.free();
        if (LOG) Log.i(TAG, "RendererLost: " + objPathRenderer);
        int n = clients.beginBroadcast();
        for (int i = 0; i < n; i++) {
            try {
                clients.getBroadcastItem(i).onRendererLost(objPathRenderer);
            } catch (RemoteException e) {
            }
        }
        clients.finishBroadcast();
    }

    private void onDBusPropertiesChanged(String objPath, GVariant gvParams) {
        GVariant gvIface = gvParams.getChildAtIndex(0);
        String ifaceName = gvIface.getString();
        if (ifaceName.equals(IFACE_CONTROLLER)) {
            GVariant gvDictionary = gvParams.getChildAtIndex(1);
            onControllerPropertiesChanged(objPath, gvDictionary);
            gvDictionary.free();
        }
        gvIface.free();
    }

    private void onControllerPropertiesChanged(String objPath, GVariant gvDictionary) {
        Bundle props = new Bundle();
        GVariant gvEntries[] = gvDictionary.getArrayOfGVariant();
        for (GVariant gvEntry : gvEntries) {
            GVariant gvPropName = gvEntry.getChildAtIndex(0);
            GVariant gvPropValueVariant = gvEntry.getChildAtIndex(1);
            GVariant gvPropValue = gvPropValueVariant.getChildAtIndex(0);
            addControllerProperty(props, gvPropName.getString(), gvPropValue);
            gvPropValue.free();
            gvPropValueVariant.free();
            gvPropName.free();
            gvEntry.free();
        }
        int n = clients.beginBroadcast();
        for (int i = 0; i < n; i++) {
            try {
                clients.getBroadcastItem(i).onControllerPropertiesChanged(objPath, props);
            } catch (RemoteException e) {
            }
        }
        clients.finishBroadcast();
    }

    /** Add the controller property with the given name and value to the given bundle. */
    private void addControllerProperty(Bundle bundle, String propName, GVariant gvPropValue) {
        RendererControllerProps.Enum e = RendererControllerProps.getEnum(propName);
        switch (e) {
        case PLAYBACK_STATUS:
            String s = gvPropValue.getString();
            bundle.putString(propName, s);
            if (LOG) Log.i(TAG, "CtlrPropChange: " + propName + ": " + s);
            break;
        case CAN_GO_NEXT:
        case CAN_GO_PREVIOUS:
        case CAN_PLAY:
        case CAN_PAUSE:
        case CAN_SEEK:
        case CAN_CONTROL:
        case MUTE:
            boolean b = gvPropValue.getBoolean();
            bundle.putBoolean(propName, b);
            if (LOG) Log.i(TAG, "CtlrPropChange: " + propName + ": " + b);
            break;
        case RATE:
        case VOLUME:
        case MINIMUM_RATE:
        case MAXIMUM_RATE:
            double d = gvPropValue.getDouble();
            bundle.putDouble(propName, d);
            if (LOG) Log.i(TAG, "CtlrPropChange: " + propName + ": " + d);
            break;
        case POSITION:
            long l = gvPropValue.getInt64();
            bundle.putLong(propName, l);
            if (LOG) Log.i(TAG, "CtlrPropChange: " + propName + ": " + l);
            break;
        case METADATA:
            if (LOG) Log.i(TAG, "CtlrPropChange: " + propName + " BEGIN");
            bundle.putBundle(propName, makeBundleFromDictionary(gvPropValue));
            if (LOG) Log.i(TAG, "CtlrPropChange: " + propName + " END");
            break;
        case TRANSPORT_PLAY_SPEEDS:
            double[] ad = gvPropValue.getArrayOfDouble();
            bundle.putDoubleArray(propName, ad);
            if (LOG) Log.i(TAG, "CtlrPropChange: " + propName + ": " + ad);
            break;
        case CURRENT_TRACK:
        case NUMBER_OF_TRACKS:
            int i = gvPropValue.getUInt32();
            bundle.putInt(propName, i);
            if (LOG) Log.i(TAG, "CtlrPropChange: " + propName + ": " + i);
            break;
        case UNKNOWN:
            Log.e(TAG, "Unknown renderer controller property: " + propName);
            break;
        default:
            Log.w(TAG, "Unhandled renderer controller property: " + propName);
            break;
        }
    }

    private Bundle makeBundleFromDictionary(GVariant gvDictionary) {
        Bundle bundle = new Bundle();
        GVariant gvEntries[] = gvDictionary.getArrayOfGVariant();
        for (GVariant gvEntry : gvEntries) {
            GVariant gvEntryName = gvEntry.getChildAtIndex(0);
            GVariant gvEntryValueVariant = gvEntry.getChildAtIndex(1);
            GVariant gvEntryValue = gvEntryValueVariant.getChildAtIndex(0);
            putToBundleMaybe(bundle, gvEntryName.getString(), gvEntryValue);
            gvEntryValue.free();
            gvEntryValueVariant.free();
            gvEntryName.free();
            gvEntry.free();
        }
        return bundle;
    }

    // Add the (key, value) to the bundle if it's of a type we like.
    private void putToBundleMaybe(Bundle bundle, String key, GVariant gvValue) {
        String type = gvValue.getTypeString();
        boolean added = false;
        if (type.equals("o") || type.equals("s")) {
            bundle.putString(key, gvValue.getString());
            added = true;
        } else if (type.equals("x") || type.equals("t")) {
            bundle.putLong(key, gvValue.getInt64());
            added = true;
        }
        if (LOG) Log.i(TAG, String.format("putToBundle: %s name=%s type=%s",
                added ? "ADD " : "SKIP", key, type));
    }
}
