package com.connectoBackend.user.service;

import com.connectoBackend.user.dto.response.UserSummaryResponse;
import com.connectoBackend.user.entity.FollowRequest;

import java.util.List;
import java.util.UUID;

public interface FollowService {

    void followUser(UUID userId, UUID targetUserId);

    void unfollowUser(UUID userId, UUID targetUserId);

    String getFollowStatus(UUID userId, UUID targetUserId);

    List<UserSummaryResponse> getFollowers(UUID userId);

    List<UserSummaryResponse> getFollowing(UUID userId);

    List<UserSummaryResponse> getPendingFollowRequests(UUID userId);

    void acceptFollowRequest(UUID userId, UUID requestId);

    void rejectFollowRequest(UUID userId, UUID requestId);
}
