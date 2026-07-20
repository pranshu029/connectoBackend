package com.connectoBackend.chat.entity;

import com.connectoBackend.chat.enums.ReactionType;
import com.connectoBackend.common.entity.BaseEntity;
import com.connectoBackend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Represents a user's reaction to a message.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "message_reactions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_message_reaction",
                        columnNames = {
                                "message_id",
                                "user_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_message_reaction_message",
                        columnList = "message_id"
                ),
                @Index(
                        name = "idx_message_reaction_user",
                        columnList = "user_id"
                )
        }
)
public class MessageReaction extends BaseEntity {

    // -> Message on which the reaction was added.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    // -> User who reacted.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // -> Reaction type.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReactionType reaction;

}