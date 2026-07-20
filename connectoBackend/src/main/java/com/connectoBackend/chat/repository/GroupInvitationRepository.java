package com.connectoBackend.chat.repository;

import com.connectoBackend.chat.entity.Conversation;
import com.connectoBackend.chat.entity.GroupInvitation;
import com.connectoBackend.chat.enums.InvitationStatus;
import com.connectoBackend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for GroupInvitation entity.
 */
@Repository
public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, Long> {

    List<GroupInvitation> findAllByInvitedUser(User invitedUser);

    List<GroupInvitation> findAllByConversation(
            Conversation conversation
    );

    List<GroupInvitation> findAllByInvitedUserAndStatus(
            User invitedUser,
            InvitationStatus status
    );

}