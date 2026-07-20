package com.connectoBackend.session.entity;

import com.connectoBackend.common.entity.BaseEntity;
import com.connectoBackend.session.enums.DeviceType;
import com.connectoBackend.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Represents a logged-in user session.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "user_sessions",
        indexes = {
                @Index(name = "idx_user_sessions_user_id", columnList = "user_id"),
                @Index(name = "idx_user_sessions_device_id", columnList = "device_id"),
                @Index(name = "idx_user_sessions_active", columnList = "active"),
                @Index(name = "idx_user_sessions_expires_at", columnList = "expires_at")
        }
)
public class UserSession extends BaseEntity {

    // -> User owning this session.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_user_sessions_user")
    )
    private User user;

    // -> Unique identifier for the device.
    @Column(name = "device_id", nullable = false, length = 100)
    private String deviceId;

    // -> Friendly device name.
    @Column(name = "device_name", nullable = false, length = 100)
    private String deviceName;

    // -> Device category.
    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    @Column(name = "device_type", nullable = false, length = 20)
    private DeviceType deviceType;

    // -> Operating system.
    @Column(name = "operating_system", length = 100)
    private String operatingSystem;

    // -> Browser name.
    @Column(name = "browser", length = 100)
    private String browser;

    // -> IP address.
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    // -> User-Agent header.
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    // -> Last activity timestamp.
    @Column(name = "last_activity_at", nullable = false)
    private LocalDateTime lastActivityAt;

    // -> Session expiry timestamp.
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    // -> Indicates whether the session is active.
    @Column(name = "active", nullable = false)
    private Boolean active;
}