package com.connectoBackend.webSocket.dto;

import java.util.UUID;

public record TypingPayload(

		UUID conversationId,
		UUID userId,
		boolean typing

) {
}