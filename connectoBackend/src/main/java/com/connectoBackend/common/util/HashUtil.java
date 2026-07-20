package com.connectoBackend.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for hashing.
 */
public final class HashUtil {

    private HashUtil() {
    }

    // -> Generate SHA-256 hash.
    public static String sha256(String value) {

        try {

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

            byte[] hash = messageDigest.digest(
                    value.getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder builder = new StringBuilder();

            for (byte b : hash) {
                builder.append(String.format("%02x", b));
            }

            return builder.toString();

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "Unable to generate SHA-256 hash.",
                    exception
            );
        }
    }
}