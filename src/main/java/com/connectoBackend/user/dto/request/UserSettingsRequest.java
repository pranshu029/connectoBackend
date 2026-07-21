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
}