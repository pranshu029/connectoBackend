package com.connectoBackend.user.service.impl;

import com.connectoBackend.common.exception.BadRequestException;
import com.connectoBackend.common.exception.ConflictException;
import com.connectoBackend.common.exception.ResourceNotFoundException;
import com.connectoBackend.user.dto.response.UserSummaryResponse;
import com.connectoBackend.user.entity.BlockedUser;
import com.connectoBackend.user.entity.User;
import com.connectoBackend.user.repository.BlockedUserRepository;
import com.connectoBackend.user.repository.UserRepository;
import com.connectoBackend.user.service.BlockUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of {@link BlockUserService}.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class BlockUserServiceImpl implements BlockUserService {

	private final UserRepository userRepository;
	private final BlockedUserRepository blockedUserRepository;

	@Override
	public void blockUser(UUID userId, UUID blockedUserId) {

		if (userId.equals(blockedUserId)) {
			throw new BadRequestException(" You cannot block yourself ");
		}

		User user = getUser(userId);
		User blockedUser = getUser(blockedUserId);

		if (blockedUserRepository.existsByUserAndBlockedUser(user, blockedUser)) {
			throw new ConflictException("User is already blocked.");
		}

		blockedUserRepository.save(
				BlockedUser.builder()
						.user(user)
						.blockedUser(blockedUser)
						.build()
		);
	}

	@Override
	public void unblockUser(UUID userId, UUID blockedUserId) {

		User user = getUser(userId);
		User blockedUser = getUser(blockedUserId);

		if (!blockedUserRepository.existsByUserAndBlockedUser(user, blockedUser)) {
			throw new ResourceNotFoundException("Blocked user relationship not found.");
		}

		blockedUserRepository.deleteByUserAndBlockedUser(user, blockedUser);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserSummaryResponse> getBlockedUsers(UUID userId) {

		User user = getUser(userId);

		return blockedUserRepository.findAllByUser(user)
				.stream()
				.map(blockedUser -> {
					User blocked = blockedUser.getBlockedUser();
					return new UserSummaryResponse(
							blocked.getId(),
							blocked.getUsername(),
							blocked.getFirstName() + " " + blocked.getLastName(),
							blocked.getProfilePictureUrl(),
							Boolean.TRUE.equals(blocked.getEmailVerified())
					);
				})
				.toList();
	}

	private User getUser(UUID userId) {

		return userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found."));
	}
}