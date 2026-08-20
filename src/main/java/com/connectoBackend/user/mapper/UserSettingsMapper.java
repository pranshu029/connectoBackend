package com.connectoBackend.user.mapper;

import com.connectoBackend.user.dto.request.UserSettingsRequest;
import com.connectoBackend.user.dto.response.UserSettingsResponse;
import com.connectoBackend.user.entity.UserSettings;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper for user settings.
 */
@Mapper(componentModel = "spring")
public interface UserSettingsMapper {

	@Mapping(target = "userId", source = "user.id")
	UserSettingsResponse toResponse(UserSettings entity);

	@Mapping(target = "user", ignore = true)
	@BeanMapping(
			nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
			ignoreByDefault = true
	)
	@Mapping(target = "notificationsEnabled", source = "notificationsEnabled")
	@Mapping(target = "readReceiptsEnabled", source = "readReceiptsEnabled")
	@Mapping(target = "onlineStatusVisible", source = "onlineStatusVisible")
	@Mapping(target = "publicProfileDetails", source = "publicProfileDetails")
	@Mapping(target = "firstNamePublic", source = "firstNamePublic")
	@Mapping(target = "lastNamePublic", source = "lastNamePublic")
	@Mapping(target = "emailPublic", source = "emailPublic")
	@Mapping(target = "phoneNumberPublic", source = "phoneNumberPublic")
	@Mapping(target = "dateOfBirthPublic", source = "dateOfBirthPublic")
	@Mapping(target = "genderPublic", source = "genderPublic")
	@Mapping(target = "profilePicturePublic", source = "profilePicturePublic")
	void updateEntity(UserSettingsRequest request, @MappingTarget UserSettings entity);
}