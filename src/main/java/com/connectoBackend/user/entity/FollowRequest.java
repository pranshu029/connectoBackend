package com.connectoBackend.user.entity;

import com.connectoBackend.common.entity.BaseEntity;
import com.connectoBackend.user.enums.FollowRequestStatus;
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
        name = "follow_requests",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_follow_request_sender_target",
                        columnNames = {"requester_id", "target_user_id"}
                )
        },
        indexes = {
                @Index(name = "idx_follow_request_requester", columnList = "requester_id"),
                @Index(name = "idx_follow_request_target", columnList = "target_user_id"),
                @Index(name = "idx_follow_request_status", columnList = "status")
        }
)
public class FollowRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id", nullable = false)
    private User targetUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private FollowRequestStatus status;
}
