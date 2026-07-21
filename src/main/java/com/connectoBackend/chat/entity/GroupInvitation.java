package com.connectoBackend.chat.entity;

import com.connectoBackend.chat.enums.InvitationStatus;
import com.connectoBackend.common.entity.BaseEntity;
import com.connectoBackend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Represents an invitation sent to a user to join a group conversation.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "group_invitations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_group_invitation_conversation_invited_user",
                        columnNames = {
                                "conversation_id",
                                "invited_user_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_group_invitation_conversation",
                        columnList = "conversation_id"
                ),
                @Index(
                        name = "idx_group_invitation_invited_user",
                        columnList = "invited_user_id"
                ),
                @Index(
                        name = "idx_group_invitation_status",
                        columnList = "status"
                )
        }
)

public class GroupInvitation extends BaseEntity {

    // -> Group conversation.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    // -> User who sent the invitation.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by_user_id", nullable = false)
    private User invitedBy;

    // -> User receiving the invitation.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_user_id", nullable = false)
    private User invitedUser;

    // -> Current invitation status.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InvitationStatus status;

    // -> Invitation expiry time.
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    // -> Time when the invitation was responded to.
    private LocalDateTime respondedAt;

}