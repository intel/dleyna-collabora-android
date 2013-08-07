package com.intel.dleyna.lib;

/**
 * Base class for exceptions thrown by the DLeyna library.
 */
@SuppressWarnings("serial")
public class DLeynaException extends Exception {

    public DLeynaException() {
    }

    public DLeynaException(String detailMessage) {
        super(detailMessage);
    }

    public DLeynaException(String name, Throwable cause) {
        super(name, cause);
    }

    public DLeynaException(Exception cause) {
        super(cause);
    }
};
