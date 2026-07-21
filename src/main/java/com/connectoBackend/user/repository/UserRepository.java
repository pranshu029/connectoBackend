package com.connectoBackend.user.repository;

import com.connectoBackend.user.entity.User;
import com.connectoBackend.user.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // -> Find user by email.
    Optional<User> findByEmail(String email);

    // -> Find user by username.
    Optional<User> findByUsername(String username);

    // -> Check if email already exists.
    Boolean existsByEmail(String email);

    // -> Check if username already exists.
    boolean existsByUsername(String username);

    // -> Find user by email and account status.
    Optional<User> findByEmailAndAccountStatus(
            String email,
            AccountStatus accountStatus
    );

    // -> Find user by username and account status.
    Optional<User> findByUsernameAndAccountStatus(
            String username,
            AccountStatus accountStatus
    );

}