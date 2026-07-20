package com.connectoBackend.user.dto.response;

import java.io.Serializable;

/**
 * Lightweight user representation used across modules.
 * <p>
 * This DTO is intended for embedding in other response DTOs such as
 * chat, notifications, friends, groups, and future modules.
 * </p>
 *
 * @param id               unique identifier of the user
 * @param username         username of the user
 * @param fullName         full name of the user
 * @param profileImageUrl  profile image URL of the user
 * @param verified         indicates whether the user is verified
 */
public record UserSummaryResponse(

        Long id,

        String username,

        String fullName,

        String profileImageUrl,

        boolean verified

) implements Serializable {
}