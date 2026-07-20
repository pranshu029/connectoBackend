package com.connectoBackend.chat.dto.request;

import com.connectoBackend.chat.enums.ConversationType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Request DTO for creating a conversation.
 */
public record CreateConversationRequest(

        @NotNull(message = "Conversation type is required.")
        ConversationType conversationType,

        @Size(max = 100, message = "Conversation name cannot exceed 100 characters.")
        String name,

        @Size(max = 500, message = "Description cannot exceed 500 characters.")
        String description,

        @NotEmpty(message = "At least one participant is required.")
        List<Long> participantIds

) {
}