package com.connectoBackend.session.service;

import com.connectoBackend.auth.entity.RefreshToken;
import com.connectoBackend.session.dto.DeviceInfo;

import java.util.UUID;

/**
 * Service for refresh token management.
 */
public interface RefreshTokenService {

    // -> Create and persist a refresh token.
    RefreshToken createRefreshToken(
            UUID userId,
            String tokenHash,
            DeviceInfo deviceInfo
    );

    // -> Validate a refresh token.
    RefreshToken validateRefreshToken(String tokenHash);

    // -> Revoke a refresh token.
    void revokeRefreshToken(String tokenHash);

    // -> Revoke all refresh tokens of a user.
    void revokeAllRefreshTokens(UUID userId);

    // -> Remove expired refresh tokens.
    void removeExpiredRefreshTokens();

}