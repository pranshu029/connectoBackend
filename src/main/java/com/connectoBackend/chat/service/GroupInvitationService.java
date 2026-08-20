package com.connectoBackend.chat.service;

import com.connectoBackend.chat.dto.request.InviteMemberRequest;
import com.connectoBackend.chat.dto.response.GroupInvitationResponse;
import com.connectoBackend.chat.enums.InvitationStatus;

import java.util.List;
import java.util.UUID;

public interface GroupInvitationService {

	GroupInvitationResponse inviteMember(UUID conversationId, UUID invitedByUserId, InviteMemberRequest request);

	List<GroupInvitationResponse> getInvitationsForUser(UUID invitedUserId);

	void respondToInvitation(UUID userId, UUID invitationId, InvitationStatus status);

	void cancelInvitation(UUID userId, UUID invitationId);
}