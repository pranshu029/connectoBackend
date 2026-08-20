package com.connectoBackend.session.service.impl;

import com.connectoBackend.common.exception.ResourceNotFoundException;
import com.connectoBackend.common.exception.ForbiddenException;
import com.connectoBackend.session.entity.UserSession;
import com.connectoBackend.session.enums.DeviceType;
import com.connectoBackend.session.repository.UserSessionRepository;
import com.connectoBackend.session.service.UserSessionService;
import com.connectoBackend.user.entity.User;
import com.connectoBackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of {@link UserSessionService}.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserSessionServiceImpl implements UserSessionService {

    private static final long SESSION_EXPIRY_DAYS = 30;

    private final UserSessionRepository userSessionRepository;
    private final UserRepository userRepository;

    @Override
    public UserSession createSession(
            UUID userId,
            String deviceId,
            String deviceName,
            String operatingSystem,
            String browser,
            String ipAddress,
            String userAgent
    ) {

        User user = getUser(userId);

        UserSession session = UserSession.builder()
                .user(user)
                .deviceId(deviceId)
                .deviceName(deviceName)
                .deviceType(DeviceType.UNKNOWN)
                .operatingSystem(operatingSystem)
                .browser(browser)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .lastActivityAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(SESSION_EXPIRY_DAYS))
                .active(true)
                .build();

        return userSessionRepository.save(session);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSession> getActiveSessions(UUID requesterId, UUID userId) {
        requireSameUser(requesterId, userId);
        return userSessionRepository.findAllByUserAndActiveTrue(getUser(userId));
    }

    @Override
    public void updateLastActivity(UUID sessionId) {

        UserSession session = getSession(sessionId);
        session.setLastActivityAt(LocalDateTime.now());

        userSessionRepository.save(session);
    }

    @Override
    public void logout(UUID requesterId, UUID sessionId) {

        UserSession session = getSession(sessionId);
        requireSameUser(requesterId, session.getUser().getId());
        session.setActive(false);

        userSessionRepository.save(session);
    }

    @Override
    public void logoutAll(UUID requesterId, UUID userId) {

        requireSameUser(requesterId, userId);
        List<UserSession> sessions =
                userSessionRepository.findAllByUserAndActiveTrue(getUser(userId));

        sessions.forEach(session -> session.setActive(false));

        userSessionRepository.saveAll(sessions);
    }

    @Override
    public void removeExpiredSessions() {

        List<UserSession> sessions =
                userSessionRepository.findAllByActiveTrueAndExpiresAtBefore(LocalDateTime.now());

        sessions.forEach(session -> session.setActive(false));

        userSessionRepository.saveAll(sessions);
    }

    private User getUser(UUID userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    private UserSession getSession(UUID sessionId) {

        return userSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found."));
    }

    private void requireSameUser(UUID requesterId, UUID ownerId) {
        if (!requesterId.equals(ownerId)) {
            throw new ForbiddenException("You can manage only your own sessions.");
        }
    }
}