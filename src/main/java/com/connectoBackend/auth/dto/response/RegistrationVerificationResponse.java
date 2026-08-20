package com.connectoBackend.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegistrationVerificationResponse {

    private String email;
    private String registrationToken;
}