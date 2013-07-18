package com.intel.dleyna;

public interface IConnectorClient {

    public boolean onNotify(String objPath, String ifaceName, String notifName, long params,
            long gErrPtr);

}
