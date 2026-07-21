package com.connectoBackend.chat.service.impl;

import com.connectoBackend.chat.dto.request.AddReactionRequest;
import com.connectoBackend.chat.dto.response.MessageReactionResponse;
import com.connectoBackend.chat.entity.Message;
import com.connectoBackend.chat.entity.MessageReaction;
import com.connectoBackend.chat.enums.ReactionType;
import com.connectoBackend.chat.repository.MessageReactionRepository;
import com.connectoBackend.chat.repository.MessageRepository;
import com.connectoBackend.chat.service.ReactionService;
import com.connectoBackend.common.exception.ResourceNotFoundException;
import com.connectoBackend.user.entity.User;
import com.connectoBackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReactionServiceImpl implements ReactionService {

	private final MessageReactionRepository messageReactionRepository;
	private final MessageRepository messageRepository;
	private final UserRepository userRepository;

	@Override
	public MessageReactionResponse addReaction(UUID messageId, UUID userId, AddReactionRequest request) {
		Message message = getMessage(messageId);
		User user = getUser(userId);
		MessageReaction reaction = messageReactionRepository.findByMessageAndUser(message, user)
				.orElseGet(MessageReaction::new);
		reaction.setMessage(message);
		reaction.setUser(user);
		reaction.setReaction(ReactionType.valueOf(request.reactionType().toUpperCase()));
		reaction = messageReactionRepository.save(reaction);
		return toResponse(reaction);
	}

	@Override
	public void removeReaction(UUID messageId, UUID userId) {
		Message message = getMessage(messageId);
		User user = getUser(userId);
		messageReactionRepository.findByMessageAndUser(message, user)
				.ifPresent(messageReactionRepository::delete);
	}

	@Override
	@Transactional(readOnly = true)
	public List<MessageReactionResponse> getReactions(UUID messageId) {
		Message message = getMessage(messageId);
		return messageReactionRepository.findAllByMessage(message)
				.stream()
				.map(this::toResponse)
				.toList();
	}

	private Message getMessage(UUID messageId) {
		return messageRepository.findById(messageId)
				.orElseThrow(() -> new ResourceNotFoundException("Message not found."));
	}

	private User getUser(UUID userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found."));
	}

	private MessageReactionResponse toResponse(MessageReaction reaction) {
		return new MessageReactionResponse(
				reaction.getId(),
				reaction.getUser().getId(),
				reaction.getUser().getUsername(),
				reaction.getReaction()
		);
	}
}