package com.connectoBackend.webSocket.dto;

import java.util.UUID;

public record ReadReceiptPayload(

		UUID conversationId,
		UUID userId,
		UUID messageId

) {
}