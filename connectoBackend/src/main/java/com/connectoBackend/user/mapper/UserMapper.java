package com.connectoBackend.user.mapper;

import com.connectoBackend.common.mapper.BaseMapper;
import com.connectoBackend.user.dto.request.CreateUserRequest;
import com.connectoBackend.user.dto.request.UpdateUserRequest;
import com.connectoBackend.user.dto.response.UserProfileResponse;
import com.connectoBackend.user.dto.response.UserResponse;
import com.connectoBackend.user.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper for User entity.
 */
@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<UserResponse, User> {

    // -> Convert create request to entity.
    User toEntity(CreateUserRequest request);

    // -> Convert entity to response DTO.
    @Override
    UserResponse toDto(User entity);

    // -> Convert entity to profile response DTO.
    UserProfileResponse toProfileResponse(User entity);

    // -> Update entity from update request.
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UpdateUserRequest request, @MappingTarget User entity);

    // -> Required by BaseMapper.
    @Override
    default void updateEntity(UserResponse dto, @MappingTarget User entity) {
        throw new UnsupportedOperationException("Operation not supported.");
    }
}