package com.connectoBackend.webSocket.dto;

import java.util.UUID;

public record ChatMessagePayload(

		UUID conversationId,
		UUID senderId,
		String content,
		UUID replyToMessageId

) {
}