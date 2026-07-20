package com.connectoBackend.chat.dto.response;

import com.connectoBackend.chat.enums.ReactionType;

/**
 * Response DTO for a message reaction.
 */
public record MessageReactionResponse(

        Long id,

        Long userId,

        String username,

        ReactionType reaction

) {
}