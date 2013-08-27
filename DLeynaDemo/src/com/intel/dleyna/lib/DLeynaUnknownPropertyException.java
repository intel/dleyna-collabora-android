package com.intel.dleyna.lib;

/**
 * The DLeyna "unknown property" exception.
 * <p>
 * This special case of a {@link DLeynaException} is often less exceptional than the others,
 * since many properties are optional.
 * <p>
 * When retrieving multiple properties,
 * you may find that catching this for each optional property
 * within a global catch of {@link DLeynaException} simplifies your code.
 */
public class DLeynaUnknownPropertyException extends DLeynaException {

    private static final long serialVersionUID = 4617115911557533931L;

    DLeynaUnknownPropertyException(String detailMessage) {
        super(ERR_UNKNOWN_PROPERTY, detailMessage);
    }
}
