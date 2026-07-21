package com.connectoBackend.common.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

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

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null) {
            return null;
        }

        return authentication.getName();
    }

    public static boolean hasRole(String role) {

        Authentication authentication = getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        return authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals(role));
    }
}