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
 */

package com.intel.dleyna;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.intel.dleyna.Connector.Invocation;
import com.intel.dleyna.lib.ContainerUpdateId;
import com.intel.dleyna.lib.DLeynaException;
import com.intel.dleyna.lib.DmsFeature;
import com.intel.dleyna.lib.Extras;
import com.intel.dleyna.lib.IServerClient;
import com.intel.dleyna.lib.IServerService;
import com.intel.dleyna.lib.Icon;
import com.intel.dleyna.lib.ServerControllerProps;

public class ServerService extends Service implements IConnectorClient {

    private static final boolean LOG = false;
    private static final String TAG = "ServerService";

    private static final String DAEMON_THREAD_NAME = "ServerDaemon";

    private static final String MANAGER_OBJECT_PATH = "/com/intel/dLeynaServer";

    private static final String ERR_NO_MANAGER = "No manager object " + MANAGER_OBJECT_PATH;

    private static final String IFACE_MANAGER    = "com.intel.dLeynaServer.Manager";
    private static final String IFACE_DEVICE     = "com.intel.dLeynaServer.MediaDevice";
    private static final String IFACE_CONTROLLER = "com.intel.dLeynaServer.MediaDevice";
    private static final String IFACE_CONTAINER  = "org.gnome.UPnP.MediaContainer2";
    private static final String IFACE_MEDIA_OBJECT = "org.gnome.MediaObject2";
    private static final String IFACE_MEDIA_ITEM = "org.gnome.MediaItem2";
    private static final String IFACE_DBUS_PROP  = "org.freedesktop.DBus.Properties";

    private Thread daemonThread;

    private Connector connector;

    private RemoteCallbackList<IServerClient> clients =
            new RemoteCallbackList<IServerClient>();

