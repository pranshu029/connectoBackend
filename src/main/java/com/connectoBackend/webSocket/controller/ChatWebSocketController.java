package com.connectoBackend.webSocket.controller;

import com.connectoBackend.webSocket.dto.ChatMessagePayload;
import com.connectoBackend.webSocket.dto.PresencePayload;
import com.connectoBackend.webSocket.dto.ReadReceiptPayload;
import com.connectoBackend.webSocket.dto.TypingPayload;
import com.connectoBackend.webSocket.service.PresenceService;
import com.connectoBackend.webSocket.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

	private final WebSocketService webSocketService;
	private final PresenceService presenceService;

	@MessageMapping("/chat.message")
	public void handleMessage(ChatMessagePayload payload) {
		webSocketService.broadcastMessage(payload);
	}

	@MessageMapping("/chat.typing")
	public void handleTyping(TypingPayload payload) {
		webSocketService.broadcastTyping(payload);
	}

	@MessageMapping("/chat.presence")
	public void handlePresence(PresencePayload payload) {
		if (payload.online()) {
			presenceService.markOnline(payload.userId());
		} else {
			presenceService.markOffline(payload.userId());
		}
		webSocketService.broadcastPresence(payload);
	}

	@MessageMapping("/chat.read")
	public void handleReadReceipt(ReadReceiptPayload payload) {
		webSocketService.broadcastReadReceipt(payload);
	}
}