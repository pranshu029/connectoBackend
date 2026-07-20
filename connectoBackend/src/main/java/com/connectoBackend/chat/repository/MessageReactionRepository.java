package com.connectoBackend.chat.repository;

import com.connectoBackend.chat.entity.Message;
import com.connectoBackend.chat.entity.MessageReaction;
import com.connectoBackend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for MessageReaction entity.
 */
@Repository
public interface MessageReactionRepository extends JpaRepository<MessageReaction, Long> {

    List<MessageReaction> findAllByMessage(Message message);

    Optional<MessageReaction> findByMessageAndUser(
            Message message,
            User user
    );

}