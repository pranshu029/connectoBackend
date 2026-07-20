package com.connectoBackend.chat.entity;

import com.connectoBackend.chat.enums.MessageStatus;
import com.connectoBackend.chat.enums.MessageType;
import com.connectoBackend.common.entity.BaseEntity;
import com.connectoBackend.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a message sent in a conversation.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "messages",
        indexes = {
                @Index(
                        name = "idx_message_conversation_sequence",
                        columnList = "conversation_id, sequence_number"
                ),
                @Index(
                        name = "idx_message_sender",
                        columnList = "sender_id"
                )
        }
)
public class Message extends BaseEntity {

    // -> Conversation containing this message.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "conversation_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_message_conversation"
            )
    )
    private Conversation conversation;

    // -> User who sent this message.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "sender_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_message_sender"
            )
    )
    private User sender;

    // -> Message type.
    @Enumerated(EnumType.STRING)
    @Column(
            name = "type",
            nullable = false,
            length = 20
    )
    private MessageType type;

    // -> Delivery status.
    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private MessageStatus status;

    // -> Message content.
    @Column(
            name = "content",
            columnDefinition = "TEXT"
    )
    private String content;

    // -> Parent message if this is a reply.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "reply_to_message_id",
            foreignKey = @ForeignKey(
                    name = "fk_message_reply"
            )
    )
    private Message replyToMessage;

    // -> Indicates whether the message was edited.
    @Builder.Default
    @Column(
            name = "edited",
            nullable = false
    )
    private boolean edited = false;

    // -> Time when the message was edited.
    @Column(name = "edited_at")
    private LocalDateTime editedAt;

    // -> Sequential number inside the conversation.
    @Column(
            name = "sequence_number",
            nullable = false
    )
    private Long sequenceNumber;

    // -> Message attachments.
    @OneToMany(
            mappedBy = "message",
            fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            }
    )
    @Builder.Default
    private Set<Attachment> attachments = new HashSet<>();

    // -> Message reactions.
    @OneToMany(
            mappedBy = "message",
            fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            }
    )
    @Builder.Default
    private Set<MessageReaction> reactions = new HashSet<>();

}