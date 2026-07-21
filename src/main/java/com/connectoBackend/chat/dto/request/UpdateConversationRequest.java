package com.connectoBackend.chat.dto.request;

import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating a conversation.
 */
public record UpdateConversationRequest(

		@Size(max = 100, message = "Conversation name cannot exceed 100 characters.")
		String name,

		@Size(max = 500, message = "Description cannot exceed 500 characters.")
		String description,

		@Size(max = 500, message = "Image URL cannot exceed 500 characters.")
		String imageUrl,

		Boolean active

) {
}
