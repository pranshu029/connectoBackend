package com.connectoBackend.notification.controller;

import com.connectoBackend.common.response.ApiResponse;
import com.connectoBackend.notification.dto.NotificationResponse;
import com.connectoBackend.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/users/{userId}")
    public ApiResponse<List<NotificationResponse>> getNotificationsForUser(@PathVariable UUID userId) {
        return ApiResponse.<List<NotificationResponse>>builder()
                .success(true)
                .message("Notifications fetched successfully.")
                .data(notificationService.getNotificationsForUser(userId))
                .build();
    }

    @PutMapping("/{notificationId}/read")
    public ApiResponse<Void> markNotificationAsRead(@PathVariable UUID notificationId) {
        notificationService.markNotificationAsRead(notificationId);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Notification marked as read.")
                .build();
    }

    @PutMapping("/users/{userId}/read-all")
    public ApiResponse<Void> markAllNotificationsAsRead(@PathVariable UUID userId) {
        notificationService.markAllNotificationsAsRead(userId);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("All notifications marked as read.")
                .build();
    }

    @DeleteMapping("/{notificationId}")
    public ApiResponse<Void> deleteNotification(@PathVariable UUID notificationId) {
        notificationService.deleteNotification(notificationId);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Notification deleted successfully.")
                .build();
    }
}
