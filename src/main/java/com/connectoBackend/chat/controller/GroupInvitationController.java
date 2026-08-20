package com.connectoBackend.chat.controller;

import com.connectoBackend.chat.dto.request.InviteMemberRequest;
import com.connectoBackend.chat.dto.response.GroupInvitationResponse;
import com.connectoBackend.chat.enums.InvitationStatus;
import com.connectoBackend.chat.service.GroupInvitationService;
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
@RequestMapping("/api/v1/chats/conversations/{conversationId}/invitations")
@RequiredArgsConstructor
public class GroupInvitationController {

	private final GroupInvitationService groupInvitationService;
	private final UserRepository userRepository;

	@PostMapping("/users/{invitedByUserId}")
	public ApiResponse<GroupInvitationResponse> inviteMember(@PathVariable UUID conversationId, @PathVariable UUID invitedByUserId, @Valid @RequestBody InviteMemberRequest request, Authentication authentication) {
		return ApiResponse.<GroupInvitationResponse>builder().success(true).message("Invitation sent successfully.").data(groupInvitationService.inviteMember(conversationId, currentUserId(invitedByUserId, authentication), request)).build();
	}

	@GetMapping("/users/{invitedUserId}")
	public ApiResponse<List<GroupInvitationResponse>> getInvitations(@PathVariable UUID invitedUserId, Authentication authentication) {
		return ApiResponse.<List<GroupInvitationResponse>>builder().success(true).message("Invitations fetched successfully.").data(groupInvitationService.getInvitationsForUser(currentUserId(invitedUserId, authentication))).build();
	}

	@PutMapping("/{invitationId}")
	public ApiResponse<Void> respondToInvitation(@PathVariable UUID invitationId, @RequestParam InvitationStatus status, Authentication authentication) {
		groupInvitationService.respondToInvitation(currentUserId(authentication), invitationId, status);
		return ApiResponse.<Void>builder().success(true).message("Invitation updated successfully.").build();
	}

	@DeleteMapping("/{invitationId}")
	public ApiResponse<Void> cancelInvitation(@PathVariable UUID invitationId, Authentication authentication) {
		groupInvitationService.cancelInvitation(currentUserId(authentication), invitationId);
		return ApiResponse.<Void>builder().success(true).message("Invitation cancelled successfully.").build();
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