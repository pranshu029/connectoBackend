package com.connectoBackend.auth.controller;

import com.connectoBackend.session.service.RefreshTokenService;
import com.connectoBackend.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller for refresh token management.
 */
@RestController
@RequestMapping("/api/v1/refresh-tokens")
@RequiredArgsConstructor
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;

    // -> Revoke a refresh token.
    @DeleteMapping("/{tokenHash}")
    public ApiResponse<Void> revokeRefreshToken(
            @PathVariable String tokenHash
    ) {

        refreshTokenService.revokeRefreshToken(tokenHash);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Refresh token revoked successfully.")
                .build();
    }

    // -> Revoke all refresh tokens of a user.
    @DeleteMapping("/users/{userId}")
    public ApiResponse<Void> revokeAllRefreshTokens(
            @PathVariable UUID userId
    ) {

        refreshTokenService.revokeAllRefreshTokens(userId);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("All refresh tokens revoked successfully.")
                .build();
    }
}