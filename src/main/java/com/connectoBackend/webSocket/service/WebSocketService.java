package com.connectoBackend.webSocket.service;

import com.connectoBackend.webSocket.dto.ChatMessagePayload;
import com.connectoBackend.webSocket.dto.PresencePayload;
import com.connectoBackend.webSocket.dto.ReadReceiptPayload;
import com.connectoBackend.webSocket.dto.TypingPayload;

public interface WebSocketService {

	void broadcastMessage(ChatMessagePayload payload);

	void broadcastTyping(TypingPayload payload);

	void broadcastPresence(PresencePayload payload);

	void broadcastReadReceipt(ReadReceiptPayload payload);
}