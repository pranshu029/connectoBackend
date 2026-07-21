package com.connectoBackend.webSocket.service.impl;

import com.connectoBackend.webSocket.dto.ChatMessagePayload;
import com.connectoBackend.webSocket.dto.PresencePayload;
import com.connectoBackend.webSocket.dto.ReadReceiptPayload;
import com.connectoBackend.webSocket.dto.TypingPayload;
import com.connectoBackend.webSocket.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketServiceImpl implements WebSocketService {

	private final SimpMessagingTemplate messagingTemplate;

	@Override
	public void broadcastMessage(ChatMessagePayload payload) {
		messagingTemplate.convertAndSend("/topic/conversations/" + payload.conversationId(), payload);
	}

	@Override
	public void broadcastTyping(TypingPayload payload) {
		messagingTemplate.convertAndSend("/topic/conversations/" + payload.conversationId() + "/typing", payload);
	}

	@Override
	public void broadcastPresence(PresencePayload payload) {
		messagingTemplate.convertAndSend("/topic/presence", payload);
	}

	@Override
	public void broadcastReadReceipt(ReadReceiptPayload payload) {
		messagingTemplate.convertAndSend("/topic/conversations/" + payload.conversationId() + "/read", payload);
	}
}