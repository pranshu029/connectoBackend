package com.connectoBackend.user.entity;

import com.connectoBackend.common.entity.BaseEntity;
import com.connectoBackend.user.enums.ConnectionStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "connections",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_connection_user_target",
                        columnNames = {"user_id", "target_user_id"}
                )
        },
        indexes = {
                @Index(name = "idx_connection_user", columnList = "user_id"),
                @Index(name = "idx_connection_target_user", columnList = "target_user_id"),
                @Index(name = "idx_connection_status", columnList = "status")
        }
)
public class Connection extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id", nullable = false)
    private User targetUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ConnectionStatus status;
}
