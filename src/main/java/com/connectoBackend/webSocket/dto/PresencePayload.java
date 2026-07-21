package com.connectoBackend.webSocket.dto;

import java.util.UUID;

public record PresencePayload(

		UUID userId,
		boolean online

) {
}