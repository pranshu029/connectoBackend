package com.connectoBackend.chat.dto.request;

import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request DTO for sending a message.
 */
public record SendMessageRequest(

        @Size(max = 5000, message = "Message content cannot exceed 5000 characters.")
        String content,

        UUID replyToMessageId

) {
}