package com.connectoBackend.auth.service.impl;

import com.connectoBackend.auth.entity.RefreshToken;
import com.connectoBackend.auth.repository.RefreshTokenRepository;
import com.connectoBackend.auth.service.RefreshTokenService;
import com.connectoBackend.common.exception.ResourceNotFoundException;
import com.connectoBackend.user.entity.User;
import com.connectoBackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of {@link RefreshTokenService}.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${application.security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Override
    public RefreshToken createRefreshToken(
            UUID userId,
            String tokenHash,
            String deviceId,
            String ipAddress,
            String userAgent
    ) {

        User user = getUser(userId);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .deviceId(deviceId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpiration))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken validateRefreshToken(String tokenHash) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByTokenHashAndRevokedFalse(tokenHash)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Refresh token not found."));

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResourceNotFoundException("Refresh token has expired.");
        }

        return refreshToken;
    }

    @Override
    public void revokeRefreshToken(String tokenHash) {

        RefreshToken refreshToken = validateRefreshToken(tokenHash);

        refreshToken.setRevoked(true);

        refreshTokenRepository.save(refreshToken);
    }

    @Override
    public void revokeAllRefreshTokens(UUID userId) {

        User user = getUser(userId);

        List<RefreshToken> refreshTokens =
                refreshTokenRepository.findAllByUserAndRevokedFalse(user);

        refreshTokens.forEach(token -> token.setRevoked(true));

        refreshTokenRepository.saveAll(refreshTokens);
    }

    @Override
    public void removeExpiredRefreshTokens() {

        List<RefreshToken> refreshTokens =
                refreshTokenRepository.findAllByRevokedFalseAndExpiresAtBefore(LocalDateTime.now());

        refreshTokens.forEach(token -> token.setRevoked(true));

        refreshTokenRepository.saveAll(refreshTokens);
    }

    // -> Fetch user or throw exception.
    private User getUser(UUID userId) {

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));
    }
}