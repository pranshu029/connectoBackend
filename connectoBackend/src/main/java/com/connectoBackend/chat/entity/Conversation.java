package com.connectoBackend.chat.entity;

import com.connectoBackend.chat.enums.ConversationType;
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

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a chat conversation.
 *
 * A conversation can be:
 * -> Direct conversation
 * -> Group conversation
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "conversations",
        indexes = {
                @Index(
                        name = "idx_conversation_type",
                        columnList = "type"
                )
        }
)
public class Conversation extends BaseEntity {

    // -> Conversation type.
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private ConversationType type;

    // -> Group name (null for direct conversations).
    @Column(name = "name", length = 100)
    private String name;

    // -> Group description.
    @Column(name = "description", length = 500)
    private String description;

    // -> Group profile image.
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    // -> Indicates whether the conversation is active.
    @Builder.Default
    @Column(name = "active", nullable = false)
    private boolean active = true;

    // -> User who created the conversation.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "created_by_user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_conversation_created_by_user")
    )
    private User createdBy;

    // -> Members participating in this conversation.
    @OneToMany(
            mappedBy = "conversation",
            fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            }
    )
    @Builder.Default
    private Set<ConversationMember> members = new HashSet<>();

    // -> Messages belonging to this conversation.
    @OneToMany(
            mappedBy = "conversation",
            fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            }
    )
    @Builder.Default
    private Set<Message> messages = new HashSet<>();

}