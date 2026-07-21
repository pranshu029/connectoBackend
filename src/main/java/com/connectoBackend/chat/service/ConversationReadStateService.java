package com.connectoBackend.chat.service;

import java.util.UUID;

public interface ConversationReadStateService {

	void markAsRead(UUID conversationId, UUID userId, UUID lastReadMessageId);
}