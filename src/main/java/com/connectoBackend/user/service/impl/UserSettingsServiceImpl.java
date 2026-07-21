package com.connectoBackend.user.service.impl;

import com.connectoBackend.common.exception.ResourceNotFoundException;
import com.connectoBackend.user.dto.request.UserSettingsRequest;
import com.connectoBackend.user.dto.response.UserSettingsResponse;
import com.connectoBackend.user.entity.User;
import com.connectoBackend.user.entity.UserSettings;
import com.connectoBackend.user.mapper.UserSettingsMapper;
import com.connectoBackend.user.repository.UserRepository;
import com.connectoBackend.user.repository.UserSettingsRepository;
import com.connectoBackend.user.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementation of {@link UserSettingsService}.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserSettingsServiceImpl implements UserSettingsService {

	private final UserRepository userRepository;
	private final UserSettingsRepository userSettingsRepository;
	private final UserSettingsMapper userSettingsMapper;

	@Override
	@Transactional(readOnly = true)
	public UserSettingsResponse getUserSettings(UUID userId) {
		return userSettingsMapper.toResponse(getOrCreateSettings(userId));
	}

	@Override
	public UserSettingsResponse updateUserSettings(UUID userId, UserSettingsRequest request) {

		UserSettings settings = getOrCreateSettings(userId);
		userSettingsMapper.updateEntity(request, settings);
		settings = userSettingsRepository.save(settings);

		return userSettingsMapper.toResponse(settings);
	}

	private UserSettings getOrCreateSettings(UUID userId) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found."));

		return userSettingsRepository.findByUser(user)
				.orElseGet(() -> userSettingsRepository.save(
						UserSettings.builder()
								.user(user)
								.notificationsEnabled(true)
								.readReceiptsEnabled(true)
								.onlineStatusVisible(true)
								.build()
						));
	}
}