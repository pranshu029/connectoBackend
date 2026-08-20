package com.connectoBackend.session.service;

import com.connectoBackend.session.entity.UserSession;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing user sessions.
 */
public interface UserSessionService {

    // -> Create a new session.
    UserSession createSession(
            UUID userId,
            String deviceId,
            String deviceName,
            String operatingSystem,
            String browser,
            String ipAddress,
            String userAgent
    );

    // -> Get all active sessions of a user.
    List<UserSession> getActiveSessions(UUID requesterId, UUID userId);

    // -> Update last activity.
    void updateLastActivity(UUID sessionId);

    // -> Logout current session.
    void logout(UUID requesterId, UUID sessionId);

    // -> Logout all sessions of a user.
    void logoutAll(UUID requesterId, UUID userId);

    // -> Remove expired sessions.
    void removeExpiredSessions();
}