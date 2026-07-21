package com.connectoBackend.chat.dto.request;

import com.connectoBackend.chat.enums.ConversationMemberRole;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for updating a member role.
 */
public record UpdateMemberRoleRequest(

		@NotNull(message = "Member role is required.")
		ConversationMemberRole role

) {
}
