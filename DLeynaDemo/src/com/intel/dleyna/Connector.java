package com.intel.dleyna;

import android.util.Log;
import android.util.SparseArray;

import com.intel.dleyna.lib.IRendererClient;


/**
 * A Connector provides the interface to a native DLeyna service.
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
 * The following methods will then be called from the g_main_loop on the daemon thread:
 * {@link #watchClient(String)},
 * {@link #unwatchClient(String)},
 * {@link #setClientLostCallback(long)},
 * {@link #publishObject(String, boolean, int, long)},
 * {@link #publishSubtree(String, long[], long)},
 * {@link #unpublishObject(int)},
 * {@link #unpublishSubtree(int)},
 * {@link #returnResponse(long, long)},
 * {@link #returnError(long, long)},
 * {@link #notify(String, String, String, long, long)}
 * <p>
 * Then {@link #disconnect()} and {@link #shutdown()} will be called, in that order,
 * on the daemon thread, just after the g_main_loop exits.
 * <p>
 * Some of the functions of this interface supply us with callbacks.
 * We necessarily invoke the callbacks on some thread other than the daemon thread,
 * we arrange for them to be forwarded to the daemon thread.
 */
public class Connector {

    private static final boolean LOG = true;
    private static final String TAG = "Connector";

    /**
     * The current set of remote objects.
     * The first object to show up should be the Manager object
     * (though we don't presume so),
     * and it should be around until shutdown.
     * Renderer objects come and go.
     */
    private SparseArray<RemoteObject> objects = new SparseArray<RemoteObject>();

    /** We attribute ids to remote objects starting with 1. */
    private int lastAssignedObjectId;

    /**
     * The current set of pending method invocations.
     */
    private SparseArray<Invocation> pendingInvocations = new SparseArray<Invocation>();

    /** We attribute ids to outstanding method invocations starting with 1. */
    private int lastAssignedInvocationId;

    private String managerObjectPath;
    private int managerObjectId;

    private GMainLoop gMainLoop;

    private IConnectorClient client;

    /**
     * Construct a connector instance.
     * @param client who will get callbacks
     * @param managerObjectPath identifies the manager object for this connector
     */
    public Connector(IConnectorClient client, String managerObjectPath) {
        this.client = client;
        this.managerObjectPath = managerObjectPath;
    }

    public RemoteObject getManagerObject() {
        return objects.get(managerObjectId);
    }

