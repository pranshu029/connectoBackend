package com.connectoBackend.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * Response returned after successful authentication.
 */
@Getter
@Builder
public class AuthenticationResponse {

    private String accessToken;

    private String refreshToken;

    private String tokenType;

    private long expiresIn;

}