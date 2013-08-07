package com.intel.dleyna;

public class GError {
    public static native int getCodeNative(long peer);
    public static native String getMessageNative(long peer);
}
