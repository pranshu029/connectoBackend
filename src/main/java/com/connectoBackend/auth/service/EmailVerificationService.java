package com.connectoBackend.auth.service;

import com.connectoBackend.auth.dto.request.SendOtpRequest;
import com.connectoBackend.auth.dto.request.VerifyOtpRequest;
import com.connectoBackend.auth.dto.response.RegistrationVerificationResponse;

public interface EmailVerificationService {

    void sendOtp(SendOtpRequest request);

    RegistrationVerificationResponse verifyOtp(VerifyOtpRequest request);

    void consumeRegistrationToken(String email, String registrationToken);
}