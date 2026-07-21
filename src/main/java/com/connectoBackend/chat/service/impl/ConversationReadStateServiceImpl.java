package com.connectoBackend.chat.service.impl;

import com.connectoBackend.chat.entity.Conversation;
import com.connectoBackend.chat.entity.ConversationReadState;
import com.connectoBackend.chat.entity.Message;
import com.connectoBackend.chat.repository.ConversationReadStateRepository;
import com.connectoBackend.chat.repository.ConversationRepository;
import com.connectoBackend.chat.repository.MessageRepository;
import com.connectoBackend.chat.service.ConversationReadStateService;
import com.connectoBackend.common.exception.ResourceNotFoundException;
import com.connectoBackend.user.entity.User;
import com.connectoBackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ConversationReadStateServiceImpl implements ConversationReadStateService {

	private final ConversationReadStateRepository conversationReadStateRepository;
	private final ConversationRepository conversationRepository;
	private final UserRepository userRepository;
	private final MessageRepository messageRepository;

	@Override
	public void markAsRead(UUID conversationId, UUID userId, UUID lastReadMessageId) {
		Conversation conversation = getConversation(conversationId);
		User user = getUser(userId);
		Message lastReadMessage = getMessage(lastReadMessageId);

		ConversationReadState state = conversationReadStateRepository.findByConversationAndUser(conversation, user)
				.orElseGet(ConversationReadState::new);
		state.setConversation(conversation);
		state.setUser(user);
		state.setLastReadMessage(lastReadMessage);
		conversationReadStateRepository.save(state);
	}

	private Conversation getConversation(UUID conversationId) {
		return conversationRepository.findById(conversationId)
				.orElseThrow(() -> new ResourceNotFoundException("Conversation not found."));
	}

	private User getUser(UUID userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found."));
	}

	private Message getMessage(UUID messageId) {
		return messageRepository.findById(messageId)
				.orElseThrow(() -> new ResourceNotFoundException("Message not found."));
	}
}