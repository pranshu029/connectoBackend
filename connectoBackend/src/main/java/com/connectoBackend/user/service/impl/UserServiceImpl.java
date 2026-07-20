package com.connectoBackend.user.service.impl;

import com.connectoBackend.common.exception.ConflictException;
import com.connectoBackend.common.exception.ResourceNotFoundException;
import com.connectoBackend.user.dto.request.CreateUserRequest;
import com.connectoBackend.user.dto.request.UpdateUserRequest;
import com.connectoBackend.user.dto.response.UserProfileResponse;
import com.connectoBackend.user.dto.response.UserResponse;
import com.connectoBackend.user.entity.User;
import com.connectoBackend.user.enums.AccountStatus;
import com.connectoBackend.user.enums.UserRole;
import com.connectoBackend.user.mapper.UserMapper;
import com.connectoBackend.user.repository.UserRepository;
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

    @Override
    public UserResponse createUser(CreateUserRequest request) {

        // -> Check email uniqueness.
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists.");
        }

        // -> Check username uniqueness.
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already exists.");
        }

        // -> Map request to entity.
        User user = userMapper.toEntity(request);

        // -> Initialize default values.
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER);
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setEmailVerified(Boolean.FALSE);

        // -> Persist user.
        user = userRepository.save(user);

        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID userId) {

        User user = getUser(userId);

        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(UUID userId) {

        User user = getUser(userId);

        return userMapper.toProfileResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {

        return userRepository.findAll(pageable)
                .map(userMapper::toDto);
    }

    @Override
    public UserProfileResponse updateUser(UUID userId, UpdateUserRequest request) {

        User user = getUser(userId);

        // -> Update entity.
        userMapper.updateEntity(request, user);

        user = userRepository.save(user);

        return userMapper.toProfileResponse(user);
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