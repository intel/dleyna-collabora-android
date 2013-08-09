package com.intel.dleyna.lib;

import android.os.Bundle;

public class Extras {

    public static final String KEY_ERR_CODE = "ErrCode";
    public static final String KEY_ERR_MSG  = "ErrMsg";

    public static void throwExceptionIfError(Bundle extras) throws DLeynaException {
        if (extras.containsKey(Extras.KEY_ERR_CODE)) {
            throw new DLeynaException(
                    extras.getInt(Extras.KEY_ERR_CODE),
                    extras.getString(Extras.KEY_ERR_MSG));
        }
    }
}
