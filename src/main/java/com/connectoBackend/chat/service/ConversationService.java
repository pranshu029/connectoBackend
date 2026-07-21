package com.connectoBackend.chat.service;

import com.connectoBackend.chat.dto.request.CreateConversationRequest;
import com.connectoBackend.chat.dto.request.UpdateConversationRequest;
import com.connectoBackend.chat.dto.request.UpdateMemberRoleRequest;
import com.connectoBackend.chat.dto.response.ConversationMemberResponse;
import com.connectoBackend.chat.dto.response.ConversationResponse;
import com.connectoBackend.chat.dto.response.ConversationSummaryResponse;
import com.connectoBackend.chat.enums.ConversationMemberRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ConversationService {

	ConversationResponse createConversation(UUID creatorUserId, CreateConversationRequest request);

	ConversationResponse getConversation(UUID conversationId);

	Page<ConversationSummaryResponse> getConversationsForUser(UUID userId, Pageable pageable);

	ConversationResponse updateConversation(UUID conversationId, UpdateConversationRequest request);

	void deleteConversation(UUID conversationId);

	ConversationMemberResponse addMember(UUID conversationId, UUID userId, ConversationMemberRole role);

	ConversationMemberResponse updateMemberRole(UUID conversationId, UUID userId, UpdateMemberRoleRequest request);

	void removeMember(UUID conversationId, UUID userId);

	List<ConversationMemberResponse> getMembers(UUID conversationId);
}