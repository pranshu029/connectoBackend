package com.connectoBackend.user.controller;

import com.connectoBackend.common.response.ApiResponse;
import com.connectoBackend.user.dto.response.UserSummaryResponse;
import com.connectoBackend.user.service.BlockUserService;
import com.connectoBackend.user.repository.UserRepository;
import com.connectoBackend.common.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for blocked user operations.
 */
@RestController
@RequestMapping("/api/v1/users/{userId}/blocked-users")
@RequiredArgsConstructor
public class BlockedUserController {

	private final BlockUserService blockUserService;
	private final UserRepository userRepository;

	@GetMapping
	public ApiResponse<List<UserSummaryResponse>> getBlockedUsers(
			@PathVariable UUID userId,
			Authentication authentication
	) {

		return ApiResponse.<List<UserSummaryResponse>>builder()
				.success(true)
				.message("Blocked users fetched successfully.")
				.data(blockUserService.getBlockedUsers(currentUserId(userId, authentication)))
				.build();
	}

	@PostMapping("/{blockedUserId}")
	public ApiResponse<Void> blockUser(
			@PathVariable UUID userId,
			@PathVariable UUID blockedUserId,
			Authentication authentication
	) {

		blockUserService.blockUser(currentUserId(userId, authentication), blockedUserId);

		return ApiResponse.<Void>builder()
				.success(true)
				.message("User blocked successfully.")
				.build();
	}

	@DeleteMapping("/{blockedUserId}")
	public ApiResponse<Void> unblockUser(
			@PathVariable UUID userId,
			@PathVariable UUID blockedUserId,
			Authentication authentication
	) {

		blockUserService.unblockUser(currentUserId(userId, authentication), blockedUserId);

		return ApiResponse.<Void>builder()
				.success(true)
				.message("User unblocked successfully.")
				.build();
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
}