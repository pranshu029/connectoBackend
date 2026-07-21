package com.connectoBackend.webSocket.event;

import java.util.UUID;

public record MessageEvent(

		UUID conversationId,
		UUID messageId,
		UUID senderId,
		String content

) {
}