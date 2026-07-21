package com.connectoBackend.chat.service;

import com.connectoBackend.chat.dto.request.EditMessageRequest;
import com.connectoBackend.chat.dto.request.SendMessageRequest;
import com.connectoBackend.chat.dto.response.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MessageService {

	MessageResponse sendMessage(UUID conversationId, UUID senderId, SendMessageRequest request);

	MessageResponse editMessage(UUID messageId, EditMessageRequest request);

	void deleteMessage(UUID messageId);

	Page<MessageResponse> getMessages(UUID conversationId, Pageable pageable);
}