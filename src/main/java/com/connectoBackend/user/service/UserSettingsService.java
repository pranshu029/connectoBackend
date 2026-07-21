package com.connectoBackend.user.service;

import com.connectoBackend.user.dto.request.UserSettingsRequest;
import com.connectoBackend.user.dto.response.UserSettingsResponse;

import java.util.UUID;

/**
 * Service for managing user settings.
 */
public interface UserSettingsService {

	UserSettingsResponse getUserSettings(UUID userId);

	UserSettingsResponse updateUserSettings(UUID userId, UserSettingsRequest request);
}