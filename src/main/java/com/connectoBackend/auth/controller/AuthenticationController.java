package com.connectoBackend.auth.controller;

import com.connectoBackend.auth.dto.request.LoginRequest;
import com.connectoBackend.auth.dto.request.RefreshTokenRequest;
import com.connectoBackend.auth.dto.response.AuthenticationResponse;
import com.connectoBackend.auth.service.AuthenticationService;
import com.connectoBackend.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    // -> Authenticate user.
    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpServletRequest
    ) {

        AuthenticationResponse response =
                authenticationService.login(request, httpServletRequest);

        return ApiResponse.<AuthenticationResponse>builder()
                .success(true)
                .message("Login successful.")
                .data(response)
                .build();
    }

    // -> Refresh access token.
    @PostMapping("/refresh")
    public ApiResponse<AuthenticationResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {

        AuthenticationResponse response =
                authenticationService.refreshToken(request);

        return ApiResponse.<AuthenticationResponse>builder()
                .success(true)
                .message("Access token refreshed successfully.")
                .data(response)
                .build();
    }

    // -> Logout current user.
    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            @Valid @RequestBody RefreshTokenRequest request
    ) {

        authenticationService.logout(request.getRefreshToken());

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Logout successful.")
                .build();
    }
}