package com.connectoBackend.common.constants;

/**
 * Contains API related constants.
 */
public final class ApiConstants {

    // -> Prevent instantiation
    private ApiConstants() {
    }

    // -> API Version
    public static final String API_VERSION = "/api/v1";

    // -> Authentication APIs
    public static final String AUTH = API_VERSION + "/auth";

    // -> User APIs
    public static final String USERS = API_VERSION + "/users";

    // -> Conversation APIs
    public static final String CONVERSATIONS = API_VERSION + "/conversations";

    // -> Message APIs
    public static final String MESSAGES = API_VERSION + "/messages";

    // -> Session APIs
    public static final String SESSIONS = API_VERSION + "/sessions";
}