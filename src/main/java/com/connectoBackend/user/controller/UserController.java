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
import org.springframework.security.core.Authentication;
import com.connectoBackend.common.exception.ForbiddenException;
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
            @PathVariable UUID userId,
            Authentication authentication
    ) {

        UserResponse response = userService.getUserById(userId, viewerId(authentication));

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User fetched successfully.")
                .data(response)
                .build();
    }

    // -> Get complete user profile.
    @GetMapping("/{userId}/profile")
    public ApiResponse<UserProfileResponse> getUserProfile(
            @PathVariable UUID userId,
            Authentication authentication
    ) {

        UserProfileResponse response = userService.getUserProfile(
                userId,
                userService.getUserIdByEmail(authentication.getName())
        );

        return ApiResponse.<UserProfileResponse>builder()
                .success(true)
                .message("User profile fetched successfully.")
                .data(response)
                .build();
    }

    // -> Get user by email.
    @GetMapping("/email")
    public ApiResponse<UserResponse> getUserByEmail(
            @RequestParam String email,
            Authentication authentication
    ) {

        UserResponse response = userService.getUserByEmail(email, viewerId(authentication));

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User fetched successfully.")
                .data(response)
                .build();
    }

    // -> Get user by username.
    @GetMapping("/username")
    public ApiResponse<UserResponse> getUserByUsername(
            @RequestParam String username,
            Authentication authentication
    ) {

        UserResponse response = userService.getUserByUsername(username, viewerId(authentication));

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User fetched successfully.")
                .data(response)
                .build();
    }

    // -> Get all users.
    @GetMapping
    public ApiResponse<Page<UserResponse>> getAllUsers(
            Pageable pageable,
            Authentication authentication
    ) {

        Page<UserResponse> response = userService.getAllUsers(pageable, viewerId(authentication));

        return ApiResponse.<Page<UserResponse>>builder()
                .success(true)
                .message("Users fetched successfully.")
                .data(response)
                .build();
    }

        private UUID viewerId(Authentication authentication) {
                return userService.getUserIdByEmail(authentication.getName());
        }

    // -> Update user profile.
    @PutMapping("/{userId}")
    public ApiResponse<UserProfileResponse> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserRequest request,
            Authentication authentication
    ) {

        UserProfileResponse response = userService.updateUser(
                userId,
                userService.getUserIdByEmail(authentication.getName()),
                request
        );

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
            @PathVariable UUID userId,
            Authentication authentication
    ) {

                UUID authenticatedUserId = userService.getUserIdByEmail(authentication.getName());
                if (!authenticatedUserId.equals(userId)) {
                        throw new ForbiddenException("You can delete only your own account.");
                }
                userService.deleteUser(userId);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("User deleted successfully.")
                .build();
    }

}