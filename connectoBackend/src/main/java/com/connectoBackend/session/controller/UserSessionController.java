package com.connectoBackend.session.controller;

import com.connectoBackend.common.response.ApiResponse;
import com.connectoBackend.session.entity.UserSession;
import com.connectoBackend.session.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for user session operations.
 */
@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class UserSessionController {

    private final UserSessionService userSessionService;

    // -> Get active sessions of a user.
    @GetMapping("/users/{userId}")
    public ApiResponse<List<UserSession>> getActiveSessions(
            @PathVariable UUID userId
    ) {

        return ApiResponse.<List<UserSession>>builder()
                .success(true)
                .message("Active sessions fetched successfully.")
                .data(userSessionService.getActiveSessions(userId))
                .build();
    }

    // -> Logout a session.
    @DeleteMapping("/{sessionId}")
    public ApiResponse<Void> logout(
            @PathVariable UUID sessionId
    ) {

        userSessionService.logout(sessionId);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Session logged out successfully.")
                .build();
    }

    // -> Logout all sessions of a user.
    @DeleteMapping("/users/{userId}")
    public ApiResponse<Void> logoutAll(
            @PathVariable UUID userId
    ) {

        userSessionService.logoutAll(userId);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("All sessions logged out successfully.")
                .build();
    }
}