package com.connectoBackend.chat.controller;

import com.connectoBackend.chat.dto.request.EditMessageRequest;
import com.connectoBackend.chat.dto.request.SendMessageRequest;
import com.connectoBackend.chat.dto.response.MessageResponse;
import com.connectoBackend.chat.service.MessageService;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chats/conversations/{conversationId}/messages")
@RequiredArgsConstructor
public class MessageController {

	private final MessageService messageService;
	private final UserRepository userRepository;

	@PostMapping("/users/{senderId}")
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<MessageResponse> sendMessage(@PathVariable UUID conversationId, @PathVariable UUID senderId, @Valid @RequestBody SendMessageRequest request, Authentication authentication) {
		return ApiResponse.<MessageResponse>builder().success(true).message("Message sent successfully.").data(messageService.sendMessage(conversationId, currentUserId(senderId, authentication), request)).build();
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

	@GetMapping
	public ApiResponse<Page<MessageResponse>> getMessages(@PathVariable UUID conversationId, Pageable pageable, Authentication authentication) {
		return ApiResponse.<Page<MessageResponse>>builder().success(true).message("Messages fetched successfully.").data(messageService.getMessages(conversationId, authenticatedUserId(authentication), pageable)).build();
	}

	@PutMapping("/{messageId}")
	public ApiResponse<MessageResponse> editMessage(@PathVariable UUID messageId, @Valid @RequestBody EditMessageRequest request, Authentication authentication) {
		return ApiResponse.<MessageResponse>builder().success(true).message("Message updated successfully.").data(messageService.editMessage(messageId, authenticatedUserId(authentication), request)).build();
	}

	@DeleteMapping("/{messageId}")
	public ApiResponse<Void> deleteMessage(@PathVariable UUID messageId, Authentication authentication) {
		messageService.deleteMessage(messageId, authenticatedUserId(authentication));
		return ApiResponse.<Void>builder().success(true).message("Message deleted successfully.").build();
	}

	private UUID authenticatedUserId(Authentication authentication) {
		return userRepository.findByEmail(authentication.getName())
				.orElseThrow(() -> new IllegalStateException("Authenticated user not found.")).getId();
	}
}