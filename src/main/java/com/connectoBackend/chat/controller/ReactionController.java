package com.connectoBackend.chat.controller;

import com.connectoBackend.chat.dto.request.AddReactionRequest;
import com.connectoBackend.chat.dto.response.MessageReactionResponse;
import com.connectoBackend.chat.service.ReactionService;
import com.connectoBackend.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chats/messages/{messageId}/reactions")
@RequiredArgsConstructor
public class ReactionController {

	private final ReactionService reactionService;

	@PostMapping("/users/{userId}")
	public ApiResponse<MessageReactionResponse> addReaction(@PathVariable UUID messageId, @PathVariable UUID userId, @Valid @RequestBody AddReactionRequest request) {
		return ApiResponse.<MessageReactionResponse>builder().success(true).message("Reaction added successfully.").data(reactionService.addReaction(messageId, userId, request)).build();
	}

	@DeleteMapping("/users/{userId}")
	public ApiResponse<Void> removeReaction(@PathVariable UUID messageId, @PathVariable UUID userId) {
		reactionService.removeReaction(messageId, userId);
		return ApiResponse.<Void>builder().success(true).message("Reaction removed successfully.").build();
	}

	@GetMapping
	public ApiResponse<List<MessageReactionResponse>> getReactions(@PathVariable UUID messageId) {
		return ApiResponse.<List<MessageReactionResponse>>builder().success(true).message("Reactions fetched successfully.").data(reactionService.getReactions(messageId)).build();
	}
}