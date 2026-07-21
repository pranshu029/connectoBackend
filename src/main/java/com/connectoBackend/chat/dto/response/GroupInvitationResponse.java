package com.connectoBackend.chat.dto.response;

import com.connectoBackend.chat.enums.InvitationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for a group invitation.
 */
public record GroupInvitationResponse(

        UUID id,

        UUID invitedByUserId,

        String invitedByUsername,

        UUID invitedUserId,

        String invitedUsername,

        InvitationStatus status,

        LocalDateTime expiresAt

) {
}