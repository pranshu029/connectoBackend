package com.connectoBackend.user.entity;

import com.connectoBackend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Represents a blocked user relationship.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "blocked_users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_blocked_user",
                        columnNames = {
                                "user_id",
                                "blocked_user_id"
                        }
                )
        }
)
public class BlockedUser extends BaseEntity {

    // -> User who blocks
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    // -> Blocked user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User blockedUser;
}