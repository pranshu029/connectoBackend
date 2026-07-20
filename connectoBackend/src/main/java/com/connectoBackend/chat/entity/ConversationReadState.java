package com.connectoBackend.chat.entity;

import com.connectoBackend.common.entity.BaseEntity;
import com.connectoBackend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Stores the read state of a user for a conversation.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "conversation_read_states",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_conversation_read_state",
                        columnNames = {
                                "conversation_id",
                                "user_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_read_state_conversation",
                        columnList = "conversation_id"
                ),
                @Index(
                        name = "idx_read_state_user",
                        columnList = "user_id"
                )
        }
)
public class ConversationReadState extends BaseEntity {

    // -> Conversation whose read state is tracked.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    // -> User whose read state is tracked.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // -> Last message read by the user.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_read_message_id")
    private Message lastReadMessage;

}