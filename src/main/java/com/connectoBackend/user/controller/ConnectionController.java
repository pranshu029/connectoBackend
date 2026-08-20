package com.connectoBackend.user.controller;

import com.connectoBackend.common.response.ApiResponse;
import com.connectoBackend.user.dto.response.UserSummaryResponse;
import com.connectoBackend.user.enums.ConnectionStatus;
import com.connectoBackend.user.service.ConnectionService;
import com.connectoBackend.user.repository.UserRepository;
import com.connectoBackend.common.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class ConnectionController {

    private final ConnectionService connectionService;
        private final UserRepository userRepository;

    @PostMapping("/{userId}/connections/{targetUserId}")
    public ApiResponse<Void> sendConnectionRequest(
            @PathVariable UUID userId,
            @PathVariable UUID targetUserId
                        , Authentication authentication
    ) {
                connectionService.sendConnectionRequest(currentUserId(userId, authentication), targetUserId);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Connection request sent successfully.")
                .build();
    }

    @GetMapping("/{userId}/connections/{targetUserId}/status")
    public ApiResponse<ConnectionStatus> getConnectionStatus(
            @PathVariable UUID userId,
            @PathVariable UUID targetUserId
            , Authentication authentication
    ) {
        return ApiResponse.<ConnectionStatus>builder()
                .success(true)
                .message("Connection status fetched successfully.")
                .data(connectionService.getConnectionStatus(currentUserId(userId, authentication), targetUserId))
                .build();
    }

    @PutMapping("/{userId}/connections/{targetUserId}/accept")
    public ApiResponse<Void> acceptConnectionRequest(
            @PathVariable UUID userId,
            @PathVariable UUID targetUserId
            , Authentication authentication
    ) {
        connectionService.acceptConnectionRequest(currentUserId(userId, authentication), targetUserId);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Connection request accepted successfully.")
                .build();
    }

    @PutMapping("/{userId}/connections/{targetUserId}/reject")
    public ApiResponse<Void> rejectConnectionRequest(
            @PathVariable UUID userId,
            @PathVariable UUID targetUserId
            , Authentication authentication
    ) {
        connectionService.rejectConnectionRequest(currentUserId(userId, authentication), targetUserId);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Connection request rejected successfully.")
                .build();
    }

    @DeleteMapping("/{userId}/connections/{targetUserId}")
    public ApiResponse<Void> removeConnection(
            @PathVariable UUID userId,
            @PathVariable UUID targetUserId
            , Authentication authentication
    ) {
        connectionService.removeConnection(currentUserId(userId, authentication), targetUserId);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Connection removed successfully.")
                .build();
    }

    @DeleteMapping("/{userId}/connections/{targetUserId}/request")
    public ApiResponse<Void> cancelConnectionRequest(
            @PathVariable UUID userId,
            @PathVariable UUID targetUserId
            , Authentication authentication
    ) {
        connectionService.cancelConnectionRequest(currentUserId(userId, authentication), targetUserId);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Connection request cancelled successfully.")
                .build();
    }

    @GetMapping("/{userId}/connections")
    public ApiResponse<List<UserSummaryResponse>> getConnections(
            @PathVariable UUID userId
            , Authentication authentication
    ) {
        return ApiResponse.<List<UserSummaryResponse>>builder()
                .success(true)
                .message("Connections fetched successfully.")
                .data(connectionService.getConnections(currentUserId(userId, authentication)))
                .build();
    }

    @GetMapping("/{userId}/connections/incoming")
    public ApiResponse<List<UserSummaryResponse>> getIncomingRequests(
            @PathVariable UUID userId
            , Authentication authentication
    ) {
        return ApiResponse.<List<UserSummaryResponse>>builder()
                .success(true)
                .message("Incoming requests fetched successfully.")
                .data(connectionService.getIncomingRequests(currentUserId(userId, authentication)))
                .build();
    }

    @GetMapping("/{userId}/connections/sent")
    public ApiResponse<List<UserSummaryResponse>> getSentRequests(
            @PathVariable UUID userId
            , Authentication authentication
    ) {
        return ApiResponse.<List<UserSummaryResponse>>builder()
                .success(true)
                .message("Sent requests fetched successfully.")
                                .data(connectionService.getSentRequests(currentUserId(userId, authentication)))
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
