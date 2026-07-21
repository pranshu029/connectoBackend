package com.connectoBackend.user.entity;

import com.connectoBackend.common.entity.BaseEntity;
import com.connectoBackend.user.enums.AccountStatus;
import com.connectoBackend.user.enums.Gender;
import com.connectoBackend.user.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents an application user.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_users_email", columnList = "email"),
                @Index(name = "idx_users_username", columnList = "username"),
                @Index(name = "idx_users_account_status", columnList = "account_status")
        }
)
public class User extends BaseEntity {

    @Size(max = 100)
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Size(max = 100)
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Size(min = 3, max = 30)
    @Pattern(
            regexp = "^[a-zA-Z0-9._]+$",
            message = "Username may contain only letters, numbers, dots and underscores."
    )
    @Column(name = "username", nullable = false, unique = true, length = 30)
    private String username;

    @Email
    @Size(max = 255)
    @Column(name = "email", nullable = false, unique = true, length = 255, updatable = false)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Pattern(
            regexp = "^[+]?[0-9]{7,15}$",
            message = "Invalid phone number."
    )
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "profile_picture_url", length = 500)
    private String profilePictureUrl;

    @Size(max = 500)
    @Column(name = "bio", length = 500)
    private String bio;

    @Past
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 30)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false, length = 30)
    private AccountStatus accountStatus;

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified;

    @Column(name = "last_seen_at")
    private LocalDateTime lastSeenAt;

}