package com.connectoBackend.user.dto.request;

import com.connectoBackend.user.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Request DTO for creating a user.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    @NotBlank
    @Size(max = 100)
    private String firstName;

    @NotBlank
    @Size(max = 100)
    private String lastName;

    @NotBlank
    @Size(min = 3, max = 30)
    @Pattern(regexp = "^[a-zA-Z0-9._]+$")
    private String username;

    @NotBlank
    @Email
    @Size(max = 255)
    private String email;

    @NotBlank(message = "Registration verification token is required.")
    private String registrationToken;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    @Pattern(regexp = "^[+]?[0-9]{7,15}$")
    private String phoneNumber;

    @Size(max = 500)
    private String bio;

    @Past
    private LocalDate dateOfBirth;

    private Gender gender;
}