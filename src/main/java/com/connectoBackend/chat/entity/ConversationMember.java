package com.connectoBackend.chat.entity;

import com.connectoBackend.chat.enums.ConversationMemberRole;
import com.connectoBackend.chat.enums.MemberStatus;
import com.connectoBackend.common.entity.BaseEntity;
import com.connectoBackend.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Represents a user's membership in a conversation.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "conversation_members",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_conversation_member",
                        columnNames = {
                                "conversation_id",
                                "user_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_member_conversation",
                        columnList = "conversation_id"
                ),
                @Index(
                        name = "idx_member_user",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_member_role",
                        columnList = "role"
                )
        }
)
public class ConversationMember extends BaseEntity {

    // -> Conversation to which the user belongs.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "conversation_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_member_conversation"
            )
    )
    private Conversation conversation;

    // -> Member of the conversation.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_member_user"
            )
    )
    private User user;

    // -> Role of the member.
    @Enumerated(EnumType.STRING)
    @Column(
            name = "role",
            nullable = false,
            length = 30
    )
    private ConversationMemberRole role;

        // -> Membership status.
        @Enumerated(EnumType.STRING)
        @Column(
                        name = "status",
                        nullable = false,
                        length = 20
        )
        private MemberStatus status;

    // -> Indicates whether the member is currently active.
    @Builder.Default
    @Column(
            name = "active",
            nullable = false
    )
    private boolean active = true;

        // -> Indicates whether the conversation is muted for this member.
        @Builder.Default
        @Column(
                        name = "muted",
                        nullable = false
        )
        private boolean muted = false;

        // -> Indicates whether the conversation is pinned for this member.
        @Builder.Default
        @Column(
                        name = "pinned",
                        nullable = false
        )
        private boolean pinned = false;

    // -> Time when the user joined the conversation.
    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    // -> Time when the user left the conversation.
    @Column(name = "left_at")
    private LocalDateTime leftAt;

}