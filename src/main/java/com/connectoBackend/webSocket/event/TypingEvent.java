package com.connectoBackend.webSocket.event;

import java.util.UUID;

public record TypingEvent(

		UUID conversationId,
		UUID userId,
		boolean typing

) {
}