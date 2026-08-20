package com.connectoBackend.user.service.impl;

import com.connectoBackend.auth.service.EmailVerificationService;
import com.connectoBackend.common.exception.ConflictException;
import com.connectoBackend.common.exception.ForbiddenException;
import com.connectoBackend.common.exception.ResourceNotFoundException;
import com.connectoBackend.user.dto.request.CreateUserRequest;
import com.connectoBackend.user.dto.request.UpdateUserRequest;
import com.connectoBackend.user.dto.response.UserProfileResponse;
import com.connectoBackend.user.dto.response.UserResponse;
import com.connectoBackend.user.entity.User;
import com.connectoBackend.user.entity.UserSettings;
import com.connectoBackend.user.enums.AccountStatus;
import com.connectoBackend.user.enums.UserRole;
import com.connectoBackend.user.mapper.UserMapper;
import com.connectoBackend.user.repository.UserRepository;
import com.connectoBackend.user.repository.UserSettingsRepository;
import com.connectoBackend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementation of {@link UserService}.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserSettingsRepository userSettingsRepository;
    private final EmailVerificationService emailVerificationService;

    @Override
    public UserResponse createUser(CreateUserRequest request) {

        // -> Check email uniqueness.
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists.");
        }

        // -> Check username uniqueness
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already exists.");
        }

        emailVerificationService.consumeRegistrationToken(
            request.getEmail(),
            request.getRegistrationToken()
        );

        // -> Map request to entity.
        User user = userMapper.toEntity(request);

        // -> Initialize default values.
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER);
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setEmailVerified(Boolean.TRUE);

        // -> Persist user.
        user = userRepository.save(user);

        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID userId, UUID viewerId) {

        User user = getUser(userId);
        return toVisibleUserResponse(user, viewerId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(UUID userId, UUID viewerId) {

        User user = getUser(userId);
        UserProfileResponse response = userMapper.toProfileResponse(user);
        if (!userId.equals(viewerId)) {
            UserSettings settings = userSettingsRepository.findByUser(user).orElse(null);
            if (settings == null || !Boolean.TRUE.equals(settings.getFirstNamePublic())) {
                response.setFirstName(null);
            }
            if (settings == null || !Boolean.TRUE.equals(settings.getLastNamePublic())) {
                response.setLastName(null);
            }
			response.setEmail(null);
            if (settings == null || !Boolean.TRUE.equals(settings.getPhoneNumberPublic())) {
                response.setPhoneNumber(null);
            }
            if (settings == null || !Boolean.TRUE.equals(settings.getDateOfBirthPublic())) {
                response.setDateOfBirth(null);
            }
            if (settings == null || !Boolean.TRUE.equals(settings.getGenderPublic())) {
                response.setGender(null);
            }
            if (settings == null || !Boolean.TRUE.equals(settings.getProfilePicturePublic())) {
                response.setProfilePictureUrl(null);
            }
            response.setFullName(buildVisibleFullName(response));
            response.setRole(null);
            response.setAccountStatus(null);
            response.setEmailVerified(false);
            response.setLastSeenAt(null);
            response.setCreatedAt(null);
            response.setUpdatedAt(null);
        }
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email, UUID viewerId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        return toVisibleUserResponse(user, viewerId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username, UUID viewerId) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        return toVisibleUserResponse(user, viewerId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable, UUID viewerId) {

        return userRepository.findAllByDeletedFalse(pageable)
                .map(user -> toVisibleUserResponse(user, viewerId));
    }

    @Override
    public UserProfileResponse updateUser(UUID userId, UUID viewerId, UpdateUserRequest request) {

        if (!userId.equals(viewerId)) {
            throw new ForbiddenException("You can update only your own profile.");
        }

        User user = getUser(userId);

        // -> Update entity.
        userMapper.updateEntity(request, user);

        user = userRepository.save(user);

        return userMapper.toProfileResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UUID getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."))
                .getId();
    }

    private String buildVisibleFullName(UserProfileResponse response) {
        String fullName = ((response.getFirstName() == null ? "" : response.getFirstName()) + " "
                + (response.getLastName() == null ? "" : response.getLastName())).trim();
        return fullName.isBlank() ? null : fullName;
    }

    private UserResponse toVisibleUserResponse(User user, UUID viewerId) {
        UserResponse response = userMapper.toDto(user);
        if (user.getId().equals(viewerId)) {
            return response;
        }
        boolean imagePublic = userSettingsRepository.findByUser(user)
                .map(settings -> Boolean.TRUE.equals(settings.getProfilePicturePublic()))
                .orElse(false);
        return new UserResponse(
                response.id(),
                response.username(),
                response.fullName(),
                imagePublic ? response.profileImageUrl() : null,
                response.verified()
        );
    }

    @Override
    public void deleteUser(UUID userId) {

        User user = getUser(userId);

        // -> Soft delete.
        user.setDeleted(Boolean.TRUE);

        userRepository.save(user);
    }

    // -> Fetch user or throw exception.
    private User getUser(UUID userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }
}