package com.connectoBackend.chat.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for inviting a member.
 */
public record InviteMemberRequest(

        @NotNull(message = "User ID is required.")
        Long userId

) {
}