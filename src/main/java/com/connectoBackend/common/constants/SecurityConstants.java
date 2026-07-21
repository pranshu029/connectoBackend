package com.connectoBackend.common.constants;

/**
 * Security related constants.
 */
public final class SecurityConstants {

    private SecurityConstants() {
    }

    // -> HTTP headers.
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String USER_AGENT_HEADER = "User-Agent";
    public static final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For";

    // -> Token information.
    public static final String BEARER = "Bearer";
    public static final String BEARER_PREFIX = "Bearer ";

}