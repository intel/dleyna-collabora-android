package com.intel.dleyna;

/***
 * Everything you never wanted to know about a remote object.
 */
public class RemoteObject {
    public final int id;
    public final long connectorId;
    public final String objectPath;
    public final boolean isRoot;
    public final int interfaceIndex;
    public final long dispatchCb;

    public RemoteObject(int id, long connectorId, String objectPath, boolean isRoot, int interfaceIndex,long dispatchCb) {
        this.id = id;
        this.connectorId = connectorId;
        this.objectPath = objectPath;
        this.isRoot = isRoot;
        this.interfaceIndex = interfaceIndex;
        this.dispatchCb = dispatchCb;
    }
}
