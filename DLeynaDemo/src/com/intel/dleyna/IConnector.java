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

/**
 * IConnector is the interface to a native DLeyna service.
 * <p>
 * Some thread, which we here call the "daemon" thread
 * (and which is *not* the "main" thread in the Android sense),
 * will call the native function dleyna_main_loop_start()
 * which in turn will call {@link #initialize(String, String, int, long)},
 * then {@link #connect(String, ConnectionCallback)},
 * and then g_main_loop_run().
 * <p>
 * Almost all the other methods of this interface will then be called from the g_main_loop
 * on the daemon thread:
 * {@link #watchClient(String)},
 * {@link #unwatchClient(String)},
 * {@link #setClientLostCallback(ClientLostCallback)},
 * {@link #publishObject(long, String, boolean, int, DispatchCallback)},
 * {@link #publishSubtree(long, String, DispatchCallback[], InterfaceFilterCallback)},
 * {@link #unpublishObject(long, int)},
 * {@link #unpublishSubtree(long, int)},
 * {@link #returnResponse(long, Object)},
 * {@link #returnError(long, int)},
 * {@link #notify(long, String, String, String, Object, int)}
 * <p>
 * The exceptions are {@link #disconnect()} and {@link #shutdown()}
 * which will be called, in that order, on the daemon thread,
 * just after the g_main_loop exits.
 * <p>
 * Some of the functions of this interface supply us with callbacks.
 * We necessarily invoke the callbacks on some thread other than the main thread,
 * but down in the native implementation we arrange for them to be executed in
 * the main loop.
 *
 * @author Tom Keel
 *
 */
public interface IConnector {

    /**
     * Called before anything else.
     * Provides various information about the service.
     * @param serverInfo xml document describing the manager object
     * @param rootInfo xml document describing the root object
     * @param errorQuark
     * @param userData
     * @return true iff success
     */
    public boolean initialize(String serverInfo, String rootInfo, int errorQuark, long userData);

    /**
     * Called after the g_main_loop exits, and after {@link #disconnect()} has been called,
     * on the daemon thread.
     * Clean up.
     */
    public void shutdown();

    /**
     * Called after {@link #initialize}, before the g_main_loop loop starts.
     * We don't really need to do anything other than call the callback,
     * but we have to delay doing that until the g_main_loop is running.
     * @param serverName
     * @param connCb
     */
    public void connect(String serverName, ConnectionCallback connCb);

    /**
     * Called after the g_main_loop finishes, before {@link #shutdown()}.
     */
    public void disconnect();

    /**
     * Start tracking the given client.
     * @param clientName
     * @return ?
     */
    public boolean watchClient(String clientName);

    /**
     * Stop tracking the given client.
     * @param clientName
     */
    public void unwatchClient(String clientName);

    /**
     * Provides a callback to use to notify the service that a tracked client is gone.
     * @param lostCb
     */
    public void setClientLostCallback(ClientLostCallback lostCb);

    /**
     * Notification that a new remote object is available.
     * @param connectorId
     * @param objectPath
     * @param isRoot
     * @param interfaceIndex
     * @param dispCb
     * @return the id of the object
     */
    public int publishObject(long connectorId, String objectPath, boolean isRoot,
            int interfaceIndex, DispatchCallback dispCb);

    /**
     * Notification that a new remote subtree is available.
     * @param connectorId
     * @param objectPath
     * @param dispCb
     * @param filterCb
     * @return the id of the subtree
     */
    public int publishSubtree(long connectorId, String objectPath, DispatchCallback dispCb[],
            InterfaceFilterCallback filterCb);

    /**
     * Notification that the given remote object is no longer available.
     * @param connectorId
     * @param ObjectId
     */
    public void unpublishObject(long connectorId, int ObjectId);

    /**
     * Notification that the given remote subtree is no longer available.
     * @param connectorId
     * @param objectId
     */
    public void unpublishSubtree(long connectorId, int objectId);

    /**
     * Asynchronous positive return from a dispatched method invocation.
     * @param messageId
     * @param parameters
     */
    public void returnResponse(long messageId, Object parameters);

    /**
     * Asynchronous negative return from a dispatched method invocation.
     * @param messageId
     * @param errorCode
     */
    public void returnError(long messageId, int errorCode);

    /**
     * Broadcasted notification of some event from a remote object.
     * @param connectorId
     * @param objectPath
     * @param interfaceName
     * @param notificationName
     * @param parameters
     * @param errorCode
     * @return ?
     */
    public boolean notify(long connectorId, String objectPath, String interfaceName,
            String notificationName, Object parameters, int errorCode);

    /*-----------+
     | Callbacks |
     +-----------*/

    public interface ConnectionCallback {
        public void onConnected(long connectorId);
        public void onDisconnected(long connectorId);
    }

    public interface ClientLostCallback {
        public void onClientLost(String clientName);
    }

    public interface DispatchCallback {
        public void onDispatch(long connectorId, String sender, String objectId, String interfc,
                String method, Object parameters, long messageId);
    }

    public interface InterfaceFilterCallback {
        public boolean onFilterChanged(String objectPath, String node, String interfc);
    }
}
