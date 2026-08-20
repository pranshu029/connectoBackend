package com.connectoBackend.user.service;

import com.connectoBackend.user.dto.request.CreateUserRequest;
import com.connectoBackend.user.dto.request.UpdateUserRequest;
import com.connectoBackend.user.dto.response.UserProfileResponse;
import com.connectoBackend.user.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for user operations.
 */
public interface UserService {

    // -> Create a new user.
    UserResponse createUser(CreateUserRequest request);

    // -> Get user by id.
    UserResponse getUserById(UUID userId, UUID viewerId);

    // -> Get user profile.
    UserProfileResponse getUserProfile(UUID userId, UUID viewerId);

    UUID getUserIdByEmail(String email);

    // -> Get user by email.
    UserResponse getUserByEmail(String email, UUID viewerId);

    // -> Get user by username.
    UserResponse getUserByUsername(String username, UUID viewerId);

    // -> Get all users.
    Page<UserResponse> getAllUsers(Pageable pageable, UUID viewerId);

    // -> Update user profile.
    UserProfileResponse updateUser(
            UUID userId,
            UUID viewerId,
            UpdateUserRequest request
    );

    // -> Soft delete user.
    void deleteUser(UUID userId);

}