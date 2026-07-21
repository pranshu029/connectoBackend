package com.connectoBackend.auth.service;

import com.connectoBackend.auth.dto.request.LoginRequest;
import com.connectoBackend.auth.dto.request.RefreshTokenRequest;
import com.connectoBackend.auth.dto.response.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Service responsible for authentication operations.
 */
public interface AuthenticationService {

    // -> Authenticate user and generate JWT tokens.
    AuthenticationResponse login(LoginRequest request, HttpServletRequest httpServletRequest);

    // -> Generate a new access token using a refresh token.
    AuthenticationResponse refreshToken(RefreshTokenRequest request);

    // -> Logout the current session.
    void logout(String refreshToken);

}