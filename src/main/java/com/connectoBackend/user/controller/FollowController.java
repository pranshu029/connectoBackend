package com.connectoBackend.user.controller;

import com.connectoBackend.common.response.ApiResponse;
import com.connectoBackend.user.dto.response.UserSummaryResponse;
import com.connectoBackend.user.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{userId}/follow/{targetUserId}")
    public ApiResponse<Void> followUser(
            @PathVariable UUID userId,
            @PathVariable UUID targetUserId
    ) {
        followService.followUser(userId, targetUserId);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Follow action processed successfully.")
                .build();
    }

    @DeleteMapping("/{userId}/follow/{targetUserId}")
    public ApiResponse<Void> unfollowUser(
            @PathVariable UUID userId,
            @PathVariable UUID targetUserId
    ) {
        followService.unfollowUser(userId, targetUserId);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Unfollowed successfully.")
                .build();
    }

    @GetMapping("/{userId}/follow/{targetUserId}/status")
    public ApiResponse<String> getFollowStatus(
            @PathVariable UUID userId,
            @PathVariable UUID targetUserId
    ) {
        return ApiResponse.<String>builder()
                .success(true)
                .message("Follow status fetched successfully.")
                .data(followService.getFollowStatus(userId, targetUserId))
                .build();
    }

    @GetMapping("/{userId}/followers")
    public ApiResponse<List<UserSummaryResponse>> getFollowers(
            @PathVariable UUID userId
    ) {
        return ApiResponse.<List<UserSummaryResponse>>builder()
                .success(true)
                .message("Followers fetched successfully.")
                .data(followService.getFollowers(userId))
                .build();
    }

    @GetMapping("/{userId}/following")
    public ApiResponse<List<UserSummaryResponse>> getFollowing(
            @PathVariable UUID userId
    ) {
        return ApiResponse.<List<UserSummaryResponse>>builder()
                .success(true)
                .message("Following fetched successfully.")
                .data(followService.getFollowing(userId))
                .build();
    }

    @GetMapping("/{userId}/follow-requests")
    public ApiResponse<List<UserSummaryResponse>> getPendingFollowRequests(
            @PathVariable UUID userId
    ) {
        return ApiResponse.<List<UserSummaryResponse>>builder()
                .success(true)
                .message("Pending follow requests fetched successfully.")
                .data(followService.getPendingFollowRequests(userId))
                .build();
    }

    @PutMapping("/{userId}/follow-requests/{requestId}/accept")
    public ApiResponse<Void> acceptFollowRequest(
            @PathVariable UUID userId,
            @PathVariable UUID requestId
    ) {
        followService.acceptFollowRequest(userId, requestId);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Follow request accepted successfully.")
                .build();
    }

    @PutMapping("/{userId}/follow-requests/{requestId}/reject")
    public ApiResponse<Void> rejectFollowRequest(
            @PathVariable UUID userId,
            @PathVariable UUID requestId
    ) {
        followService.rejectFollowRequest(userId, requestId);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Follow request rejected successfully.")
                .build();
    }
}
