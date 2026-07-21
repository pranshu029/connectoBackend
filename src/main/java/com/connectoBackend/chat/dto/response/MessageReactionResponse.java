package com.connectoBackend.chat.dto.response;

import com.connectoBackend.chat.enums.ReactionType;

import java.util.UUID;

/**
 * Response DTO for a message reaction.
 */
public record MessageReactionResponse(

        UUID id,

        UUID userId,

        String username,

        ReactionType reaction

) {
}