    public ServerService() {
        JNI.initialize(JNI.SERVER_CONF_FILENAME);
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
        if (LOG) Log.i(TAG, "onBind -- intent = " + intent);
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

    private final IBinder binder = new IServerService.Stub() {

        // The methods herein are called on some arbitrary binder thread.

        public void registerClient(IServerClient client) {
            if (LOG) Log.i(TAG, "registerClient");
            clients.register(client);
        }

        public void unregisterClient(IServerClient client) {
            if (LOG) Log.i(TAG, "unregisterClient");
            clients.unregister(client);
        }

        /*---------------+
         | ServerManager |
         +---------------*/

        public String[] getServers(IServerClient client, Bundle extras) {
            String[] result = null;
            RemoteObject mo = connector.getRemoteObject(MANAGER_OBJECT_PATH, IFACE_MANAGER);
            if (mo != null) {
                Invocation invo = connector.dispatch(client, MANAGER_OBJECT_PATH, mo, IFACE_MANAGER, "GetServers", null);
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

        public void rescan(IServerClient client, Bundle extras) {
            RemoteObject mo = connector.getRemoteObject(MANAGER_OBJECT_PATH, IFACE_MANAGER);
            if (mo != null) {
                Invocation invo = connector.dispatch(client, MANAGER_OBJECT_PATH, mo, IFACE_MANAGER, "Rescan", null);
                if (!invo.success) {
                    extras.putInt(Extras.KEY_ERR_CODE, invo.errCode);
                    extras.putString(Extras.KEY_ERR_MSG, invo.errMessage);
                }
            }
        }

        public String getVersion(IServerClient client, Bundle extras) {
            String result = null;
            RemoteObject mo = connector.getRemoteObject(MANAGER_OBJECT_PATH, IFACE_MANAGER);
            if (mo != null) {
                Invocation invo = connector.dispatch(client, MANAGER_OBJECT_PATH, mo, IFACE_MANAGER, "GetVersion", null);
                if (invo.success) {
                    result = invo.result.getString();
                    invo.result.free();
                } else {
                    extras.putInt(Extras.KEY_ERR_CODE, invo.errCode);
                    extras.putString(Extras.KEY_ERR_MSG, invo.errMessage);
                }
            }
            return result;
        }

        /*--------------+
         | IMediaDevice |
         +--------------*/

        public String getLocation(IServerClient client, String objectPath, Bundle extras) {
            return getStringDBusProperty(client, objectPath, IFACE_DEVICE, "Location", extras);
        }

        public String getUniqueDeviceName(IServerClient client, String objectPath, Bundle extras) {
            return getStringDBusProperty(client, objectPath, IFACE_DEVICE, "UDN", extras);
        }

        public String getRootUniqueDeviceName(IServerClient client, String objectPath, Bundle extras) {
            return getStringDBusProperty(client, objectPath, IFACE_DEVICE, "RootUDN", extras);
        }

        public String getDeviceType(IServerClient client, String objectPath, Bundle extras) {
            return getStringDBusProperty(client, objectPath, IFACE_DEVICE, "DeviceType", extras);
        }

        public String getFriendlyName(IServerClient client, String objectPath, Bundle extras) {
            return getStringDBusProperty(client, objectPath, IFACE_DEVICE, "FriendlyName", extras);
        }

        public String getManufacturer(IServerClient client, String objectPath, Bundle extras) {
            return getStringDBusProperty(client, objectPath, IFACE_DEVICE, "Manufacturer", extras);
        }

        public String getManufacturerUrl(IServerClient client, String objectPath, Bundle extras) {
            return getStringDBusProperty(client, objectPath, IFACE_DEVICE, "ManufacturerUrl", extras);
        }

        public String getModelDescription(IServerClient client, String objectPath, Bundle extras) {
            return getStringDBusProperty(client, objectPath, IFACE_DEVICE, "ModelDescription", extras);
        }

        public String getModelName(IServerClient client, String objectPath, Bundle extras) {
            return getStringDBusProperty(client, objectPath, IFACE_DEVICE, "ModelName", extras);
        }

        public String getModelNumber(IServerClient client, String objectPath, Bundle extras) {
            return getStringDBusProperty(client, objectPath, IFACE_DEVICE, "ModelNumber", extras);
        }

        public String getModelUrl(IServerClient client, String objectPath, Bundle extras) {
            return getStringDBusProperty(client, objectPath, IFACE_DEVICE, "ModelURL", extras);
        }

        public String getSerialNumber(IServerClient client, String objectPath, Bundle extras) {
            return getStringDBusProperty(client, objectPath, IFACE_DEVICE, "SerialNumber", extras);
        }

        public String getPresentationUrl(IServerClient client, String objectPath, Bundle extras) {
            return getStringDBusProperty(client, objectPath, IFACE_DEVICE, "PresentationURL", extras);
        }

        public String getIconUrl(IServerClient client, String objectPath, Bundle extras) {
            return getStringDBusProperty(client, objectPath, IFACE_DEVICE, "IconURL", extras);
        }

        public Bundle getDlnaCaps(IServerClient client, String objectPath, Bundle extras) {
            return getDictDBusProperty(client, objectPath, IFACE_DEVICE, "DLNACaps", extras);
        }

        public String[] getSearchCaps(IServerClient client, String objectPath, Bundle extras) {
            return getArrayOfStringDBusProperty(client, objectPath, IFACE_DEVICE, "SearchCaps", extras);
        }

        public String[] getSortCaps(IServerClient client, String objectPath, Bundle extras) {
            return getArrayOfStringDBusProperty(client, objectPath, IFACE_DEVICE, "SortCaps", extras);
        }

        public String[] getSortExtCaps(IServerClient client, String objectPath, Bundle extras) {
            return getArrayOfStringDBusProperty(client, objectPath, IFACE_DEVICE, "SortExtCaps", extras);
        }

        public DmsFeature[] getFeatureList(IServerClient client, String objectPath, Bundle extras) {
            return getArrayOfDmsFeatureDBusProperty(client, objectPath, IFACE_DEVICE, "FeatureList", extras);
        }

        public String getServiceResetToken(IServerClient client, String objectPath, Bundle extras) {
            return getStringDBusProperty(client, objectPath, IFACE_DEVICE, "ServiceResetToken", extras);
        }

        public boolean isSleeping(IServerClient client, String objectPath, Bundle extras) {
            return getBooleanDBusProperty(client, objectPath, IFACE_DEVICE, "Sleeping", extras);
        }

        public int getSystemUpdateId(IServerClient client, String objectPath, Bundle extras) {
            return getUInt32DBusProperty(client, objectPath, IFACE_DEVICE, "SystemUpdateID", extras);
        }

        public String getProtocolInfo(IServerClient client, String objectPath, Bundle extras) {
            return getStringDBusProperty(client, objectPath, IFACE_DEVICE, "ProtocolInfo", extras);
        }

        public Icon getIcon(IServerClient client, String objectPath, Bundle extras) {
            // TODO
            return null;
        }

        public void cancel(IServerClient client, String objectPath, Bundle extras) {
            RemoteObject ro = connector.getRemoteObject(objectPath, IFACE_DEVICE);
            if (ro != null) {
                if (LOG) Log.w(TAG, "*** CANCEL ***");
                Invocation invo = connector.dispatch(client, objectPath, ro, IFACE_DEVICE, "Cancel", null);
                if (!invo.success) {
                    extras.putInt(Extras.KEY_ERR_CODE, invo.errCode);
                    extras.putString(Extras.KEY_ERR_MSG, invo.errMessage);
                }
            }
        }

        public Bundle[] browseObjects(IServerClient client, String objectPath, Bundle extras) {
            String[] objectPaths = new String[] { objectPath };
            String[] filters = new String[] { "*" };
            return doBundleArrayMethodStringArrayStringArray(client, objectPath, IFACE_DEVICE, "BrowseObjects", objectPaths, filters, extras);
        }

        // TODO: ?

        /*------------------+
         | IMediaContainer2 |
         +------------------*/

        public Bundle[] listChildrenEx(IServerClient client, String objectPath, Bundle extras) {
            String[] filters = new String[] { "*" };
            return doBundleArrayMethodIntIntStringArrayString(client, objectPath, IFACE_CONTAINER, "ListChildrenEx", 0, 0, filters, "+DisplayName", extras);
        }

        public Bundle[] searchObjectsEx(IServerClient client, String objectPath, Bundle extras) {
            String query = "*";
            String[] filters = new String[] { "*" };
            return doBundleArrayMethodStringIntIntStringArrayString(client, objectPath, IFACE_CONTAINER, "SearchObjectsEx", query, 0, 0, filters, "+DisplayName", extras);
        }

        // TODO: ?

        /*---------------+
         | IMediaObject2 |
         +---------------*/

        public String getMetadata(IServerClient client, String objectPath, Bundle extras) {
            return doStringMethodVoid(client, objectPath, IFACE_MEDIA_OBJECT, "GetMetaData", extras);
        }

        // TODO: ?

        /*-------------------+
         | IServerController |
         +-------------------*/

        // TODO: ?

        /*-------------+
         | IServerDemo |
         +-------------*/

        public Bundle[] getChildren(IServerClient client, String objectPath, Bundle extras) {
            if (LOG) Log.i(TAG, "GetChildren for path = " + objectPath);
            List<Bundle> childList = getChildren(client, objectPath, extras, true);
            if (LOG) Log.i(TAG, "GetChildren size = " + childList.size());
            return childList.toArray(new Bundle[childList.size()]);
        }

        private final String[] filters = new String[] { "DisplayName", "Path", "Type" };
        private List<Bundle> getChildren(IServerClient client, String objectPath, Bundle extras, boolean useContainerMethod) {
            List<Bundle> childList = new ArrayList<Bundle>();
            try {
                String methodName = "ListChildrenEx";
                Bundle[] children = doBundleArrayMethodIntIntStringArrayString(client, objectPath, IFACE_CONTAINER, methodName, 0, 0, filters, "+DisplayName", extras);
                for (Bundle child : children) {
                    String path = null;
                    String name = null;
                    boolean isContainer = false;
                    for (String key : child.keySet()) {
                        if (key.equalsIgnoreCase("path")) {
                            path = child.getString(key);
                        } else if (key.equalsIgnoreCase("displayname")) {
                            name = child.getString(key);
                        } else if (key.equalsIgnoreCase("type")) {
                            isContainer = child.getString(key).equalsIgnoreCase("container");
                        } else {
                            // future ?
                        }
                    }
                    if (LOG) Log.d(TAG, "Examining child: " + name + ", isContainer: " + isContainer);

                    if (isContainer) {
                        Bundle bundleToAdd = new Bundle();
                        bundleToAdd.putString("ObjectPath", path);
                        bundleToAdd.putString("DisplayName", name);
                        bundleToAdd.putBoolean("Container", Boolean.TRUE);
                        childList.add(bundleToAdd);

                    } else {
                        try {
                            String metadata = getMetadata(client, path, extras);
                            if (LOG) {
                                Log.d(TAG, "Media name = " + name);
                                Log.d(TAG, "\tmetadata: " + metadata);
                            }
                            Bundle[] objects = browseObjects(client, path, extras);
                            if (LOG) {
                                Log.d(TAG, "Media name = " + name);
                                Log.d(TAG, "\tBrowsed objects: " + objects);
                            }
                            for (Bundle object : objects) {
                                if (LOG) {
                                    Log.d(TAG, "\t\tBrowsed object: " + object);
                                    for (String key : object.keySet()) {
                                        Log.d(TAG, "\t\t\t" + key + " --> " + object.get(key));
                                        if (object.get(key) instanceof String[]) {
                                            for (String string : (String[])object.get(key)) {
                                                Log.d(TAG, "\t\t\t\t" + string);
                                            }
                                        }
                                        if (object.get(key) instanceof Bundle[]) {
                                            for (Bundle bundle : (Bundle[])object.get(key)) {
                                                Log.d(TAG, "\t\t\t\t" + bundle);
                                            }
                                        }
                                    }
                                }

                                // TODO: might need another way to keep bundle size small enough for transaction
                                Bundle bundleToAdd = new Bundle();
                                bundleToAdd.putString("ObjectPath", path);
                                bundleToAdd.putString("DisplayName", object.getString("DisplayName"));
                                bundleToAdd.putStringArray("URLs", object.getStringArray("URLs"));
                                bundleToAdd.putString("AlbumArtURL", object.getString("AlbumArtURL"));
                                bundleToAdd.putString("MIMEType", object.getString("MIMEType"));
                                childList.add(bundleToAdd);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "\tBrowsed objects exception: " + e.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in GetChildren: " + e.getMessage());
            }
            return childList;
        }

        // TODO: ?
    };

    /*--------------------*/

    private DmsFeature[] getArrayOfDmsFeatureDBusProperty(IServerClient client, String objectPath, String iface,
            String propName, Bundle extras) {
        DmsFeature[] result = null;
        GVariant gvResult = getDBusProperty(client, objectPath, iface, propName, extras);
        if (gvResult != null) {
            if (LOG) Log.i(TAG, "getArrayOfDmsFeatureDBusProperty: type = " + gvResult.getTypeString());
            // expecting type "a(ssao)"
            List<DmsFeature> features = new ArrayList<DmsFeature>();
            GVariant[] gvFeatures = gvResult.getArrayOfGVariant();
            for (GVariant gvFeature : gvFeatures) {
                GVariant gvName = gvFeature.getChildAtIndex(0);
                GVariant gvVersion = gvFeature.getChildAtIndex(1);
                GVariant gvObjectPaths = gvFeature.getChildAtIndex(2);
                String name = gvName.getString();
                String version = gvVersion.getString();
                String[] objectPaths = gvObjectPaths.getArrayOfString();
                gvName.free();
                gvVersion.free();
                gvObjectPaths.free();
                gvFeature.free();
                if (LOG) Log.i(TAG, "Feature: name = " + name + ", version = " + version + "objectPaths = " + objectPaths);
                features.add(new DmsFeature(name, version, objectPaths));
            }
            result = features.toArray(new DmsFeature[features.size()]);
            if (LOG) Log.i(TAG, "getArrayDmsFeatureDBusProperty: size = " + result.length);
            gvResult.free();
        }
        return result;
    }

    private String[] getArrayOfStringDBusProperty(IServerClient client, String objectPath, String iface,
            String propName, Bundle extras) {
        String[] result = null;
        GVariant gvResult = getDBusProperty(client, objectPath, iface, propName, extras);
        if (gvResult != null) {
            if (LOG) Log.i(TAG, "getArrayOfStringDBusProperty: type = " + gvResult.getTypeString());
            // expecting type "as"
            List<String> strings = new ArrayList<String>();
            String[] gvStrings = gvResult.getArrayOfString();
            for (String string : gvStrings) {
                strings.add(string);
            }
            result = strings.toArray(new String[strings.size()]);
            if (LOG) Log.i(TAG, "getArrayOfStringDBusProperty: size = " + result.length);
            gvResult.free();
        }
        return result;
    }

    private String getStringDBusProperty(IServerClient client, String objectPath, String iface,
            String propName, Bundle extras) {
        String result = null;
        GVariant gvResult = getDBusProperty(client, objectPath, iface, propName, extras);
        if (gvResult != null) {
            result = gvResult.getString();
            gvResult.free();
        }
        if (LOG) Log.i(TAG, "getStringDBusProp: result=" + result);
        return result;
    }

    private boolean getBooleanDBusProperty(IServerClient client, String objectPath, String iface,
            String propName, Bundle extras) {
        boolean result = false;
        GVariant gvResult = getDBusProperty(client, objectPath, iface, propName, extras);
        if (gvResult != null) {
            result = gvResult.getBoolean();
            gvResult.free();
        }
        if (LOG) Log.i(TAG, "getBooleanDBusProp: result=" + result);
        return result;
    }

    private double getDoubleDBusProperty(IServerClient client, String objectPath, String iface,
            String propName, Bundle extras) {
        double result = 0;
        GVariant gvResult = getDBusProperty(client, objectPath, iface, propName, extras);
        if (gvResult != null) {
            result = gvResult.getDouble();
            gvResult.free();
        }
        if (LOG) Log.i(TAG, "getDoubleDBusProp: result=" + result);
        return result;
    }

    private long getLongDBusProperty(IServerClient client, String objectPath, String iface,
            String propName, Bundle extras) {
        long result = 0;
        GVariant gvResult = getDBusProperty(client, objectPath, iface, propName, extras);
        if (gvResult != null) {
            result = gvResult.getInt64();
            gvResult.free();
        }
        if (LOG) Log.i(TAG, "getLongDBusProp: result=" + result);
        return result;
    }

    private int getUInt32DBusProperty(IServerClient client, String objectPath, String iface,
            String propName, Bundle extras) {
        int result = 0;
        GVariant gvResult = getDBusProperty(client, objectPath, iface, propName, extras);
        if (gvResult != null) {
            result = gvResult.getUInt32();
            gvResult.free();
        }
        if (LOG) Log.i(TAG, "getUInt32DBusProp: result=" + result);
        return result;
    }

    private Bundle getDictDBusProperty(IServerClient client, String objectPath, String iface,
            String propName, Bundle extras) {
        Bundle result = null;
        GVariant gvResult = getDBusProperty(client, objectPath, iface, propName, extras);
        if (gvResult != null) {
            result = makeBundleFromDictionary(gvResult);
            gvResult.free();
        }
        if (LOG) Log.i(TAG, "getDictDBusProp: result=" + result);
        return result;
    }

    private GVariant getDBusProperty(IServerClient client, String objectPath, String iface,
            String propName, Bundle extras) {
        if (LOG) Log.i(TAG, String.format("getDBusProp: prop=%s obj=%s iface=%s", propName, objectPath, iface));
        GVariant result = null;
        RemoteObject ro = connector.getRemoteObject(objectPath, IFACE_DBUS_PROP);
        if (ro != null) {
            GVariant args = GVariant.newTupleStringString(iface, propName);
            Invocation invo = connector.dispatch(client, objectPath, ro, IFACE_DBUS_PROP, "Get", args);
            args.free();
            if (invo.success) {
                result = invo.result.getChildAtIndex(0);
                invo.result.free();
            } else {
                extras.putInt(Extras.KEY_ERR_CODE, invo.errCode);
                extras.putString(Extras.KEY_ERR_MSG, invo.errMessage);
            }
        }
        return result;
    }

    private String getStringInterfaceProperty(IServerClient client, String objectPath, String iface,
            String propName, Bundle extras) {
        String result = null;
        GVariant gvResult = getInterfaceProperty(client, objectPath, iface, propName, extras);
        if (gvResult != null) {
            result = gvResult.getString();
            gvResult.free();
        }
        if (LOG) Log.i(TAG, "getStringInterfaceProp: result=" + result);
        return result;
    }

    private boolean getBooleanInterfaceProperty(IServerClient client, String objectPath, String iface,
            String propName, Bundle extras) {
        boolean result = false;
        GVariant gvResult = getInterfaceProperty(client, objectPath, iface, propName, extras);
        if (gvResult != null) {
            result = gvResult.getBoolean();
            gvResult.free();
        }
        if (LOG) Log.i(TAG, "getBooleanInterfaceProp: result=" + result);
        return result;
    }

    private int getUInt32InterfaceProperty(IServerClient client, String objectPath, String iface,
            String propName, Bundle extras) {
        int result = 0;
        GVariant gvResult = getInterfaceProperty(client, objectPath, iface, propName, extras);
        if (gvResult != null) {
            result = gvResult.getUInt32();
            gvResult.free();
        }
        if (LOG) Log.i(TAG, "getUInt32InterfaceProp: result=" + result);
        return result;
    }

    private GVariant getInterfaceProperty(IServerClient client, String objectPath, String iface,
            String propName, Bundle extras) {
        if (LOG) Log.i(TAG, String.format("getInterfaceProp: prop=%s obj=%s iface=%s", propName, objectPath, iface));
        GVariant result = null;
        RemoteObject ro = connector.getRemoteObject(objectPath, IFACE_DBUS_PROP);
        if (ro != null) {
            GVariant args = GVariant.newTupleStringString(iface, propName);
            Invocation invo = connector.dispatch(client, objectPath, ro, IFACE_DBUS_PROP, "Get", args);
            args.free();
            if (invo.success) {
                result = invo.result.getChildAtIndex(0);
                invo.result.free();
            } else {
                extras.putInt(Extras.KEY_ERR_CODE, invo.errCode);
                extras.putString(Extras.KEY_ERR_MSG, invo.errMessage);
            }
        }
        return result;
    }

/*
    private void setDoubleDBusProperty(IServerClient client, String objectPath, String iface,
            String propName, double value, Bundle extras) {
        if (LOG) Log.i(TAG, String.format("setDoubleDBusProperty: prop=%s obj=%s iface=%s val=%f",
                propName, objectPath, iface, value));
        GVariant gvValue = GVariant.newDouble(value);
        setDBusProperty(client, objectPath, iface, propName, gvValue, extras);
        gvValue.free();
    }

    private void setDBusProperty(IServerClient client, String objectPath, String iface,
            String propName, GVariant value, Bundle extras) {
        RemoteObject ro = connector.getRemoteObject(objectPath, IFACE_DBUS_PROP);
        if (ro != null) {
            GVariant args = GVariant.newTupleStringStringVariant(iface, propName, value);
            Invocation invo = connector.dispatch(client, ro, IFACE_DBUS_PROP, "Set", args);
            args.free();
            if (!invo.success) {
                extras.putInt(Extras.KEY_ERR_CODE, invo.errCode);
                extras.putString(Extras.KEY_ERR_MSG, invo.errMessage);
            }
        }
    }
*/
/*
    private void doVoidMethodVoid(IServerClient client, String objectPath, String iface,
            String method, Bundle extras) {
        if (LOG) logDoMethod("doVoidMethodVoid", objectPath, iface, method);
        doMethod(client, objectPath, iface, method, null, extras);
    }

    private void doVoidMethodString(IServerClient client, String objectPath, String iface,
            String method, String arg, Bundle extras) {
        if (LOG) logDoMethod("doVoidMethodString", objectPath, iface, method);
        GVariant gvArgs = GVariant.newTupleString(arg);
        doMethod(client, objectPath, iface, method, gvArgs, extras);
        gvArgs.free();
    }

    private String doStringMethodString(IServerClient client, String objectPath, String iface,
            String method, String arg, Bundle extras) {
        if (LOG) logDoMethod("doStringMethodString", objectPath, iface, method);
        String result = null;
        GVariant gvArgs = GVariant.newTupleString(arg);
        GVariant gvResult = doMethod(client, objectPath, iface, method, gvArgs, extras);
        gvArgs.free();
        if (gvResult != null) {
            result = gvResult.getString();
            gvResult.free();
        }
        return result;
    }

    private void doVoidMethodLong(IServerClient client, String objectPath, String iface,
            String method, long arg, Bundle extras) {
        if (LOG) logDoMethod("doVoidMethodLong", objectPath, iface, method);
        GVariant gvArgs = GVariant.newTupleInt64(arg);
        doMethod(client, objectPath, iface, method, gvArgs, extras);
        gvArgs.free();
    }

    private void doVoidMethodStringString(IServerClient client, String objectPath, String iface,
            String method, String arg1, String arg2, Bundle extras) {
        if (LOG) logDoMethod("doVoidMethodStringString", objectPath, iface, method);
        GVariant gvArgs = GVariant.newTupleStringString(arg1, arg2);
        doMethod(client, objectPath, iface, method, gvArgs, extras);
        gvArgs.free();
    }

    private void doVoidMethodObjPathLong(IServerClient client, String objectPath, String iface,
            String method, String arg1, long arg2, Bundle extras) {
        if (LOG) logDoMethod("doVoidMethodStringLong", objectPath, iface, method);
        GVariant gvArgs = GVariant.newTupleObjPathInt64(arg1, arg2);
        doMethod(client, objectPath, iface, method, gvArgs, extras);
        gvArgs.free();
    }
*/

    private String doStringMethodVoid(IServerClient client, String objectPath, String iface,
            String method, Bundle extras) {
        if (LOG) logDoMethod("doStringMethodVoid", objectPath, iface, method);
        String result = null;
        GVariant gvResult = doMethod(client, objectPath, iface, method, null, extras);
        if (gvResult != null) {
            result = gvResult.getString();
            gvResult.free();
        }
        return result;
    }

    private Bundle[] doBundleArrayMethodStringArrayStringArray(IServerClient client, String objectPath, String iface,
            String method, String[] arg1, String[] arg2, Bundle extras) {
        if (LOG) logDoMethod("doBundleArrayMethodStringArrayStringArray", objectPath, iface, method);
        Bundle[] result = null;
        GVariant gvArgs = GVariant.newTupleStringArrayStringArray(arg1, arg2);
        if (LOG) Log.i(TAG, "doBundleArrayMethodStringArrayStringArray: args type = " + gvArgs.getTypeString());
        GVariant gvResult = doMethod(client, objectPath, iface, method, gvArgs, extras);
        gvArgs.free();
        if (gvResult != null) {
            if (LOG) Log.i(TAG, "doBundleArrayMethodStringArrayStringArray: result type = " + gvResult.getTypeString());
            // expecting type "aa{sv}"
            List<Bundle> bundles = new ArrayList<Bundle>();
            GVariant[] gvDicts = gvResult.getArrayOfGVariant();
            for (GVariant gvDict : gvDicts) {
                bundles.add(makeBundleFromDictionary(gvDict));
                gvDict.free();
            }
            result = bundles.toArray(new Bundle[bundles.size()]);
            if (LOG) Log.i(TAG, "doBundleArrayMethodStringArrayStringArray: result size = " + result.length);
            gvResult.free();
        }
        return result;
    }

    private Bundle[] doBundleArrayMethodIntIntStringArrayString(IServerClient client, String objectPath, String iface,
            String method, int arg1, int arg2, String[] arg3, String arg4, Bundle extras) {
        if (LOG) logDoMethod("doBundleArrayMethodIntIntStringArrayString", objectPath, iface, method);
        Bundle[] result = null;
        GVariant gvArgs = GVariant.newTupleIntIntStringArrayString(arg1, arg2, arg3, arg4);
        if (LOG) Log.i(TAG, "doBundleArrayMethodIntIntStringArrayString: args type = " + gvArgs.getTypeString());
        GVariant gvResult = doMethod(client, objectPath, iface, method, gvArgs, extras);
        gvArgs.free();
        if (gvResult != null) {
            if (LOG) Log.i(TAG, "doBundleArrayMethodIntIntStringArrayString: result type = " + gvResult.getTypeString());
            // expecting type "aa{sv}"
            List<Bundle> bundles = new ArrayList<Bundle>();
            GVariant[] gvDicts = gvResult.getArrayOfGVariant();
            for (GVariant gvDict : gvDicts) {
                bundles.add(makeBundleFromDictionary(gvDict));
                gvDict.free();
            }
            result = bundles.toArray(new Bundle[bundles.size()]);
            if (LOG) Log.i(TAG, "doBundleArrayMethodIntIntStringArrayString: result size = " + result.length);
            gvResult.free();
        }
        return result;
    }

    // TODO: handle the second return value
    private Bundle[] doBundleArrayMethodStringIntIntStringArrayString(IServerClient client, String objectPath, String iface,
            String method, String arg1, int arg2, int arg3, String[] arg4, String arg5, Bundle extras) {
        if (LOG) logDoMethod("doBundleArrayMethodStringIntIntStringArrayString", objectPath, iface, method);
        Bundle[] result = null;
        GVariant gvArgs = GVariant.newTupleStringIntIntStringArrayString(arg1, arg2, arg3, arg4, arg5);
        if (LOG) Log.i(TAG, "doBundleArrayMethodStringIntIntStringArrayString: args type = " + gvArgs.getTypeString());
        GVariant gvResult = doMethod(client, objectPath, iface, method, gvArgs, extras);
        gvArgs.free();
        if (gvResult != null) {
            if (LOG) Log.i(TAG, "doBundleArrayMethodStringIntIntStringArrayString: result type = " + gvResult.getTypeString());
            // expecting type "aa{sv}u"
            List<Bundle> bundles = new ArrayList<Bundle>();
            // TODO:  needs work all the way down to get 2nd return param
            //GVariant[] gvDicts = gvResult.getChildAtIndex(0).getArrayOfGVariant();
            //int retVal2 = gvResult.getChildAtIndex(1).getUInt32();
            GVariant[] gvDicts = gvResult.getArrayOfGVariant();
            for (GVariant gvDict : gvDicts) {
                bundles.add(makeBundleFromDictionary(gvDict));
                gvDict.free();
            }
            result = bundles.toArray(new Bundle[bundles.size()]);
            if (LOG) Log.i(TAG, "doBundleArrayMethodStringIntIntStringArrayString: result size = " + result.length + ", return value 2 = " /*+ retVal2*/);
            gvResult.free();
        }
        return result;
    }

    private GVariant doMethod(IServerClient client, String objectPath, String iface,
            String method, GVariant args, Bundle extras) {
        GVariant gvResult = null;
        RemoteObject ro = connector.getRemoteObject(objectPath, iface);
        if (ro != null) {
            Invocation invo = connector.dispatch(client, objectPath, ro, iface, method, args);
            if (invo.success) {
                gvResult = invo.result;
            } else {
                extras.putInt(Extras.KEY_ERR_CODE, invo.errCode);
                extras.putString(Extras.KEY_ERR_MSG, invo.errMessage);
            }
        }
        return gvResult;
    }

    private static void logDoMethod(String method, String objPath, String iface, String method2) {
        if (LOG) Log.i(TAG, String.format("%s: method=%s obj=%s iface=%s", method, method2, objPath, iface));
    }

    /*------------------+
     | IConnectorClient |
     +------------------*/

    public void onNotify(String objPath, String ifaceName, String notifName, long params) {
        if (LOG) Log.i(TAG, "onNotify: I=" + ifaceName + " N=" + notifName + " O=" + objPath);
        GVariant gvParams = new GVariant(params);
        if (ifaceName.equals(IFACE_MANAGER)) {
            if (notifName.equals("FoundServer")) {
                onServerFound(gvParams);
            } else if (notifName.equals("LostServer")) {
                onServerLost(gvParams);
            } else if (notifName.equals("LastChange")) {
                onLastChange(objPath, gvParams);
            } else {
                Log.w(TAG, "onNotify: unprocessed notification: I=" + ifaceName + " N=" + notifName + " O=" + objPath);
            }
        } else if (ifaceName.equals(IFACE_DBUS_PROP)) {
            if (notifName.equals("PropertiesChanged")) {
                onDBusPropertiesChanged(objPath, gvParams);
            } else {
                Log.w(TAG, "onNotify: unprocessed notification: I=" + ifaceName + " N=" + notifName + " O=" + objPath);
            }
        } else if (ifaceName.equals(IFACE_DEVICE)) {
            if (notifName.equals("ContainerUpdateIDs")) {
                onContainerUpdateIds(objPath, gvParams);
            } else if (notifName.equals("Changed")) {
                onDevicePropertiesChanged(objPath, gvParams);
            } else if (notifName.equals("UploadUpdate")) {
                onUploadUpdate(objPath, gvParams);
            } else {
                Log.w(TAG, "onNotify: unprocessed notification: I=" + ifaceName + " N=" + notifName + " O=" + objPath);
            }
        } else {
            Log.w(TAG, "onNotify: unprocessed notification: I=" + ifaceName + " N=" + notifName + " O=" + objPath);
        }
        gvParams.free();
    }

    private void onServerFound(GVariant gvParams) {
        GVariant gvObjPathServer = gvParams.getChildAtIndex(0);
        String objPathServer = gvObjPathServer.getString();
        gvObjPathServer.free();
        if (LOG) Log.i(TAG, "ServerFound: " + objPathServer);
        int n = clients.beginBroadcast();
        for (int i = 0; i < n; i++) {
            try {
                clients.getBroadcastItem(i).onServerFound(objPathServer);
            } catch (RemoteException e) {
            }
        }
        clients.finishBroadcast();
    }

    private void onServerLost(GVariant gvParams) {
        GVariant gvObjPathServer = gvParams.getChildAtIndex(0);
        String objPathServer = gvObjPathServer.getString();
        gvObjPathServer.free();
        if (LOG) Log.i(TAG, "ServerLost: " + objPathServer);
        int n = clients.beginBroadcast();
        for (int i = 0; i < n; i++) {
            try {
                clients.getBroadcastItem(i).onServerLost(objPathServer);
            } catch (RemoteException e) {
            }
        }
        clients.finishBroadcast();
    }

    private void onLastChange(String objPath, GVariant gvParams) {
        if (LOG) Log.i(TAG, "LastChange: " + objPath);
        int n = clients.beginBroadcast();
        for (int i = 0; i < n; i++) {
            try {
                clients.getBroadcastItem(i).onLastChange(objPath, null/*TODO: gvParams*/);
            } catch (RemoteException e) {
            }
        }
        clients.finishBroadcast();
    }

    private void onContainerUpdateIds(String objPath, GVariant gvParams) {
        if (LOG) Log.i(TAG, "ContainerUpdateIDs: " + objPath + ", type = " + gvParams.getTypeString());
        // expecting (a(ou))
        List<ContainerUpdateId> containerUpdateIds = new ArrayList<ContainerUpdateId>();
        GVariant gvContainer = gvParams.getChildAtIndex(0);
        GVariant[] gvContainerUpdateIds = gvContainer.getArrayOfGVariant();
        for (GVariant gvContainerUpdateId : gvContainerUpdateIds) {
            GVariant o = gvContainerUpdateId.getChildAtIndex(0);
            GVariant u = gvContainerUpdateId.getChildAtIndex(1);
            String oPath = o.getString();
            int uId = u.getUInt32();
            o.free();
            u.free();
            gvContainerUpdateId.free();
            if (LOG) Log.i(TAG, "ContainerUpdateIDs: objPath = " + oPath + ", id = " + uId);
            containerUpdateIds.add(new ContainerUpdateId(oPath, uId));
        }
        gvContainer.free();
        
        ContainerUpdateId[] updates = containerUpdateIds.toArray(new ContainerUpdateId[containerUpdateIds.size()]);
        if (LOG) Log.i(TAG, "ContainerUpdateIDs: updates size = " + updates.length);

        int n = clients.beginBroadcast();
        for (int i = 0; i < n; i++) {
            try {
                clients.getBroadcastItem(i).onContainerUpdateIds(objPath, updates);
            } catch (RemoteException e) {
            }
        }
        clients.finishBroadcast();
    }

    private void onDevicePropertiesChanged(String objPath, GVariant gvParams) {
        if (LOG) Log.i(TAG, "Changed: " + objPath + ", type = " + gvParams.getTypeString());
        // expecting (aa{sv})
        //List<DeviceChange> deviceChanges = new ArrayList<DeviceChange>();
        GVariant gvContainer = gvParams.getChildAtIndex(0);
        Log.w(TAG, "Changed: " + objPath + " " + gvContainer.getTypeString());
        GVariant[] gvChanged = gvContainer.getArrayOfGVariant();
        Log.w(TAG, "Changed: gv length = " + gvChanged.length);
        for (GVariant gvChange : gvChanged) {
            //GVariant gvDictionary = gvChange.getChildAtIndex(0);
            //Log.w(TAG, "Changed: " + gvDictionary.toString() + " " + gvDictionary.getTypeString());

            Bundle bundle = new Bundle();
            //GVariant gvEntries[] = gvDictionary.getArrayOfGVariant();
            GVariant gvEntries[] = gvChange.getArrayOfGVariant();
            for (GVariant gvEntry : gvEntries) {
                GVariant gvKey = gvEntry.getChildAtIndex(0);
                //Log.w(TAG, "Changed: " + gvKey.toString() + " " + gvKey.getTypeString());
                GVariant gvKeyValueVariant = gvEntry.getChildAtIndex(1);
                //Log.w(TAG, "Changed: " + gvKeyValueVariant.toString() + " " + gvKeyValueVariant.getTypeString());
                GVariant gvKeyValue = gvKeyValueVariant.getChildAtIndex(0);
                //Log.w(TAG, "Changed: " + gvKeyValue.toString() + " " + gvKeyValue.getTypeString());
                //TODO: add to some bundle or something... (bundle, gvKey.getString(), gvKeyValue);
                gvKeyValue.free();
                gvKeyValueVariant.free();
                gvKey.free();
                gvEntry.free();
            }
            //gvDictionary.free();
            gvChange.free();
        }
        gvContainer.free();

        int n = clients.beginBroadcast();
        for (int i = 0; i < n; i++) {
            try {
                clients.getBroadcastItem(i).onDevicePropertiesChanged(objPath, null/*TODO: gvParams*/);
            } catch (RemoteException e) {
            }
        }
        clients.finishBroadcast();
    }

    private void onUploadUpdate(String objPath, GVariant gvParams) {
        if (LOG) Log.i(TAG, "UploadUpdate: " + objPath + ", type = " + gvParams.getTypeString());
        // TODO: get from gvParams
        int id = 0;
        String status = "";
        long length = 0L;
        long total = 0L;

        int n = clients.beginBroadcast();
        for (int i = 0; i < n; i++) {
            try {
                clients.getBroadcastItem(i).onUploadUpdate(objPath, id, status, length, total);
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
    private static void addControllerProperty(Bundle bundle, String propName, GVariant gvPropValue) {
        ServerControllerProps.Enum e = ServerControllerProps.getEnum(propName);
        switch (e) {
        case SYSTEM_UPDATE_ID:
            int systemId = gvPropValue.getUInt32();
            bundle.putInt(propName, systemId);
            if (LOG) Log.i(TAG, "CtlrPropChange: " + propName + ": " + systemId);
            break;
        case UNKNOWN:
            Log.e(TAG, "Unknown server controller property: " + propName);
            break;
        default:
            Log.w(TAG, "Unhandled server controller property: " + propName);
            break;
        }
    }

    private static Bundle makeBundleFromBooleanDictionary(GVariant gvDictionary) {
        Bundle bundle = new Bundle();
        GVariant gvEntries[] = gvDictionary.getArrayOfGVariant();
        for (GVariant gvEntry : gvEntries) {
            GVariant gvEntryName = gvEntry.getChildAtIndex(0);
            GVariant gvEntryValue = gvEntry.getChildAtIndex(1);
            bundle.putBoolean(gvEntryName.getString(), gvEntryValue.getBoolean());
            gvEntryValue.free();
            gvEntryName.free();
            gvEntry.free();
        }
        return bundle;
    }

    private static Bundle makeBundleFromDictionary(GVariant gvDictionary) {
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
    private static void putToBundleMaybe(Bundle bundle, String key, GVariant gvValue) {
        String type = gvValue.getTypeString();
        boolean added = false;
        if (type.equals("o") || type.equals("s")) {
            bundle.putString(key, gvValue.getString());
            added = true;
        } else if (type.equals("i") || type.equals("u")) {
            bundle.putInt(key, gvValue.getUInt32());
            added = true;
        } else if (type.equals("x") || type.equals("t")) {
            bundle.putLong(key, gvValue.getInt64());
            added = true;
        } else if (type.equals("b")) {
            bundle.putBoolean(key, gvValue.getBoolean());
            added = true;
        } else if (type.equals("as")) {
            bundle.putStringArray(key, gvValue.getArrayOfString());
            added = true;
        } else if (type.equals("a{sb}") || type.equals("a(sb)")) {
            bundle.putBundle(key, makeBundleFromBooleanDictionary(gvValue));
            added = true;
        } else if (type.equals("aa{sv}")) {
            List<Bundle> bundles = new ArrayList<Bundle>();
            GVariant[] gvDicts = gvValue.getArrayOfGVariant();
            for (GVariant gvDict : gvDicts) {
                bundles.add(makeBundleFromDictionary(gvDict));
                gvDict.free();
            }
            Bundle[] bundleArray = bundles.toArray(new Bundle[bundles.size()]);
            bundle.putParcelableArray(key, bundleArray);
            added = true;
        } else {
            // TODO:
        }
        if (LOG) Log.i(TAG, String.format("putToBundle: %s name=%s type=%s",
                added ? "ADD " : "SKIP", key, type));
    }
}
