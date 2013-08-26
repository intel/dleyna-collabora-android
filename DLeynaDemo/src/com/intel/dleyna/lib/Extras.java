package com.intel.dleyna.lib;

import android.os.Bundle;

public class Extras {

    public static final String KEY_ERR_CODE = "ErrCode";
    public static final String KEY_ERR_MSG  = "ErrMsg";

    public static void throwExceptionIfError(Bundle extras) throws DLeynaException {
        if (extras.containsKey(KEY_ERR_CODE)) {
            int errCode = extras.getInt(KEY_ERR_CODE);
            String errMsg = extras.getString(KEY_ERR_MSG);
            if (errCode == DLeynaException.ERR_UNKNOWN_PROPERTY) {
                throw new DLeynaUnknownPropertyException(errMsg);
            } else {
                throw new DLeynaException(errCode, errMsg);
            }
        }
    }
}
