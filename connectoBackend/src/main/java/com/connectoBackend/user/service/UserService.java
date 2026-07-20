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
    UserResponse getUserById(UUID userId);

    // -> Get user profile.
    UserProfileResponse getUserProfile(UUID userId);

    // -> Get user by email.
    UserResponse getUserByEmail(String email);

    // -> Get user by username.
    UserResponse getUserByUsername(String username);

    // -> Get all users.
    Page<UserResponse> getAllUsers(Pageable pageable);

    // -> Update user profile.
    UserProfileResponse updateUser(
            UUID userId,
            UpdateUserRequest request
    );

    // -> Soft delete user.
    void deleteUser(UUID userId);

}