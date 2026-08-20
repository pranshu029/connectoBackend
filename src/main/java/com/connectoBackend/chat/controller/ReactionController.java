package com.connectoBackend.chat.controller;

import com.connectoBackend.chat.dto.request.AddReactionRequest;
import com.connectoBackend.chat.dto.response.MessageReactionResponse;
import com.connectoBackend.chat.service.ReactionService;
import com.connectoBackend.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.connectoBackend.user.repository.UserRepository;
import com.connectoBackend.common.exception.ForbiddenException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chats/messages/{messageId}/reactions")
@RequiredArgsConstructor
public class ReactionController {

	private final ReactionService reactionService;
	private final UserRepository userRepository;

	@PostMapping("/users/{userId}")
	public ApiResponse<MessageReactionResponse> addReaction(@PathVariable UUID messageId, @PathVariable UUID userId, @Valid @RequestBody AddReactionRequest request, Authentication authentication) {
		return ApiResponse.<MessageReactionResponse>builder().success(true).message("Reaction added successfully.").data(reactionService.addReaction(messageId, currentUserId(userId, authentication), request)).build();
	}

	@DeleteMapping("/users/{userId}")
	public ApiResponse<Void> removeReaction(@PathVariable UUID messageId, @PathVariable UUID userId, Authentication authentication) {
		reactionService.removeReaction(messageId, currentUserId(userId, authentication));
		return ApiResponse.<Void>builder().success(true).message("Reaction removed successfully.").build();
	}

	@GetMapping
	public ApiResponse<List<MessageReactionResponse>> getReactions(@PathVariable UUID messageId, Authentication authentication) {
		return ApiResponse.<List<MessageReactionResponse>>builder().success(true).message("Reactions fetched successfully.").data(reactionService.getReactions(messageId, currentUserId(authentication))).build();
	}

	private UUID currentUserId(Authentication authentication) {
		return userRepository.findByEmail(authentication.getName())
				.orElseThrow(() -> new IllegalStateException("Authenticated user not found.")).getId();
	}

	private UUID currentUserId(UUID requestedUserId, Authentication authentication) {
		UUID authenticatedUserId = currentUserId(authentication);
		if (!authenticatedUserId.equals(requestedUserId)) {
			throw new ForbiddenException("You can act only as the authenticated user.");
		}
		return authenticatedUserId;
	}
}