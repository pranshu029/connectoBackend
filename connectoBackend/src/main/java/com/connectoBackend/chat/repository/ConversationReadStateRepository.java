package com.connectoBackend.chat.repository;

import com.connectoBackend.chat.entity.Conversation;
import com.connectoBackend.chat.entity.ConversationReadState;
import com.connectoBackend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for ConversationReadState entity.
 */
@Repository
public interface ConversationReadStateRepository extends JpaRepository<ConversationReadState, Long> {

    Optional<ConversationReadState> findByConversationAndUser(
            Conversation conversation,
            User user
    );

}