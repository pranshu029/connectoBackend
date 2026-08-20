package com.connectoBackend.notification.service;

import com.connectoBackend.notification.dto.NotificationResponse;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    List<NotificationResponse> getNotificationsForUser(UUID userId);

    void markNotificationAsRead(UUID notificationId);

    void markAllNotificationsAsRead(UUID userId);

    void deleteNotification(UUID notificationId);
}
