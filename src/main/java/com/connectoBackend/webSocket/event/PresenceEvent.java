package com.connectoBackend.webSocket.event;

import java.util.UUID;

public record PresenceEvent(

		UUID userId,
		boolean online

) {
}