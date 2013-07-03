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
 * will first call {@link #setJNIEnv()} to provide the native layer with the JNI "env"
 * pointer for this thread.
 * That thread will then call the native function dleyna_main_loop_start(),
 * passing it an IConnector instance.
 * That will in turn call {@link #initialize(String, String, int)},
 * then g_main_loop_new(),
 * then {@link #connect(String, long, long)},
 * and then g_main_loop_run().
 * <p>
 * Almost all the other methods of this interface will then be called from the g_main_loop
 * on the daemon thread:
 * {@link #watchClient(String)},
 * {@link #unwatchClient(String)},
 * {@link #setClientLostCallback(long)},
 * {@link #publishObject(long, String, boolean, int, long)},
 * {@link #publishSubtree(long, String, long[], long)},
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
 * We necessarily invoke the callbacks on some thread other than the deamon thread,
 * but down in the native implementation we arrange for them to be executed on
 * the daemon thread.
 *
 * @author Tom Keel
 *
 */
public interface IConnector {

    /**
     * Downward call, before calling dleyna_main_loop_start().
     * Provide the native layer with the JNI "environment" pointer.
     */
    public void setJNIEnv();

    /**
     * Upward call from dleyna_main_loop_start().
     * Provides various information about the service.
     * @param serverInfo xml document describing the manager object
     * @param rootInfo xml document describing the root object
     * @param errorQuark
     * @return true iff success
     */
    public boolean initialize(String serverInfo, String rootInfo, int errorQuark);

    /**
     * Upward call, after the g_main_loop exits, and after {@link #disconnect()} has been called.
     * Clean up.
     */
    public void shutdown();

    /**
     * Upward call from dleyna_main_loop_start(),
     * after {@link #initialize}, before the g_main_loop loop starts.
     * We don't really need to do anything other than call the callback,
     * but we have to delay doing that until the g_main_loop is running.
     * @param connectedCb native function to call to indicate connection
     * @param disconnectedCb a native function to call down to when disconnected
     */
    public void connect(String serverName, long connectedCb, long disconnectedCb);

    /**
     * Upward call, after the g_main_loop finishes, before {@link #shutdown()}.
     */
    public void disconnect();

    /**
     * Upward call on the g_main_loop.
     * Start tracking the given client.
     * @param clientName
     * @return ?
     */
    public boolean watchClient(String clientName);

    /**
     * Upward call on the g_main_loop.
     * Stop tracking the given client.
     * @param clientName
     */
    public void unwatchClient(String clientName);

    /**
     * Upward call on the g_main_loop.
     * Provides a callback to use to notify the service that a tracked client is gone.
     * @param lostCb
     */
    public void setClientLostCallback(long lostCb);

    /**
     * Upward call on the g_main_loop.
     * Notification that a new remote object is available.
     * @param connectorId
     * @param objectPath
     * @param isRoot
     * @param interfaceIndex
     * @param dispatchCb
     * @return the id of the object
     */
    public int publishObject(long connectorId, String objectPath, boolean isRoot,
            int interfaceIndex, long dispatchCb);

    /**
     * Upward call on the g_main_loop.
     * Notification that a new remote subtree is available.
     * @param connectorId
     * @param objectPath
     * @param dispatchCb
     * @param interfaceFilterCb
     * @return the id of the subtree
     */
    public int publishSubtree(long connectorId, String objectPath, long dispatchCb[],
            long interfaceFilterCb);

    /**
     * Upward call on the g_main_loop.
     * Notification that the given remote object is no longer available.
     * @param connectorId
     * @param ObjectId
     */
    public void unpublishObject(long connectorId, int ObjectId);

    /**
     * Upward call on the g_main_loop.
     * Notification that the given remote subtree is no longer available.
     * @param connectorId
     * @param objectId
     */
    public void unpublishSubtree(long connectorId, int objectId);

    /**
     * Upward call on the g_main_loop.
     * Asynchronous positive return from a dispatched method invocation.
     * @param messageId
     * @param parameters
     */
    public void returnResponse(long messageId, Object parameters);

    /**
     * Upward call on the g_main_loop.
     * Asynchronous negative return from a dispatched method invocation.
     * @param messageId
     * @param errorCode
     */
    public void returnError(long messageId, int errorCode);

    /**
     * Upward call on the g_main_loop.
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
}
