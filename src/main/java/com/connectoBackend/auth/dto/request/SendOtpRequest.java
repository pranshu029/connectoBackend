package com.connectoBackend.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendOtpRequest {

    @Email(message = "Invalid email format.")
    @NotBlank(message = "Email is required.")
    private String email;
}