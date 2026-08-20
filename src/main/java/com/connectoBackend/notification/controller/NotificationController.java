package com.connectoBackend.notification.controller;

import com.connectoBackend.common.response.ApiResponse;
import com.connectoBackend.notification.dto.NotificationResponse;
import com.connectoBackend.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.connectoBackend.common.exception.ForbiddenException;
import com.connectoBackend.user.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @GetMapping("/users/{userId}")
    public ApiResponse<List<NotificationResponse>> getNotificationsForUser(@PathVariable UUID userId, Authentication authentication) {
        return ApiResponse.<List<NotificationResponse>>builder()
                .success(true)
                .message("Notifications fetched successfully.")
                .data(notificationService.getNotificationsForUser(currentUserId(userId, authentication)))
                .build();
    }

    @PutMapping("/{notificationId}/read")
    public ApiResponse<Void> markNotificationAsRead(@PathVariable UUID notificationId, Authentication authentication) {
        notificationService.markNotificationAsRead(currentUserId(authentication), notificationId);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Notification marked as read.")
                .build();
    }

    @PutMapping("/users/{userId}/read-all")
    public ApiResponse<Void> markAllNotificationsAsRead(@PathVariable UUID userId, Authentication authentication) {
        notificationService.markAllNotificationsAsRead(currentUserId(userId, authentication));
        return ApiResponse.<Void>builder()
                .success(true)
                .message("All notifications marked as read.")
                .build();
    }

    @GetMapping("/users/{userId}/unread-count")
    public ApiResponse<Long> getUnreadCount(@PathVariable UUID userId, Authentication authentication) {
        return ApiResponse.<Long>builder()
                .success(true)
                .message("Unread notification count fetched successfully.")
                .data(notificationService.getUnreadCount(currentUserId(userId, authentication)))
                .build();
    }

    @DeleteMapping("/{notificationId}")
    public ApiResponse<Void> deleteNotification(@PathVariable UUID notificationId, Authentication authentication) {
        notificationService.deleteNotification(currentUserId(authentication), notificationId);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Notification deleted successfully.")
                .build();
    }

    private UUID currentUserId(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found."))
                .getId();
    }

    private UUID currentUserId(UUID requestedUserId, Authentication authentication) {
        UUID authenticatedUserId = currentUserId(authentication);
        if (!authenticatedUserId.equals(requestedUserId)) {
            throw new ForbiddenException("You can act only as the authenticated user.");
        }
        return authenticatedUserId;
    }
}
