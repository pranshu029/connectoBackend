package com.connectoBackend.chat.dto.response;

import com.connectoBackend.chat.enums.ConversationType;

import java.util.List;
import java.util.UUID;

/**
 * Response DTO for a conversation.
 */
public record ConversationResponse(

		UUID id,

		ConversationType type,

		String name,

		String description,

		String imageUrl,

		boolean active,

		UUID createdByUserId,

		List<ConversationMemberResponse> members

) {
}
