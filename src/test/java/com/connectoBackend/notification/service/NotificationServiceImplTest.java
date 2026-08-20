package com.connectoBackend.notification.service;

import com.connectoBackend.common.exception.ForbiddenException;
import com.connectoBackend.notification.entity.Notification;
import com.connectoBackend.notification.repository.NotificationRepository;
import com.connectoBackend.notification.service.impl.NotificationServiceImpl;
import com.connectoBackend.user.entity.User;
import com.connectoBackend.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class NotificationServiceImplTest {

    private final NotificationRepository notificationRepository = Mockito.mock(NotificationRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final NotificationServiceImpl service = new NotificationServiceImpl(notificationRepository, userRepository);

    @Test
    void userCannotMarkAnotherUsersNotificationAsRead() {
        UUID ownerId = UUID.randomUUID();
        UUID attackerId = UUID.randomUUID();
        User owner = new User();
        owner.setId(ownerId);
        Notification notification = Notification.builder().user(owner).build();
        UUID notificationId = UUID.randomUUID();
        when(notificationRepository.findById(notificationId)).thenReturn(java.util.Optional.of(notification));

        assertThrows(ForbiddenException.class,
                () -> service.markNotificationAsRead(attackerId, notificationId));
    }
}
