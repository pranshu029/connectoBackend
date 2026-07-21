package com.connectoBackend.user.dto.response;

import com.connectoBackend.user.enums.AccountStatus;
import com.connectoBackend.user.enums.Gender;
import com.connectoBackend.user.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO representing a user's profile.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private UUID id;

    private String firstName;

    private String lastName;

    private String fullName;

    private String username;

    private String email;

    private String phoneNumber;

    private String profilePictureUrl;

    private String coverPictureUrl;

    private String bio;

    private LocalDate dateOfBirth;

    private Gender gender;

    private UserRole role;

    private AccountStatus accountStatus;

    private boolean emailVerified;

    private LocalDateTime lastSeenAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}