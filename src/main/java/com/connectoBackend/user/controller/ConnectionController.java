package com.connectoBackend.user.controller;

import com.connectoBackend.common.response.ApiResponse;
import com.connectoBackend.user.dto.response.UserSummaryResponse;
import com.connectoBackend.user.enums.ConnectionStatus;
import com.connectoBackend.user.service.ConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class ConnectionController {

    private final ConnectionService connectionService;

    @PostMapping("/{userId}/connections/{targetUserId}")
    public ApiResponse<Void> sendConnectionRequest(
            @PathVariable UUID userId,
            @PathVariable UUID targetUserId
    ) {
        connectionService.sendConnectionRequest(userId, targetUserId);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Connection request sent successfully.")
                .build();
    }

    @GetMapping("/{userId}/connections/{targetUserId}/status")
    public ApiResponse<ConnectionStatus> getConnectionStatus(
            @PathVariable UUID userId,
            @PathVariable UUID targetUserId
    ) {
        return ApiResponse.<ConnectionStatus>builder()
                .success(true)
                .message("Connection status fetched successfully.")
                .data(connectionService.getConnectionStatus(userId, targetUserId))
                .build();
    }

    @PutMapping("/{userId}/connections/{targetUserId}/accept")
    public ApiResponse<Void> acceptConnectionRequest(
            @PathVariable UUID userId,
            @PathVariable UUID targetUserId
    ) {
        connectionService.acceptConnectionRequest(userId, targetUserId);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Connection request accepted successfully.")
                .build();
    }

    @PutMapping("/{userId}/connections/{targetUserId}/reject")
    public ApiResponse<Void> rejectConnectionRequest(
            @PathVariable UUID userId,
            @PathVariable UUID targetUserId
    ) {
        connectionService.rejectConnectionRequest(userId, targetUserId);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Connection request rejected successfully.")
                .build();
    }

    @DeleteMapping("/{userId}/connections/{targetUserId}")
    public ApiResponse<Void> removeConnection(
            @PathVariable UUID userId,
            @PathVariable UUID targetUserId
    ) {
        connectionService.removeConnection(userId, targetUserId);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Connection removed successfully.")
                .build();
    }

    @DeleteMapping("/{userId}/connections/{targetUserId}/request")
    public ApiResponse<Void> cancelConnectionRequest(
            @PathVariable UUID userId,
            @PathVariable UUID targetUserId
    ) {
        connectionService.cancelConnectionRequest(userId, targetUserId);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Connection request cancelled successfully.")
                .build();
    }

    @GetMapping("/{userId}/connections")
    public ApiResponse<List<UserSummaryResponse>> getConnections(
            @PathVariable UUID userId
    ) {
        return ApiResponse.<List<UserSummaryResponse>>builder()
                .success(true)
                .message("Connections fetched successfully.")
                .data(connectionService.getConnections(userId))
                .build();
    }

    @GetMapping("/{userId}/connections/incoming")
    public ApiResponse<List<UserSummaryResponse>> getIncomingRequests(
            @PathVariable UUID userId
    ) {
        return ApiResponse.<List<UserSummaryResponse>>builder()
                .success(true)
                .message("Incoming requests fetched successfully.")
                .data(connectionService.getIncomingRequests(userId))
                .build();
    }

    @GetMapping("/{userId}/connections/sent")
    public ApiResponse<List<UserSummaryResponse>> getSentRequests(
            @PathVariable UUID userId
    ) {
        return ApiResponse.<List<UserSummaryResponse>>builder()
                .success(true)
                .message("Sent requests fetched successfully.")
                .data(connectionService.getSentRequests(userId))
                .build();
    }
}
