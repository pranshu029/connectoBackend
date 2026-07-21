package com.connectoBackend.user.service;

import com.connectoBackend.user.dto.response.UserSummaryResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service for user blocking operations.
 */
public interface BlockUserService {

	void blockUser(UUID userId, UUID blockedUserId);

	void unblockUser(UUID userId, UUID blockedUserId);

	List<UserSummaryResponse> getBlockedUsers(UUID userId);
}