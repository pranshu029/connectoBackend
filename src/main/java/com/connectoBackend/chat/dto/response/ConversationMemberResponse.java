package com.connectoBackend.chat.dto.response;

import com.connectoBackend.chat.enums.ConversationRole;
import com.connectoBackend.chat.enums.MemberStatus;

import java.util.UUID;

/**
 * Response DTO for a conversation member.
 */
public record ConversationMemberResponse(

        UUID userId,

        String username,

        String fullName,

        String profileImage,

        ConversationRole role,

        MemberStatus status,

        boolean muted,

        boolean pinned

) {
}