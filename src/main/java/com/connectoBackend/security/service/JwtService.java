package com.connectoBackend.security.service;

import com.connectoBackend.security.enums.TokenType;
import com.connectoBackend.security.model.JwtClaims;
import com.connectoBackend.user.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

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
    UUID extractUserId(String token);

    // -> Extract token type.
    TokenType extractTokenType(String token);

    // -> Extract all application claims.
    JwtClaims extractClaims(String token);

    // -> Validate token against user.
    boolean isTokenValid(String token, UserDetails userDetails);

    // -> Check token expiration.
    boolean isTokenExpired(String token);
}