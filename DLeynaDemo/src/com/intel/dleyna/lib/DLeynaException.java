package com.intel.dleyna.lib;

/**
 * Exceptions thrown by the DLeyna library.
 * <p>
 * You can use {@link #getErrorCode()} to retrieve the integer error code.
 */
public class DLeynaException extends Exception {

    private static final long serialVersionUID = 470272548682702033L;

    public static final int ERR_BAD_PATH = 0;
    public static final int ERR_OBJECT_NOT_FOUND = 1;
    public static final int ERR_BAD_QUERY = 2;
    public static final int ERR_OPERATION_FAILED = 3;
    public static final int ERR_BAD_RESULT = 4;
    public static final int ERR_UNKNOWN_INTERFACE = 5;
    public static final int ERR_UNKNOWN_PROPERTY = 6;
    public static final int ERR_DEVICE_NOT_FOUND = 7;
    public static final int ERR_DIED = 8;
    public static final int ERR_CANCELLED = 9;
    public static final int ERR_NOT_SUPPORTED = 10;
    public static final int ERR_LOST_OBJECT = 11;
    public static final int ERR_BAD_MIME = 12;
    public static final int ERR_HOST_FAILED = 13;
    public static final int ERR_IO = 14;

    private int errorCode;

    DLeynaException(int errorCode, String detailMessage) {
        super(detailMessage);
        this.errorCode = errorCode;
    }

    /**
     * Get the integer error code for this DLeyna exception.
     * @return one of the {@link DLeynaException}{@code .ERR_XXX} constants
     */
    public int getErrorCode() {
        return errorCode;
    }
};
