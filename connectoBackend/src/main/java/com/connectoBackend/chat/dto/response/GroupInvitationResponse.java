package com.connectoBackend.chat.dto.response;

import com.connectoBackend.chat.enums.InvitationStatus;

import java.time.LocalDateTime;

/**
 * Response DTO for a group invitation.
 */
public record GroupInvitationResponse(

        Long id,

        Long invitedByUserId,

        String invitedByUsername,

        Long invitedUserId,

        String invitedUsername,

        InvitationStatus status,

        LocalDateTime expiresAt

) {
}