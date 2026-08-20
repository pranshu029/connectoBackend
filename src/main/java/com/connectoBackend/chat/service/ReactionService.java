package com.connectoBackend.chat.service;

import com.connectoBackend.chat.dto.request.AddReactionRequest;
import com.connectoBackend.chat.dto.response.MessageReactionResponse;

import java.util.List;
import java.util.UUID;

public interface ReactionService {

	MessageReactionResponse addReaction(UUID messageId, UUID userId, AddReactionRequest request);

	void removeReaction(UUID messageId, UUID userId);

	List<MessageReactionResponse> getReactions(UUID messageId, UUID viewerId);
}