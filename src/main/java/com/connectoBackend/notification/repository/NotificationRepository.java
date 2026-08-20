package com.connectoBackend.notification.repository;

import com.connectoBackend.notification.entity.Notification;
import com.connectoBackend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findAllByUserOrderByCreatedAtDesc(User user);

    List<Notification> findAllByUserAndReadStatusFalseOrderByCreatedAtDesc(User user);

    long countByUserAndReadStatusFalse(User user);
}
