package com.connectoBackend.user.controller;

import com.connectoBackend.common.response.ApiResponse;
import com.connectoBackend.user.dto.response.UserSummaryResponse;
import com.connectoBackend.user.service.BlockUserService;
import lombok.RequiredArgsConstructor;
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

	@GetMapping
	public ApiResponse<List<UserSummaryResponse>> getBlockedUsers(
			@PathVariable UUID userId
	) {

		return ApiResponse.<List<UserSummaryResponse>>builder()
				.success(true)
				.message("Blocked users fetched successfully.")
				.data(blockUserService.getBlockedUsers(userId))
				.build();
	}

	@PostMapping("/{blockedUserId}")
	public ApiResponse<Void> blockUser(
			@PathVariable UUID userId,
			@PathVariable UUID blockedUserId
	) {

		blockUserService.blockUser(userId, blockedUserId);

		return ApiResponse.<Void>builder()
				.success(true)
				.message("User blocked successfully.")
				.build();
	}

	@DeleteMapping("/{blockedUserId}")
	public ApiResponse<Void> unblockUser(
			@PathVariable UUID userId,
			@PathVariable UUID blockedUserId
	) {

		blockUserService.unblockUser(userId, blockedUserId);

		return ApiResponse.<Void>builder()
				.success(true)
				.message("User unblocked successfully.")
				.build();
	}
}