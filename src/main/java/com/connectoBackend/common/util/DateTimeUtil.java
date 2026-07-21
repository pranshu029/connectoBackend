package com.connectoBackend.common.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Utility methods for date and time operations.
 */
public final class DateTimeUtil {

    private DateTimeUtil() {
    }

    // -> Get current UTC time.
    public static LocalDateTime now() {

        return LocalDateTime.now(ZoneOffset.UTC);
    }

    // -> Check whether a date is expired.
    public static boolean isExpired(LocalDateTime dateTime) {

        return dateTime.isBefore(now());
    }

}