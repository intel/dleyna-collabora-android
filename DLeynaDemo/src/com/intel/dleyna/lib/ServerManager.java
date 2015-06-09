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

package com.intel.dleyna.lib;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

/**
 * This class enables applications to discover Digital Media Servers
 * on local networks attached to this device (or on the device itself).
 * Each such DMR is represented by an instance of {@link Server}.
 * <p>
 * Use {@link #ServerManager(ServerManagerListener)} to to obtain an instance of this class
 * and register for notification of events.
 * <p>
 * Use {@link #connect(Context)} to initiate the connection to the background server service.
 * <p>
 * Use {@link #disconnect()} to break the connection and deallocate connection resources.
 * <p>
 * While connected to the background server service,
 * you can use {@link #getServers()} to obtain a list of all currently available servers,
 * and you will be notified of the appearance and disappearance of servers in callbacks to
 * the {@link ServerManagerListener} object that you passed to
 * {@link #ServerManager(ServerManagerListener)}.
 * Those notifications will run on the application's main thread.
 */
public class ServerManager {

    private static final boolean LOG = false;
    private static final String TAG = "ServerMgr";

    /** The application package containing the Server service. */
    private static final String SERVER_SERVICE_PACKAGE = "com.intel.dleyna.dleynademo";

    /** The class implementing the Server service. */
    private static final String SERVER_SERVICE_CLASS = "com.intel.dleyna.ServerService";

    /** The Binder interface to the Server service. */
    private IServerService serverService;

    /** The context from which {@link #connect(Context)} was called. */
    private Context context;

    /** Do we have a binder to the service? */
    private boolean serviceBound;

    /** Are we connected to the service? */
    private boolean serviceConnected;

    /** The event listener. */
    private ServerManagerListener listener;

    /** Maps "object path" strings to servers. */
    private HashMap<String,Server> servers = new HashMap<String,Server>();

    /** The handler we use to run notifications on the main thread. */
    private Handler handler;

    /**
     * Construct a manager instance, and register for notification of events.
     * Note that you will only receive events after invoking {@link #connect(Context)}.
     * @param listener an instance of your extension of {@link ServerManagerListener},
     * for receiving notification of events.
     */
    public ServerManager(ServerManagerListener listener) {
        handler = new Handler(Looper.getMainLooper());
        this.listener = listener;
    }

    /**
     * Initiate a connection to the background server service.
     * <p>
     * Connection establishment is asynchronous --
     * if this method succeeds, you will later receive a callback to the
     * {@link ServerManagerListener#onConnected()} method of the {@link ServerManagerListener}
     * object you passed to{@link #ServerManager(ServerManagerListener)}, or to
     * {@link ServerManagerListener#onDisconnected()}
     * if something went wrong in the meantime.
     * <p>
     * This method could fail if, for example, the application package containing
     * the server service hasn't been installed on the device.
     * <p>
     * You would typically call this method from {@link Activity#onResume()}.
     * @param context the context in which this is being called, typically an {@link Activity}.
     * @return true on success, false on failure
     */
    public boolean connect(Context context) {
        if (LOG) Log.i(TAG, "connect: bound=" + serviceBound + " connected=" + serviceConnected);
        if (!serviceBound) {
            if (LOG) Log.i(TAG, "connect: binding");
            this.context = context;
            Intent intent = new Intent();
            intent.setClassName(SERVER_SERVICE_PACKAGE, SERVER_SERVICE_CLASS);
            try {
                serviceBound = context.bindService(intent, serverConnection, Context.BIND_AUTO_CREATE);
            } catch (SecurityException e) {
                if (LOG) Log.e(TAG, "connect: " + e);
            }
            if (!serviceBound) {
                if (LOG) Log.e(TAG, "connect: can't bind to server service");
                // For some crazy reason you have to unbind a service even if
                // the bind failed, or you'll get a "leaked ServiceConnection" warning.
                context.unbindService(serverConnection);
            }
            // Note: even though unbinding the service does not necessarily result in an
            // onServiceDisconnected() callback, i.e. even though we could be here with
            // serviceConnected == true, we *will* get another onServiceConnected() callback.
        }
        return serviceBound;
    }

    /**
     * Disconnect from the background server service.
     * <p>
     * You would typically call this method from {@link Activity#onPause()}.
     */
    public void disconnect() {
        if (LOG) Log.i(TAG, "disconnect: bound=" + serviceBound + " connected=" + serviceConnected);
        if (serviceConnected) {
            if (LOG) Log.i(TAG, "disconnect: unregistering client");
            try {
                serverService.unregisterClient(serverClient);
            } catch (RemoteException e) {
                if (LOG) Log.e(TAG, "disconnect: unregisterClient: " + e);
            }
            serviceConnected = false;
        }
        if (serviceBound) {
            context.unbindService(serverConnection);
            // We should be on the main thread, but even so, we don't want recursion.
            handler.post(new Runnable() {
                public void run() {
                    listener.onDisconnected();
                }
            });
            serviceBound = false;
        }
        servers.clear();
        if (LOG) Log.i(TAG, "disconnect: DONE");
    }

