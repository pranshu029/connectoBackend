package com.connectoBackend.auth.entity;

import com.connectoBackend.common.entity.BaseEntity;
import com.connectoBackend.session.enums.DeviceType;
import com.connectoBackend.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Represents a refresh token issued to a user.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_refresh_tokens_user_id", columnList = "user_id"),
                @Index(name = "idx_refresh_tokens_token_hash", columnList = "token_hash"),
                @Index(name = "idx_refresh_tokens_expires_at", columnList = "expires_at")
        }
)
public class RefreshToken extends BaseEntity {

    // -> Owner of the refresh token.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_refresh_tokens_user")
    )
    private User user;

    // -> SHA-256 hash of the refresh token.
    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    // -> Expiration time.
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    // -> Revocation status.
    @Column(name = "revoked", nullable = false)
    private boolean revoked;

    // -> Device identifier.
    @Column(name = "device_id", nullable = false, length = 100)
    private String deviceId;

    // -> Issued IP address.
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    // -> User-Agent header.
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    // -> Device name.
    @Column(name = "device_name", nullable = false, length = 100)
    private String deviceName;

    // -> Device type.
    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", nullable = false, length = 20)
    private DeviceType deviceType;

    // -> Operating system.
    @Column(name = "operating_system", length = 100)
    private String operatingSystem;

    // -> Browser.
    @Column(name = "browser", length = 100)
    private String browser;

}