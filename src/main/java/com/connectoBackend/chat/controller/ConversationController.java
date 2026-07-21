package com.connectoBackend.chat.controller;

import com.connectoBackend.chat.dto.request.CreateConversationRequest;
import com.connectoBackend.chat.dto.request.UpdateConversationRequest;
import com.connectoBackend.chat.dto.request.UpdateMemberRoleRequest;
import com.connectoBackend.chat.dto.response.ConversationMemberResponse;
import com.connectoBackend.chat.dto.response.ConversationResponse;
import com.connectoBackend.chat.dto.response.ConversationSummaryResponse;
import com.connectoBackend.chat.enums.ConversationMemberRole;
import com.connectoBackend.chat.service.ConversationService;
import com.connectoBackend.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chats/conversations")
@RequiredArgsConstructor
public class ConversationController {

	private final ConversationService conversationService;

	@PostMapping("/users/{creatorUserId}")
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<ConversationResponse> createConversation(@PathVariable UUID creatorUserId, @Valid @RequestBody CreateConversationRequest request) {
		return ApiResponse.<ConversationResponse>builder().success(true).message("Conversation created successfully.").data(conversationService.createConversation(creatorUserId, request)).build();
	}

	@GetMapping("/{conversationId}")
	public ApiResponse<ConversationResponse> getConversation(@PathVariable UUID conversationId) {
		return ApiResponse.<ConversationResponse>builder().success(true).message("Conversation fetched successfully.").data(conversationService.getConversation(conversationId)).build();
	}

	@GetMapping("/users/{userId}")
	public ApiResponse<Page<ConversationSummaryResponse>> getConversations(@PathVariable UUID userId, Pageable pageable) {
		return ApiResponse.<Page<ConversationSummaryResponse>>builder().success(true).message("Conversations fetched successfully.").data(conversationService.getConversationsForUser(userId, pageable)).build();
	}

	@PutMapping("/{conversationId}")
	public ApiResponse<ConversationResponse> updateConversation(@PathVariable UUID conversationId, @Valid @RequestBody UpdateConversationRequest request) {
		return ApiResponse.<ConversationResponse>builder().success(true).message("Conversation updated successfully.").data(conversationService.updateConversation(conversationId, request)).build();
	}

	@DeleteMapping("/{conversationId}")
	public ApiResponse<Void> deleteConversation(@PathVariable UUID conversationId) {
		conversationService.deleteConversation(conversationId);
		return ApiResponse.<Void>builder().success(true).message("Conversation deleted successfully.").build();
	}

	@GetMapping("/{conversationId}/members")
	public ApiResponse<List<ConversationMemberResponse>> getMembers(@PathVariable UUID conversationId) {
		return ApiResponse.<List<ConversationMemberResponse>>builder().success(true).message("Conversation members fetched successfully.").data(conversationService.getMembers(conversationId)).build();
	}

	@PostMapping("/{conversationId}/members/{userId}")
	public ApiResponse<ConversationMemberResponse> addMember(@PathVariable UUID conversationId, @PathVariable UUID userId, @RequestParam ConversationMemberRole role) {
		return ApiResponse.<ConversationMemberResponse>builder().success(true).message("Member added successfully.").data(conversationService.addMember(conversationId, userId, role)).build();
	}

	@PutMapping("/{conversationId}/members/{userId}")
	public ApiResponse<ConversationMemberResponse> updateMemberRole(@PathVariable UUID conversationId, @PathVariable UUID userId, @Valid @RequestBody UpdateMemberRoleRequest request) {
		return ApiResponse.<ConversationMemberResponse>builder().success(true).message("Member role updated successfully.").data(conversationService.updateMemberRole(conversationId, userId, request)).build();
	}

	@DeleteMapping("/{conversationId}/members/{userId}")
	public ApiResponse<Void> removeMember(@PathVariable UUID conversationId, @PathVariable UUID userId) {
		conversationService.removeMember(conversationId, userId);
		return ApiResponse.<Void>builder().success(true).message("Member removed successfully.").build();
	}
}