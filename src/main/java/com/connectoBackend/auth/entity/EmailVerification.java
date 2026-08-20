package com.connectoBackend.auth.entity;

import com.connectoBackend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "email_verifications", indexes = {
        @Index(name = "idx_email_verifications_email", columnList = "email", unique = true),
        @Index(name = "idx_email_verifications_token_hash", columnList = "registration_token_hash")
})
public class EmailVerification extends BaseEntity {

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "otp_hash", nullable = false, length = 64)
    private String otpHash;

    @Column(name = "otp_expires_at", nullable = false)
    private LocalDateTime otpExpiresAt;

    @Column(name = "last_sent_at", nullable = false)
    private LocalDateTime lastSentAt;

    @Column(name = "send_count", nullable = false)
    private int sendCount;

    @Column(name = "send_window_started_at")
    private LocalDateTime sendWindowStartedAt;

    @Column(name = "failed_attempts", nullable = false)
    private int failedAttempts;

    @Column(nullable = false)
    private boolean verified;

    @Column(name = "registration_token_hash", length = 64)
    private String registrationTokenHash;

    @Column(name = "token_expires_at")
    private LocalDateTime tokenExpiresAt;

    @Column(name = "token_consumed", nullable = false)
    private boolean tokenConsumed;
}