package com.connectoBackend.chat.repository;

import com.connectoBackend.chat.entity.Conversation;
import com.connectoBackend.chat.enums.ConversationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository for {@link Conversation}.
 */
@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    /**
     * Retrieves conversations by type.
     */
    Page<Conversation> findByType(
            ConversationType type,
            Pageable pageable
    );

    /**
     * Retrieves all active conversations.
     */
    Page<Conversation> findByActiveTrue(
            Pageable pageable
    );
}