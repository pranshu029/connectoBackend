package com.connectoBackend.user.service.impl;

import com.connectoBackend.common.exception.BadRequestException;
import com.connectoBackend.common.exception.ConflictException;
import com.connectoBackend.common.exception.ResourceNotFoundException;
import com.connectoBackend.notification.entity.Notification;
import com.connectoBackend.notification.enums.NotificationType;
import com.connectoBackend.notification.repository.NotificationRepository;
import com.connectoBackend.user.dto.response.UserSummaryResponse;
import com.connectoBackend.user.entity.Follow;
import com.connectoBackend.user.entity.FollowRequest;
import com.connectoBackend.user.entity.User;
import com.connectoBackend.user.enums.FollowRequestStatus;
import com.connectoBackend.user.repository.FollowRepository;
import com.connectoBackend.user.repository.FollowRequestRepository;
import com.connectoBackend.user.repository.UserRepository;
import com.connectoBackend.user.repository.BlockedUserRepository;
import com.connectoBackend.user.enums.AccountStatus;
import com.connectoBackend.common.exception.ForbiddenException;
import com.connectoBackend.user.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowServiceImpl implements FollowService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final FollowRequestRepository followRequestRepository;
    private final NotificationRepository notificationRepository;
    private final BlockedUserRepository blockedUserRepository;

    @Override
    public void followUser(UUID userId, UUID targetUserId) {
        if (userId.equals(targetUserId)) {
            throw new BadRequestException("You cannot follow yourself.");
        }

        User user = getUser(userId);
        User targetUser = getUser(targetUserId);
        if (user.isDeleted() || targetUser.isDeleted() || targetUser.getAccountStatus() == AccountStatus.DELETED) {
            throw new ResourceNotFoundException("User not found.");
        }
        if (blockedUserRepository.existsByUserAndBlockedUser(user, targetUser)
                || blockedUserRepository.existsByUserAndBlockedUser(targetUser, user)) {
            throw new ForbiddenException("This user interaction is blocked.");
        }

        if (followRepository.existsByFollowerAndFollowing(user, targetUser)) {
            throw new ConflictException("You are already following this user.");
        }

        FollowRequest existingRequest = followRequestRepository.findByRequesterAndTargetUser(user, targetUser).orElse(null);
        if (existingRequest != null && existingRequest.getStatus() == FollowRequestStatus.PENDING) {
            throw new ConflictException("A follow request already exists.");
        }

        boolean requiresApproval = false;
        if (targetUser.getAccountStatus() != null && "PRIVATE".equalsIgnoreCase(targetUser.getAccountStatus().name())) {
            requiresApproval = true;
        }

        if (requiresApproval) {
                FollowRequest request = existingRequest == null ? FollowRequest.builder()
                    .requester(user).targetUser(targetUser).build() : existingRequest;
                request.setStatus(FollowRequestStatus.PENDING);
            followRequestRepository.save(request);

            Notification notification = Notification.builder()
                    .user(targetUser)
                    .actor(user)
                    .type(NotificationType.FOLLOW_REQUEST)
                    .title("Follow request")
                    .message(user.getFirstName() + " " + user.getLastName() + " wants to follow you.")
                    .entityId(request.getId())
                    .entityType("FOLLOW_REQUEST")
                    .readStatus(false)
                    .build();
            notificationRepository.save(notification);
            return;
        }

        Follow follow = Follow.builder()
                .follower(user)
                .following(targetUser)
                .approved(true)
                .build();
        followRepository.save(follow);
    }

    @Override
    public void unfollowUser(UUID userId, UUID targetUserId) {
        User user = getUser(userId);
        User targetUser = getUser(targetUserId);
        followRepository.deleteByFollowerAndFollowing(user, targetUser);
    }

    @Override
    @Transactional(readOnly = true)
    public String getFollowStatus(UUID userId, UUID targetUserId) {
        User user = getUser(userId);
        User targetUser = getUser(targetUserId);

        if (followRepository.existsByFollowerAndFollowing(user, targetUser)) {
            return "FOLLOWING";
        }

                if (followRequestRepository.findByRequesterAndTargetUser(user, targetUser)
				.map(request -> request.getStatus() == FollowRequestStatus.PENDING)
				.orElse(false)) {
            return "REQUEST_SENT";
        }

        if (followRequestRepository.findAllByTargetUserAndStatus(targetUser, FollowRequestStatus.PENDING).stream()
            .anyMatch(req -> req.getRequester().equals(user))) {
            return "REQUEST_RECEIVED";
        }

        return "NOT_FOLLOWING";
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSummaryResponse> getFollowers(UUID userId) {
        User user = getUser(userId);
        return followRepository.findAllByFollowing(user)
                .stream()
                .map(f -> toSummary(f.getFollower()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSummaryResponse> getFollowing(UUID userId) {
        User user = getUser(userId);
        return followRepository.findAllByFollower(user)
                .stream()
                .map(f -> toSummary(f.getFollowing()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSummaryResponse> getPendingFollowRequests(UUID userId) {
        User user = getUser(userId);
        return followRequestRepository.findAllByTargetUserAndStatus(user, FollowRequestStatus.PENDING)
                .stream()
                .map(request -> toSummary(request.getRequester()))
                .toList();
    }

    @Override
    public void acceptFollowRequest(UUID userId, UUID requestId) {
        User user = getUser(userId);
        FollowRequest request = followRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Follow request not found."));

        if (!request.getTargetUser().equals(user)) {
            throw new BadRequestException("You cannot accept someone else's follow request.");
        }
        if (request.getStatus() != FollowRequestStatus.PENDING) {
            throw new BadRequestException("This follow request is no longer pending.");
        }

        request.setStatus(FollowRequestStatus.ACCEPTED);
        followRequestRepository.save(request);

        Follow follow = Follow.builder()
                .follower(request.getRequester())
                .following(request.getTargetUser())
                .approved(true)
                .build();
        followRepository.save(follow);

        Notification notification = Notification.builder()
                .user(request.getRequester())
                .actor(user)
                .type(NotificationType.FOLLOW_ACCEPTED)
                .title("Follow accepted")
                .message(user.getFirstName() + " " + user.getLastName() + " accepted your follow request.")
                .entityId(request.getId())
                .entityType("FOLLOW_REQUEST")
                .readStatus(false)
                .build();
        notificationRepository.save(notification);
    }

    @Override
    public void rejectFollowRequest(UUID userId, UUID requestId) {
        User user = getUser(userId);
        FollowRequest request = followRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Follow request not found."));

        if (!request.getTargetUser().equals(user)) {
            throw new BadRequestException("You cannot reject someone else's follow request.");
        }

        request.setStatus(FollowRequestStatus.REJECTED);
        followRequestRepository.save(request);

        Notification notification = Notification.builder()
                .user(request.getRequester())
                .actor(user)
                .type(NotificationType.FOLLOW_REJECTED)
                .title("Follow rejected")
                .message(user.getFirstName() + " " + user.getLastName() + " rejected your follow request.")
                .entityId(request.getId())
                .entityType("FOLLOW_REQUEST")
                .readStatus(false)
                .build();
        notificationRepository.save(notification);
    }

    private UserSummaryResponse toSummary(User user) {
        return new UserSummaryResponse(
                user.getId(),
                user.getUsername(),
                user.getFirstName() + " " + user.getLastName(),
                user.getProfilePictureUrl(),
                Boolean.TRUE.equals(user.getEmailVerified())
        );
    }

    private User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }
}
