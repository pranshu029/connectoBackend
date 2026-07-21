package com.connectoBackend.user.repository;

import com.connectoBackend.user.entity.BlockedUser;
import com.connectoBackend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for blocked user relationships.
 */
@Repository
public interface BlockedUserRepository extends JpaRepository<BlockedUser, UUID> {

	List<BlockedUser> findAllByUser(User user);

	Optional<BlockedUser> findByUserAndBlockedUser(User user, User blockedUser);

	boolean existsByUserAndBlockedUser(User user, User blockedUser);

	void deleteByUserAndBlockedUser(User user, User blockedUser);
}