package com.connectoBackend.auth.controller;

import com.connectoBackend.auth.dto.request.LoginRequest;
import com.connectoBackend.auth.dto.request.RefreshTokenRequest;
import com.connectoBackend.auth.dto.request.SendOtpRequest;
import com.connectoBackend.auth.dto.request.VerifyOtpRequest;
import com.connectoBackend.auth.dto.response.AuthenticationResponse;
import com.connectoBackend.auth.dto.response.RegistrationVerificationResponse;
import com.connectoBackend.auth.service.AuthenticationService;
import com.connectoBackend.auth.service.EmailVerificationService;
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
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/send-otp")
    public ApiResponse<Void> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        emailVerificationService.sendOtp(request);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("OTP sent successfully.")
                .build();
    }

    @PostMapping("/verify-otp")
    public ApiResponse<RegistrationVerificationResponse> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request
    ) {
        return ApiResponse.<RegistrationVerificationResponse>builder()
                .success(true)
                .message("Email verified successfully.")
                .data(emailVerificationService.verifyOtp(request))
                .build();
    }

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