package com.connectoBackend.security.model;

import com.connectoBackend.security.enums.TokenType;
import lombok.Builder;

import java.util.UUID;

/**
 * Immutable model representing JWT claims used by the application.
 */
@Builder
public record JwtClaims(

        UUID userId,

        String email,

        String username,

        String role,

        TokenType tokenType

) {
}