    public void waitForManagerObject() {
        synchronized(this) {
            while (managerObjectId == 0) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    /**
     * Downward call, before calling dleyna_main_loop_start().
     * Provide the native layer with the JNI "environment" pointer.
     * This MUST be called on the thread that will run the g_main_loop.
     */
    public void setJNIEnv() {
        setJNIEnvNative();
        gMainLoop = new GMainLoop();
    }

    private native void setJNIEnvNative();

    /**
     * Upward call from dleyna_main_loop_start().
     * Provides various information about the service.
     * @param serverInfo xml document describing the manager object
     * @param rootInfo xml document describing the root object
     * @param errorQuark
     * @return true iff success
     */
    public boolean initialize(String serverInfo, String rootInfo, int errorQuark) {
        if (LOG) Log.i(TAG, "initialize: root=" + rootInfo + " quark=" + errorQuark);
        return true;
    }

    /**
     * Upward call, after the g_main_loop exits, and after {@link #disconnect()} has been called.
     * Clean up.
     */
    public void shutdown() {
        if (LOG) Log.i(TAG, "shutdown");
    }

    /**
     * Upward call from dleyna_main_loop_start(),
     * after {@link #initialize}, before the g_main_loop loop starts.
     * We don't really need to do anything other than call the callback,
     * but we have to delay doing that until the g_main_loop is running.
     * @param connectedCb native function to call to indicate connection
     * @param disconnectedCb a native function to call down to when disconnected
     */
    public void connect(String serverName, long connectedCb, long disconnectedCb) {
        if (LOG) Log.i(TAG, "connect");
    }

    /**
     * Upward call, after the g_main_loop finishes, before {@link #shutdown()}.
     */
    public void disconnect() {
        if (LOG) Log.i(TAG, "disconnect");
    }

    /**
     * Upward call on the g_main_loop.
     * Start tracking the given client.
     * @param clientName
     * @return ?
     */
    public boolean watchClient(String clientName) {
        if (LOG) Log.i(TAG, "watchClient");
        return true;
    }

    /**
     * Upward call on the g_main_loop.
     * Stop tracking the given client.
     * @param clientName
     */
    public void unwatchClient(String clientName) {
        if (LOG) Log.i(TAG, "unwatchClient");
    }

    /**
     * Upward call on the g_main_loop.
     * Provides a callback to use to notify the service that a tracked client is gone.
     * @param lostCb
     */
    public void setClientLostCallback(long lostCb) {
        if (LOG) Log.i(TAG, "setClientLostCallback");
    }

    /**
     * Upward call on the g_main_loop.
     * Notification that a new remote object is available.
     * @param objectPath
     * @param isRoot
     * @param interfaceIndex
     * @param dispatchCb
     * @return the id of the object
     */
    public int publishObject(String objectPath, boolean isRoot, int interfaceIndex,
            long dispatchCb) {
        if (LOG) Log.i(TAG, "publishObject: " + objectPath + " " + isRoot + " " + interfaceIndex);

        // Add this object to the collection.
        lastAssignedObjectId++;
        RemoteObject ro = new RemoteObject(lastAssignedObjectId, objectPath, isRoot,
                interfaceIndex, dispatchCb);
        objects.append(ro.id, ro);

        // If it's the manager object, note its id and unblock waitForManagerObject().
        if (objectPath.equals(managerObjectPath)) {
            synchronized (this) {
                managerObjectId = lastAssignedObjectId;
                this.notify();
            }
        }

       return ro.id;
    }

    /**
     * Upward call on the g_main_loop.
     * Notification that a new remote subtree is available.
     * @param objectPath
     * @param dispatchCb
     * @param interfaceFilterCb
     * @return the id of the subtree
     */
    public int publishSubtree(String objectPath, long[] dispatchCb, long interfaceFilterCb) {
        if (LOG) Log.i(TAG, "publishSubtree");
        return 0;
    }

    /**
     * Upward call on the g_main_loop.
     * Notification that the given remote object is no longer available.
     * @param ObjectId
     */
    public void unpublishObject(int ObjectId) {
        if (LOG) Log.i(TAG, "unpublishObject");
    }

    /**
     * Upward call on the g_main_loop.
     * Notification that the given remote subtree is no longer available.
     * @param objectId
     */
    public void unpublishSubtree(int objectId) {
        if (LOG) Log.i(TAG, "unpublishSubtree");
    }

    /**
     * Upward call on the g_main_loop.
     * Asynchronous positive return from a dispatched method invocation.
     * @param messageId
     * @param paramsAsGVariant
     */
    public void returnResponse(long messageId, long paramsAsGVariant) {
        if (LOG) Log.i(TAG, "returnResponse: messageId=" + messageId + " result=" + paramsAsGVariant);
        returnResult((int)messageId, true, paramsAsGVariant);
    }

    /**
     * Upward call on the g_main_loop.
     * Asynchronous negative return from a dispatched method invocation.
     * @param messageId
     * @param errorAsGError
     */
    public void returnError(long messageId, long errorAsGError) {
        if (LOG) Log.i(TAG, "returnError: messageId=" + messageId + " err=" + errorAsGError);
        returnResult((int)messageId, false, errorAsGError);
    }

    private void returnResult(int id, boolean success, long result) {
        Invocation invocation;
        synchronized(pendingInvocations) {
            invocation = pendingInvocations.get(id);
            if (invocation != null) {
                pendingInvocations.remove(id);
            }
        }
        if (invocation != null) {
            synchronized(invocation) {
                invocation.done = true;
                invocation.success = success;
                // Remove the wrapper around the response.
                invocation.result = result == 0 ? null :
                        GVariant.getFromNativeContainerAtIndex(result, 0);
                invocation.notify();
            }
        } else {
            throw new Error("No pending result!");
        }
    }

    /**
     * Upward call on the g_main_loop.
     * Broadcasted notification of some event from a remote object.
     * @param objPath
     * @param ifaceName
     * @param notifName
     * @param params parameters as a native GVariant
     * @param gErrPtr native pointer to GError
     * @return ?
     */
    public boolean notify(String objPath, String ifaceName, String notifName, long params,
            long gErrPtr) {
        return client.onNotify(objPath, ifaceName, notifName, params, gErrPtr);
    }

    /**
     * Downward call to invoke a method in the native service.
     * This is called on some binder thread.
     * We transfer the call to the daemon thread, and wait for the response.
     * @param client the client
     * @param obj the remote object info
     * @param args
     */
    public Invocation dispatch(final IRendererClient client, final RemoteObject obj, final String iface,
            final String func, final GVariant args) {

        if (LOG) Log.i(TAG, "dispatch: " + client.asBinder() + " " + obj + " " + " " + iface + " "
                + func + " " + args);

        // Add a new Invocation in our list of pending Invocations.
        final int id;
        final Invocation invocation;
        synchronized(pendingInvocations) {
            id = ++lastAssignedInvocationId;
            invocation = new Invocation(id);
            pendingInvocations.append(id, invocation);
        }

        // Schedule the invocation to run on the g_main_loop.
        gMainLoop.gIdleAdd(new Runnable() {
            public void run() {
                if (LOG) Log.i(TAG, "dispatch: RUNNING");
                dispatchNative(obj.dispatchCb, client.asBinder().toString(), obj.objectPath, iface, func, args, id);
            }
        });

        // Wait for the response.
        if (LOG) Log.i(TAG, "dispatch: WAITING id=" + invocation.id);
        synchronized (invocation) {
            while (!invocation.done) {
                try {
                    invocation.wait();
                } catch (InterruptedException e) {
                }
            }
        }

        if (LOG) Log.i(TAG, "dispatch: DONE!!!");
        return invocation;
    }

    private native void dispatchNative(long dispatchFuncAddr, String sender, String objectId,
            String iface, String method, GVariant args, long msgId);

    /**
     * The status of a dispatched method invocation.
     */
    public static class Invocation {
        /** A unique identifier for this invocation. */
        public int id;
        /** Has this invocation finished? */
        public boolean done = false;
        /** Did this invocation succeed? */
        public boolean success = false;
        /** If success, output parameters as a GVariant, else as GError. */
        public GVariant result = null;

        Invocation(int id) {
            this.id = id;
        }
    }
}
