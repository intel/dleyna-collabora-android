package com.intel.dleyna;

public interface IConnectorClient {

    public void onNotify(String objPath, String ifaceName, String notifName, long params);

}
