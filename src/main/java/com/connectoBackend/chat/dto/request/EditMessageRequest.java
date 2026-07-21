package com.connectoBackend.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for editing a message.
 */
public record EditMessageRequest(

        @NotBlank(message = "Message content is required.")
        @Size(max = 5000, message = "Message content cannot exceed 5000 characters.")
        String content

) {
}