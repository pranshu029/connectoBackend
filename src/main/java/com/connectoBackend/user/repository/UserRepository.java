package com.connectoBackend.user.repository;

import com.connectoBackend.user.entity.User;
import com.connectoBackend.user.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("select u from User u where u.id = :id")
        Optional<User> findByIdForUpdate(@Param("id") UUID id);

    // -> Find user by email.
    Optional<User> findByEmail(String email);

        Optional<User> findByEmailAndAccountStatusAndDeletedFalse(String email, AccountStatus accountStatus);

        Page<User> findAllByDeletedFalse(Pageable pageable);

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