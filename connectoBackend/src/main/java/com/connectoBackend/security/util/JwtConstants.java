package com.connectoBackend.security.util;

/**
 * JWT claim names used across the application.
 */
public final class JwtConstants {

    private JwtConstants() {
    }

    public static final String USER_ID = "userId";

    public static final String USERNAME = "username";

    public static final String ROLE = "role";

    public static final String TOKEN_TYPE = "tokenType";
}