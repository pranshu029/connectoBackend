package com.connectoBackend.chat.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for adding a reaction.
 */
public record AddReactionRequest(

        @NotBlank(message = "Reaction type is required.")
        String reactionType

) {
}