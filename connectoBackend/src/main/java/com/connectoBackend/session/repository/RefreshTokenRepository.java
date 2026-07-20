package com.connectoBackend.session.repository;

import com.connectoBackend.session.entity.RefreshToken;
import com.connectoBackend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link RefreshToken}.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    // -> Find refresh token by hash.
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    // -> Find valid refresh token.
    Optional<RefreshToken> findByTokenHashAndRevokedFalse(String tokenHash);

    // -> Find all active refresh tokens of a user.
    List<RefreshToken> findAllByUserAndRevokedFalse(User user);

    // -> Check whether a valid refresh token exists.
    boolean existsByTokenHashAndRevokedFalse(String tokenHash);

    // -> Find expired active refresh tokens.
    List<RefreshToken> findAllByRevokedFalseAndExpiresAtBefore(
            LocalDateTime expiresAt
    );

    // -> Delete all refresh tokens of a user.
    void deleteAllByUser(User user);

}