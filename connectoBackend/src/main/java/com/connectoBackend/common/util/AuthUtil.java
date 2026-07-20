package com.connectoBackend.common.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility methods for authentication.
 */
public final class AuthUtil {

    // -> Prevent instantiation
    private AuthUtil() {
    }

    // -> Get current authentication
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    // -> Get current authenticated username
    public static String getCurrentUsername() {

        Authentication authentication = getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return authentication.getName();
    }
}