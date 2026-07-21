package com.connectoBackend.chat.repository;

import com.connectoBackend.chat.entity.Conversation;
import com.connectoBackend.chat.entity.ConversationMember;
import com.connectoBackend.chat.enums.MemberStatus;
import com.connectoBackend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for ConversationMember entity.
 */
@Repository
public interface ConversationMemberRepository extends JpaRepository<ConversationMember, UUID> {

    List<ConversationMember> findAllByConversation(Conversation conversation);

    List<ConversationMember> findAllByUser(User user);

    List<ConversationMember> findAllByConversationAndStatus(
            Conversation conversation,
            MemberStatus status
    );

    Optional<ConversationMember> findByConversationAndUser(
            Conversation conversation,
            User user
    );

    boolean existsByConversationAndUser(
            Conversation conversation,
            User user
    );

}