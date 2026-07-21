package com.connectoBackend.chat.service.impl;

import com.connectoBackend.chat.dto.request.InviteMemberRequest;
import com.connectoBackend.chat.dto.response.GroupInvitationResponse;
import com.connectoBackend.chat.entity.Conversation;
import com.connectoBackend.chat.entity.GroupInvitation;
import com.connectoBackend.chat.enums.InvitationStatus;
import com.connectoBackend.chat.repository.ConversationRepository;
import com.connectoBackend.chat.repository.GroupInvitationRepository;
import com.connectoBackend.chat.service.GroupInvitationService;
import com.connectoBackend.common.exception.ConflictException;
import com.connectoBackend.common.exception.ResourceNotFoundException;
import com.connectoBackend.user.entity.User;
import com.connectoBackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupInvitationServiceImpl implements GroupInvitationService {

	private static final long INVITATION_EXPIRY_DAYS = 7;

	private final GroupInvitationRepository groupInvitationRepository;
	private final ConversationRepository conversationRepository;
	private final UserRepository userRepository;

	@Override
	public GroupInvitationResponse inviteMember(UUID conversationId, UUID invitedByUserId, InviteMemberRequest request) {
		Conversation conversation = getConversation(conversationId);
		User invitedBy = getUser(invitedByUserId);
		User invitedUser = getUser(request.userId());

		if (groupInvitationRepository.findAllByConversation(conversation).stream().anyMatch(inv -> inv.getInvitedUser().equals(invitedUser) && inv.getStatus() == InvitationStatus.PENDING)) {
			throw new ConflictException("Invitation already exists.");
		}

		GroupInvitation invitation = GroupInvitation.builder()
				.conversation(conversation)
				.invitedBy(invitedBy)
				.invitedUser(invitedUser)
				.status(InvitationStatus.PENDING)
				.expiresAt(LocalDateTime.now().plusDays(INVITATION_EXPIRY_DAYS))
				.build();
		invitation = groupInvitationRepository.save(invitation);
		return toResponse(invitation);
	}

	@Override
	@Transactional(readOnly = true)
	public List<GroupInvitationResponse> getInvitationsForUser(UUID invitedUserId) {
		User invitedUser = getUser(invitedUserId);
		return groupInvitationRepository.findAllByInvitedUser(invitedUser)
				.stream()
				.map(this::toResponse)
				.toList();
	}

	@Override
	public void respondToInvitation(UUID invitationId, InvitationStatus status) {
		GroupInvitation invitation = getInvitation(invitationId);
		invitation.setStatus(status);
		invitation.setRespondedAt(LocalDateTime.now());
		groupInvitationRepository.save(invitation);
	}

	@Override
	public void cancelInvitation(UUID invitationId) {
		respondToInvitation(invitationId, InvitationStatus.CANCELLED);
	}

	private GroupInvitation getInvitation(UUID invitationId) {
		return groupInvitationRepository.findById(invitationId)
				.orElseThrow(() -> new ResourceNotFoundException("Invitation not found."));
	}

	private Conversation getConversation(UUID conversationId) {
		return conversationRepository.findById(conversationId)
				.orElseThrow(() -> new ResourceNotFoundException("Conversation not found."));
	}

	private User getUser(UUID userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found."));
	}

	private GroupInvitationResponse toResponse(GroupInvitation invitation) {
		return new GroupInvitationResponse(
				invitation.getId(),
				invitation.getInvitedBy().getId(),
				invitation.getInvitedBy().getUsername(),
				invitation.getInvitedUser().getId(),
				invitation.getInvitedUser().getUsername(),
				invitation.getStatus(),
				invitation.getExpiresAt()
		);
	}
}