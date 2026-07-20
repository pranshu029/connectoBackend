package com.connectoBackend.chat.repository;

import com.connectoBackend.chat.entity.Conversation;
import com.connectoBackend.chat.entity.Message;
import com.connectoBackend.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link Message}.
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Retrieves messages for a conversation.
     */
    Page<Message> findByConversationAndDeletedFalseOrderBySequenceNumberDesc(
            Conversation conversation,
            Pageable pageable
    );

    /**
     * Retrieves messages sent by a user.
     */
    Page<Message> findBySenderAndDeletedFalse(
            User sender,
            Pageable pageable
    );
}