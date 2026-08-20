package com.connectoBackend.chat.repository;

import com.connectoBackend.chat.entity.Conversation;
import com.connectoBackend.chat.enums.ConversationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link Conversation}.
 */
@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    @Query("""
            select c from Conversation c
            join ConversationMember firstMember on firstMember.conversation = c
            join ConversationMember secondMember on secondMember.conversation = c
            where c.type = :type and c.active = true
              and firstMember.user.id = :firstUserId and firstMember.active = true
              and secondMember.user.id = :secondUserId and secondMember.active = true
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
        List<Conversation> findActiveDirectConversations(
            ConversationType type,
            UUID firstUserId,
            UUID secondUserId
    );

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