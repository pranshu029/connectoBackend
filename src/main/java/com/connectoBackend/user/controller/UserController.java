package com.connectoBackend.user.controller;

import com.connectoBackend.common.response.ApiResponse;
import com.connectoBackend.user.dto.request.CreateUserRequest;
import com.connectoBackend.user.dto.request.UpdateUserRequest;
import com.connectoBackend.user.dto.response.UserProfileResponse;
import com.connectoBackend.user.dto.response.UserResponse;
import com.connectoBackend.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for user operations.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // -> Create a new user.
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request
    ) {

        UserResponse response = userService.createUser(request);

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User created successfully.")
                .data(response)
                .build();
    }

    // -> Get user by id.
    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUserById(
            @PathVariable UUID userId
    ) {

        UserResponse response = userService.getUserById(userId);

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User fetched successfully.")
                .data(response)
                .build();
    }

    // -> Get complete user profile.
    @GetMapping("/{userId}/profile")
    public ApiResponse<UserProfileResponse> getUserProfile(
            @PathVariable UUID userId
    ) {

        UserProfileResponse response = userService.getUserProfile(userId);

        return ApiResponse.<UserProfileResponse>builder()
                .success(true)
                .message("User profile fetched successfully.")
                .data(response)
                .build();
    }

    // -> Get user by email.
    @GetMapping("/email")
    public ApiResponse<UserResponse> getUserByEmail(
            @RequestParam String email
    ) {

        UserResponse response = userService.getUserByEmail(email);

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User fetched successfully.")
                .data(response)
                .build();
    }

    // -> Get user by username.
    @GetMapping("/username")
    public ApiResponse<UserResponse> getUserByUsername(
            @RequestParam String username
    ) {

        UserResponse response = userService.getUserByUsername(username);

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User fetched successfully.")
                .data(response)
                .build();
    }

    // -> Get all users.
    @GetMapping
    public ApiResponse<Page<UserResponse>> getAllUsers(
            Pageable pageable
    ) {

        Page<UserResponse> response = userService.getAllUsers(pageable);

        return ApiResponse.<Page<UserResponse>>builder()
                .success(true)
                .message("Users fetched successfully.")
                .data(response)
                .build();
    }

    // -> Update user profile.
    @PutMapping("/{userId}")
    public ApiResponse<UserProfileResponse> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserRequest request
    ) {

        UserProfileResponse response = userService.updateUser(userId, request);

        return ApiResponse.<UserProfileResponse>builder()
                .success(true)
                .message("User updated successfully.")
                .data(response)
                .build();
    }

    // -> Soft delete user.
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> deleteUser(
            @PathVariable UUID userId
    ) {

        userService.deleteUser(userId);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("User deleted successfully.")
                .build();
    }

}