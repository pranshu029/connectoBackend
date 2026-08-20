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
import com.connectoBackend.common.exception.ForbiddenException;
import com.connectoBackend.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chats/conversations")
@RequiredArgsConstructor
public class ConversationController {

	private final ConversationService conversationService;
	private final UserRepository userRepository;

	@PostMapping("/users/{creatorUserId}")
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<ConversationResponse> createConversation(@PathVariable UUID creatorUserId, @Valid @RequestBody CreateConversationRequest request, Authentication authentication) {
		return ApiResponse.<ConversationResponse>builder().success(true).message("Conversation created successfully.").data(conversationService.createConversation(currentUserId(creatorUserId, authentication), request)).build();
	}

	@GetMapping("/{conversationId}")
	public ApiResponse<ConversationResponse> getConversation(@PathVariable UUID conversationId, Authentication authentication) {
		return ApiResponse.<ConversationResponse>builder().success(true).message("Conversation fetched successfully.").data(conversationService.getConversation(conversationId, authenticatedUserId(authentication))).build();
	}

	private UUID currentUserId(UUID requestedUserId, Authentication authentication) {
		UUID authenticatedUserId = userRepository.findByEmail(authentication.getName())
				.orElseThrow(() -> new IllegalStateException("Authenticated user not found."))
				.getId();
		if (!authenticatedUserId.equals(requestedUserId)) {
			throw new ForbiddenException("You can act only as the authenticated user.");
		}
		return authenticatedUserId;
	}

	private UUID authenticatedUserId(Authentication authentication) {
		return userRepository.findByEmail(authentication.getName())
				.orElseThrow(() -> new IllegalStateException("Authenticated user not found.")).getId();
	}

	@GetMapping("/users/{userId}")
	public ApiResponse<Page<ConversationSummaryResponse>> getConversations(@PathVariable UUID userId, Pageable pageable, Authentication authentication) {
		return ApiResponse.<Page<ConversationSummaryResponse>>builder().success(true).message("Conversations fetched successfully.").data(conversationService.getConversationsForUser(currentUserId(userId, authentication), pageable)).build();
	}

	@PutMapping("/{conversationId}")
	public ApiResponse<ConversationResponse> updateConversation(@PathVariable UUID conversationId, @Valid @RequestBody UpdateConversationRequest request, Authentication authentication) {
		return ApiResponse.<ConversationResponse>builder().success(true).message("Conversation updated successfully.").data(conversationService.updateConversation(conversationId, authenticatedUserId(authentication), request)).build();
	}

	@DeleteMapping("/{conversationId}")
	public ApiResponse<Void> deleteConversation(@PathVariable UUID conversationId, Authentication authentication) {
		conversationService.deleteConversation(conversationId, authenticatedUserId(authentication));
		return ApiResponse.<Void>builder().success(true).message("Conversation deleted successfully.").build();
	}

	@GetMapping("/{conversationId}/members")
	public ApiResponse<List<ConversationMemberResponse>> getMembers(@PathVariable UUID conversationId, Authentication authentication) {
		return ApiResponse.<List<ConversationMemberResponse>>builder().success(true).message("Conversation members fetched successfully.").data(conversationService.getMembers(conversationId, authenticatedUserId(authentication))).build();
	}

	@PostMapping("/{conversationId}/members/{userId}")
	public ApiResponse<ConversationMemberResponse> addMember(@PathVariable UUID conversationId, @PathVariable UUID userId, @RequestParam ConversationMemberRole role, Authentication authentication) {
		return ApiResponse.<ConversationMemberResponse>builder().success(true).message("Member added successfully.").data(conversationService.addMember(conversationId, authenticatedUserId(authentication), userId, role)).build();
	}

	@PutMapping("/{conversationId}/members/{userId}")
	public ApiResponse<ConversationMemberResponse> updateMemberRole(@PathVariable UUID conversationId, @PathVariable UUID userId, @Valid @RequestBody UpdateMemberRoleRequest request, Authentication authentication) {
		return ApiResponse.<ConversationMemberResponse>builder().success(true).message("Member role updated successfully.").data(conversationService.updateMemberRole(conversationId, authenticatedUserId(authentication), userId, request)).build();
	}

	@DeleteMapping("/{conversationId}/members/{userId}")
	public ApiResponse<Void> removeMember(@PathVariable UUID conversationId, @PathVariable UUID userId, Authentication authentication) {
		conversationService.removeMember(conversationId, authenticatedUserId(authentication), userId);
		return ApiResponse.<Void>builder().success(true).message("Member removed successfully.").build();
	}
}