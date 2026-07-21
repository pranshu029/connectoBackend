package com.connectoBackend.chat.controller;

import com.connectoBackend.chat.dto.request.EditMessageRequest;
import com.connectoBackend.chat.dto.request.SendMessageRequest;
import com.connectoBackend.chat.dto.response.MessageResponse;
import com.connectoBackend.chat.service.MessageService;
import com.connectoBackend.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chats/conversations/{conversationId}/messages")
@RequiredArgsConstructor
public class MessageController {

	private final MessageService messageService;

	@PostMapping("/users/{senderId}")
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<MessageResponse> sendMessage(@PathVariable UUID conversationId, @PathVariable UUID senderId, @Valid @RequestBody SendMessageRequest request) {
		return ApiResponse.<MessageResponse>builder().success(true).message("Message sent successfully.").data(messageService.sendMessage(conversationId, senderId, request)).build();
	}

	@GetMapping
	public ApiResponse<Page<MessageResponse>> getMessages(@PathVariable UUID conversationId, Pageable pageable) {
		return ApiResponse.<Page<MessageResponse>>builder().success(true).message("Messages fetched successfully.").data(messageService.getMessages(conversationId, pageable)).build();
	}

	@PutMapping("/{messageId}")
	public ApiResponse<MessageResponse> editMessage(@PathVariable UUID messageId, @Valid @RequestBody EditMessageRequest request) {
		return ApiResponse.<MessageResponse>builder().success(true).message("Message updated successfully.").data(messageService.editMessage(messageId, request)).build();
	}

	@DeleteMapping("/{messageId}")
	public ApiResponse<Void> deleteMessage(@PathVariable UUID messageId) {
		messageService.deleteMessage(messageId);
		return ApiResponse.<Void>builder().success(true).message("Message deleted successfully.").build();
	}
}