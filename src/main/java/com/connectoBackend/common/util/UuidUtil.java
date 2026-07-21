package com.connectoBackend.common.util;

import java.util.UUID;

/**
 * Utility methods for UUID operations.
 */
public final class UuidUtil {

    private UuidUtil() {
    }

    // -> Generate a random UUID.
    public static UUID generate() {

        return UUID.randomUUID();
    }

}