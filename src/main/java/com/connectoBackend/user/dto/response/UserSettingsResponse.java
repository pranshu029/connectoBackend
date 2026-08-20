package com.connectoBackend.user.dto.response;

import java.util.UUID;

/**
 * Response DTO for user settings.
 */
public record UserSettingsResponse(

		UUID id,

		UUID userId,

		Boolean notificationsEnabled,

		Boolean readReceiptsEnabled,

		Boolean onlineStatusVisible

		, Boolean publicProfileDetails,

		Boolean firstNamePublic,
		Boolean lastNamePublic,
		Boolean emailPublic,
		Boolean phoneNumberPublic,
		Boolean dateOfBirthPublic,
		Boolean genderPublic,
		Boolean profilePicturePublic

) {
}