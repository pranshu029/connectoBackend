package com.connectoBackend.notification.dto;

import com.connectoBackend.notification.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID userId,
        UUID actorId,
        String actorName,
        NotificationType type,
        String title,
        String message,
        UUID entityId,
        String entityType,
        boolean read,
        LocalDateTime createdAt
) {
}
