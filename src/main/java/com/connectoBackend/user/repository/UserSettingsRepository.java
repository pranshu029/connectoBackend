package com.connectoBackend.user.repository;

import com.connectoBackend.user.entity.User;
import com.connectoBackend.user.entity.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for user settings.
 */
@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, UUID> {

	Optional<UserSettings> findByUser(User user);

	boolean existsByUser(User user);
}