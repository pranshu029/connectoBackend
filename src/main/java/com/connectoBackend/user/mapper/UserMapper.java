package com.connectoBackend.user.mapper;

import com.connectoBackend.common.mapper.BaseMapper;
import com.connectoBackend.user.dto.request.CreateUserRequest;
import com.connectoBackend.user.dto.request.UpdateUserRequest;
import com.connectoBackend.user.dto.response.UserProfileResponse;
import com.connectoBackend.user.dto.response.UserResponse;
import com.connectoBackend.user.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper for User entity.
 */
@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<UserResponse, User> {

    // -> Convert create request to entity.
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "bio", source = "bio")
    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    @Mapping(target = "gender", source = "gender")
    User toEntity(CreateUserRequest request);

    // -> Convert entity to response DTO.
    @Mapping(target = "fullName", expression = "java(buildFullName(entity))")
    @Mapping(target = "profileImageUrl", source = "profilePictureUrl")
    @Mapping(target = "verified", source = "emailVerified")
    @Override
    UserResponse toDto(User entity);

    // -> Convert entity to profile response DTO.
    @Mapping(target = "fullName", expression = "java(buildFullName(entity))")
    @Mapping(target = "profilePictureUrl", source = "profilePictureUrl")
    @Mapping(target = "coverPictureUrl", ignore = true)
    @Mapping(target = "emailVerified", source = "emailVerified")
    UserProfileResponse toProfileResponse(User entity);

    // -> Update entity from update request.
    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            ignoreByDefault = true
    )
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "profilePictureUrl", source = "profilePictureUrl")
    @Mapping(target = "bio", source = "bio")
    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    @Mapping(target = "gender", source = "gender")
    void updateEntity(UpdateUserRequest request, @MappingTarget User entity);

    // -> Required by BaseMapper.
    @Override
    default void updateEntity(UserResponse dto, @MappingTarget User entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.username() != null) {
            entity.setUsername(dto.username());
        }

        if (dto.profileImageUrl() != null) {
            entity.setProfilePictureUrl(dto.profileImageUrl());
        }

        if (dto.verified() != null) {
            entity.setEmailVerified(dto.verified());
        }

        // Try to split fullName into first/last name when present.
        if (dto.fullName() != null) {
            String full = dto.fullName().trim();
            if (!full.isEmpty()) {
                String[] parts = full.split("\\s+", 2);
                entity.setFirstName(parts[0]);
                if (parts.length > 1) {
                    entity.setLastName(parts[1]);
                }
            }
        }
    }

    default String buildFullName(User entity) {
        if (entity == null) {
            return null;
        }

        return (entity.getFirstName() == null ? "" : entity.getFirstName())
                + " "
                + (entity.getLastName() == null ? "" : entity.getLastName()).trim();
    }
}