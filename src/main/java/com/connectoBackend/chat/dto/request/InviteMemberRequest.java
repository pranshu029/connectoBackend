package com.connectoBackend.chat.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Request DTO for inviting a member.
 */
public record InviteMemberRequest(

        @NotNull(message = "User ID is required.")
        UUID userId

) {
}