package com.connectoBackend.user.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO for updating user settings.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSettingsRequest {

	@NotNull
	private Boolean notificationsEnabled;

	@NotNull
	private Boolean readReceiptsEnabled;

	@NotNull
	private Boolean onlineStatusVisible;

	private Boolean publicProfileDetails;

	private Boolean firstNamePublic;
	private Boolean lastNamePublic;
	private Boolean emailPublic;
	private Boolean phoneNumberPublic;
	private Boolean dateOfBirthPublic;
	private Boolean genderPublic;
	private Boolean profilePicturePublic;
}