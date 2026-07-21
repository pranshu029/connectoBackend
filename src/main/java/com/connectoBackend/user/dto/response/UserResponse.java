package com.connectoBackend.user.dto.response;

import java.util.UUID;

/**
 * Lightweight response DTO for a user.
 */
public record UserResponse(

		UUID id,

		String username,

		String fullName,

		String profileImageUrl,

		Boolean verified

) {
}
