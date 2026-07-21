package com.connectoBackend.user.controller;

import com.connectoBackend.common.response.ApiResponse;
import com.connectoBackend.user.dto.request.UserSettingsRequest;
import com.connectoBackend.user.dto.response.UserSettingsResponse;
import com.connectoBackend.user.service.UserSettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for user settings.
 */
@RestController
@RequestMapping("/api/v1/users/{userId}/settings")
@RequiredArgsConstructor
public class UserSettingsController {

	private final UserSettingsService userSettingsService;

	@GetMapping
	public ApiResponse<UserSettingsResponse> getSettings(
			@PathVariable UUID userId
	) {

		return ApiResponse.<UserSettingsResponse>builder()
				.success(true)
				.message("User settings fetched successfully.")
				.data(userSettingsService.getUserSettings(userId))
				.build();
	}

	@PutMapping
	public ApiResponse<UserSettingsResponse> updateSettings(
			@PathVariable UUID userId,
			@Valid @RequestBody UserSettingsRequest request
	) {

		return ApiResponse.<UserSettingsResponse>builder()
				.success(true)
				.message("User settings updated successfully.")
				.data(userSettingsService.updateUserSettings(userId, request))
				.build();
	}
}