    private final ServiceConnection serverConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder b) {
            if (LOG) Log.i(TAG, "onServiceConnected: bound=" + serviceBound + " connected=" + serviceConnected);
            // Note: the service could have been re-bound and re-connected automatically
            // following a service process crash, in which case serviceBound would be false
            // here. So set it true.
            serviceBound = true;
            serviceConnected = true;
            serverService = IServerService.Stub.asInterface(b);
            try {
                serverService.registerClient(serverClient);
                listener.onConnected();
            } catch (RemoteException e) {
                // I'm supposing that if we get here, then onServiceDisconnected()
                // will still be called, so do nothing special.
                e.printStackTrace();
            }
        }

        public void onServiceDisconnected(ComponentName arg0) {
            if (LOG) Log.i(TAG, "onServiceDisconnected: bound=" + serviceBound + " connected=" + serviceConnected);
            serviceConnected = false;
            if (serviceBound) {
                listener.onDisconnected();
                serviceBound = false;
            }
        }
    };

    /**
     * Get all known servers.
     * <p>
     * Note: if there are no known servers, this returns an array of length 0.
     * @return all currently known servers
     * @throws RemoteException no connection to the background server service
     * @throws DLeynaException failure reported by the background server service
     */
    public Server[] getServers() throws RemoteException, DLeynaException {
        if (!serviceConnected) {
            throw new RemoteException();
        }

        // In what follows we construct a new map representing the new current set
        // of servers, reusing the Server objects of any servers that were already
        // in the previous map. Then we substitute the new map for the previous one.

        Bundle extras = new Bundle();
        String[] newObjectIds = serverService.getServers(serverClient, extras);
        Extras.throwExceptionIfError(extras);
        // newObjectIds is the new complete set of object ids according to the service
        if (LOG) Log.i(TAG, "getServers: " + newObjectIds);

        if (newObjectIds != null && newObjectIds.length > 0) {
            for (Server s : servers.values()) {
                s.setIsObsolete(true);
            }
            // the 'obsolete' flag is set for all elements of the previous map
            List<Server> deltaServers = new LinkedList<Server>();
            for (String objectPath : newObjectIds) {
                Server s = servers.get(objectPath);
                if (s == null) {
                    s = new Server(ServerManager.this, objectPath);
                    deltaServers.add(s);
                } else {
                    s.setIsObsolete(false);
                }
            }
            // deltaServers is the set of servers that appeared since the previous set was built
            // the 'obsolete' flag is set only for the truly obsolete elements of the previous map
            HashMap<String, Server> nextServers = new HashMap<String, Server>();
            for (Server s : servers.values()) {
                if (!s.getIsObsolete()) {
                    nextServers.put(s.getObjectPath(), s);
                }
            }
            for (Server s : deltaServers) {
                nextServers.put(s.getObjectPath(), s);
            }
            // nextServers is the new current map of servers.
            servers = nextServers;
        }
        return servers.values().toArray(new Server[servers.size()]);
    }

    /**
     * Get the version number of this implementation of dLeyna-server.
     * @return version number
     * @throws RemoteException no connection to the background server service
     * @throws DLeynaException failure reported by the background server service
     */
    public String getVersion() throws RemoteException, DLeynaException {
        if (!serviceConnected) {
            throw new RemoteException();
        }

        Bundle extras = new Bundle();
        String version = serverService.getVersion(serverClient, extras);
        Extras.throwExceptionIfError(extras);

        if (LOG) Log.i(TAG, "getVersion: " + version);
        return version;
    }
    
    /**
     * Cause a GuPNP rescan to be performed.
     * <p>
     * Servers that haven't responded after a few seconds will be considered unavailable.
     * <p>
     * This may result in callbacks to {@link ServerManagerListener#onServerFound(Server)}
     * and/or {@link ServerManagerListener#onServerLost(Server)} on registered observers.
     * @throws RemoteException no connection to the background server service
     * @throws DLeynaException failure reported by the background server service
     */
    public void rescan() throws RemoteException, DLeynaException {
        if (!serviceConnected) {
            throw new RemoteException();
        }
        Bundle extras = new Bundle();
        serverService.rescan(serverClient, extras);
        Extras.throwExceptionIfError(extras);
    }

    /**
     * This is the Binder that we pass to the ServerService.
     * It serves both to identify us as a particular client of the ServerService, and
     * to receive callbacks from the ServerService.
     * The callbacks run on some arbitrary Binder thread.
     * We forward the callbacks to the main thread by using {@link #handler}.
     */
    private final IServerClient serverClient = new IServerClient.Stub() {

        /*-----------------------+
         | ServerManagerListener |
         +-----------------------*/

        public void onServerFound(final String objectPath) {
            if (LOG) Log.i(TAG, "onServerFound: " + objectPath);
            handler.post(new Runnable() {
                public void run() {
                    if (servers.containsKey(objectPath)) {
                        if (LOG) Log.e(TAG, "onServerFound: REDUNDANT: " + objectPath);
                    } else {
                        Server s = new Server(ServerManager.this, objectPath);
                        servers.put(objectPath, s);
                        listener.onServerFound(s);
                    }
                }
            });
        }

        public void onServerLost(final String objectPath) {
            if (LOG) Log.i(TAG, "onServerLost: " + objectPath);
            handler.post(new Runnable() {
                public void run() {
                    Server s = servers.get(objectPath);
                    if (s == null) {
                        if (LOG) Log.e(TAG, "onServerLost: MISSING: " + objectPath);
                    } else {
                        listener.onServerLost(s);
                        servers.remove(objectPath);
                    }
                }
            });
        }

        public void onLastChange(final String objectPath, final Bundle props) {
            if (LOG) Log.i(TAG, "onLastChange: " + objectPath);
            handler.post(new Runnable() {
                public void run() {
                    // TODO ?
                }
            });
        }

        /*---------------------------+
         | IServerControllerListener |
         +---------------------------*/

        public void onControllerPropertiesChanged(final String objectPath, final Bundle props) {
            if (LOG) logControllerPropertiesChanged(objectPath, props);
            final Server s = servers.get(objectPath);
            if (s == null) {
                if (LOG) Log.e(TAG, "CtlrPropChange: MISSING: " + objectPath);
                return;
            }

            handler.post(new Runnable() {
                public void run() {
                    for (IServerControllerListener l : s.getControllerListeners()) {
                        for (Iterator<String> it = props.keySet().iterator(); it.hasNext(); ) {
                            String propName = it.next();
                            ServerControllerProps.Enum e = ServerControllerProps.getEnum(propName);
                            switch (e) {
                            case SYSTEM_UPDATE_ID:
                                l.onSystemUpdateId(s, props.getInt(propName));
                                break;
                            case UNKNOWN:
                                Log.e(TAG, "Unknown server controller property: " + propName);
                                break;
                            default:
                                Log.e(TAG, "Unhandled server controller property: " + propName);
                                break;
                            }
                        }
                    }
                }
            });
        }

        private void logControllerPropertiesChanged(String objectPath, Bundle props) {
            if (LOG) Log.i(TAG, "CtlrPropChange: " + objectPath);
            Iterator<String> it = props.keySet().iterator();
            while (it.hasNext()) {
                String propName = it.next();
                ServerControllerProps.Enum e = ServerControllerProps.getEnum(propName);
                switch (e) {
                case SYSTEM_UPDATE_ID:
                    int systemId = props.getInt(propName);
                    if (LOG) Log.i(TAG, "CtlrPropChange: " + propName + ": " + systemId);
                    break;
                case UNKNOWN:
                    Log.e(TAG, "Unknown server controller property: " + propName);
                    break;
                default:
                    Log.e(TAG, "Unhandled server controller property: " + propName);
                    break;
                }
            }
        }

        /*----------------------+
         | IMediaDeviceListener |
         +----------------------*/

         public void onContainerUpdateIds(final String objectPath, final ContainerUpdateId[] containerUpdateIds) {
             if (LOG) {
                 Log.i(TAG, "MediaDevice: " + objectPath + " onContainerUpdateIds[" + containerUpdateIds.length + "]...");
                 for (ContainerUpdateId update : containerUpdateIds) {
                     Log.i(TAG, "  Update path = " + update.objectPath + ", id = " + update.id);
                 }
             }
         }

         public void onDevicePropertiesChanged(final String objectPath, final Bundle props) {
              Log.w(TAG, "MediaDevice: " + objectPath + " onDevicePropertiesChanged -- TODO");
         }

         public void onUploadUpdate(final String objectPath, final int id, final String status, final long length, final long total) {
              Log.w(TAG, "MediaDevice: " + objectPath + " onUploadUpdate -- TODO");
         }
     };
     
     IServerService getServerService() {
         return serverService;
     }

     IServerClient getServerClient() {
         return serverClient;
     }
}
