package com.connectoBackend.chat.controller;

import com.connectoBackend.chat.dto.request.InviteMemberRequest;
import com.connectoBackend.chat.dto.response.GroupInvitationResponse;
import com.connectoBackend.chat.enums.InvitationStatus;
import com.connectoBackend.chat.service.GroupInvitationService;
import com.connectoBackend.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chats/conversations/{conversationId}/invitations")
@RequiredArgsConstructor
public class GroupInvitationController {

	private final GroupInvitationService groupInvitationService;

	@PostMapping("/users/{invitedByUserId}")
	public ApiResponse<GroupInvitationResponse> inviteMember(@PathVariable UUID conversationId, @PathVariable UUID invitedByUserId, @Valid @RequestBody InviteMemberRequest request) {
		return ApiResponse.<GroupInvitationResponse>builder().success(true).message("Invitation sent successfully.").data(groupInvitationService.inviteMember(conversationId, invitedByUserId, request)).build();
	}

	@GetMapping("/users/{invitedUserId}")
	public ApiResponse<List<GroupInvitationResponse>> getInvitations(@PathVariable UUID invitedUserId) {
		return ApiResponse.<List<GroupInvitationResponse>>builder().success(true).message("Invitations fetched successfully.").data(groupInvitationService.getInvitationsForUser(invitedUserId)).build();
	}

	@PutMapping("/{invitationId}")
	public ApiResponse<Void> respondToInvitation(@PathVariable UUID invitationId, @RequestParam InvitationStatus status) {
		groupInvitationService.respondToInvitation(invitationId, status);
		return ApiResponse.<Void>builder().success(true).message("Invitation updated successfully.").build();
	}

	@DeleteMapping("/{invitationId}")
	public ApiResponse<Void> cancelInvitation(@PathVariable UUID invitationId) {
		groupInvitationService.cancelInvitation(invitationId);
		return ApiResponse.<Void>builder().success(true).message("Invitation cancelled successfully.").build();
	}
}