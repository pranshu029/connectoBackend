package com.connectoBackend.notification.service.impl;

import com.connectoBackend.common.exception.ResourceNotFoundException;
import com.connectoBackend.common.exception.ForbiddenException;
import com.connectoBackend.notification.dto.NotificationResponse;
import com.connectoBackend.notification.entity.Notification;
import com.connectoBackend.notification.repository.NotificationRepository;
import com.connectoBackend.notification.service.NotificationService;
import com.connectoBackend.user.entity.User;
import com.connectoBackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsForUser(UUID userId) {
        User user = getUser(userId);
        return notificationRepository.findAllByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void markNotificationAsRead(UUID userId, UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found."));
        requireOwner(userId, notification);
        notification.setReadStatus(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllNotificationsAsRead(UUID userId) {
        User user = getUser(userId);
        List<Notification> notifications = notificationRepository.findAllByUserOrderByCreatedAtDesc(user);
        notifications.forEach(notification -> notification.setReadStatus(true));
        notificationRepository.saveAll(notifications);
    }

    @Override
    public void deleteNotification(UUID userId, UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found."));
        requireOwner(userId, notification);
        notificationRepository.delete(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByUserAndReadStatusFalse(getUser(userId));
    }

    private void requireOwner(UUID userId, Notification notification) {
        if (!notification.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You can manage only your own notifications.");
        }
    }

    private NotificationResponse toResponse(Notification notification) {
        User actor = notification.getActor();
        String actorName = actor != null ? actor.getFirstName() + " " + actor.getLastName() : "System";

        return new NotificationResponse(
                notification.getId(),
                notification.getUser().getId(),
                actor != null ? actor.getId() : null,
                actorName,
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getEntityId(),
                notification.getEntityType(),
                notification.isReadStatus(),
                notification.getCreatedAt()
        );
    }

    private User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }
}
