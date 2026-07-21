package com.connectoBackend.session.repository;

import com.connectoBackend.session.entity.UserSession;
import com.connectoBackend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link UserSession}.
 */
@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {

    // -> Find session by device id.
    Optional<UserSession> findByDeviceId(String deviceId);

    // -> Find active session by device id.
    Optional<UserSession> findByDeviceIdAndActiveTrue(String deviceId);

    // -> Find all active sessions of a user.
    List<UserSession> findAllByUserAndActiveTrue(User user);

    // -> Find all sessions of a user.
    List<UserSession> findAllByUser(User user);

    // -> Check whether an active session exists for a device.
    boolean existsByDeviceIdAndActiveTrue(String deviceId);

    // -> Delete all sessions of a user.
    void deleteAllByUser(User user);

    // -> Find expired active sessions.
    List<UserSession> findAllByActiveTrueAndExpiresAtBefore(LocalDateTime dateTime);
}