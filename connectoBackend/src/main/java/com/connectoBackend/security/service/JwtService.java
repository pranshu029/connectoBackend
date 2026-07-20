package com.connectoBackend.security.service;

import com.connectoBackend.security.enums.TokenType;
import com.connectoBackend.security.model.JwtClaims;
import com.connectoBackend.user.entity.User;

/**
 * Service responsible for JWT generation and validation.
 */
public interface JwtService {

    // -> Generate access token.
    String generateAccessToken(User user);

    // -> Generate refresh token.
    String generateRefreshToken(User user);

    // -> Extract username (email) from token.
    String extractUsername(String token);

    // -> Extract user id from token.
    String extractUserId(String token);

    // -> Extract token type.
    TokenType extractTokenType(String token);

    // -> Extract all application claims.
    JwtClaims extractClaims(String token);

    // -> Validate token against user.
    boolean isTokenValid(String token, User user);

    // -> Check token expiration.
    boolean isTokenExpired(String token);
}