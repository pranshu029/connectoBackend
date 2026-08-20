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
import com.connectoBackend.common.exception.ForbiddenException;
import com.connectoBackend.common.exception.ResourceNotFoundException;
import com.connectoBackend.user.entity.User;
import com.connectoBackend.user.repository.UserRepository;
import com.connectoBackend.chat.repository.ConversationMemberRepository;
import com.connectoBackend.chat.entity.ConversationMember;
import com.connectoBackend.chat.enums.ConversationMemberRole;
import com.connectoBackend.chat.enums.MemberStatus;
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
	private final ConversationMemberRepository conversationMemberRepository;

	@Override
	public GroupInvitationResponse inviteMember(UUID conversationId, UUID invitedByUserId, InviteMemberRequest request) {
		Conversation conversation = getConversation(conversationId);
		User invitedBy = getUser(invitedByUserId);
		User invitedUser = getUser(request.userId());
		if (conversation.getType() != com.connectoBackend.chat.enums.ConversationType.GROUP || !conversation.isActive()) {
			throw new ForbiddenException("Invitations are supported only for active group conversations.");
		}
		requireAdministrator(conversation, invitedBy);

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
	public void respondToInvitation(UUID userId, UUID invitationId, InvitationStatus status) {
		GroupInvitation invitation = getInvitation(invitationId);
		if (!invitation.getInvitedUser().getId().equals(userId)) {
			throw new ForbiddenException("Only the invited user can respond to this invitation.");
		}
		if (invitation.getStatus() != InvitationStatus.PENDING || invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
			throw new ConflictException("This invitation is no longer actionable.");
		}
		if (status != InvitationStatus.ACCEPTED && status != InvitationStatus.REJECTED) {
			throw new ForbiddenException("Only accepted or rejected are valid responses.");
		}
		invitation.setStatus(status);
		invitation.setRespondedAt(LocalDateTime.now());
		groupInvitationRepository.save(invitation);
		if (status == InvitationStatus.ACCEPTED) {
			ConversationMember member = conversationMemberRepository.findByConversationAndUser(invitation.getConversation(), invitation.getInvitedUser())
					.orElseGet(ConversationMember::new);
			member.setConversation(invitation.getConversation());
			member.setUser(invitation.getInvitedUser());
			member.setRole(ConversationMemberRole.MEMBER);
			member.setStatus(MemberStatus.ACTIVE);
			member.setActive(true);
			member.setJoinedAt(LocalDateTime.now());
			member.setLeftAt(null);
			conversationMemberRepository.save(member);
		}
	}

	@Override
	public void cancelInvitation(UUID userId, UUID invitationId) {
		GroupInvitation invitation = getInvitation(invitationId);
		requireAdministrator(invitation.getConversation(), getUser(userId));
		if (invitation.getStatus() != InvitationStatus.PENDING) {
			throw new ConflictException("This invitation is no longer actionable.");
		}
		invitation.setStatus(InvitationStatus.CANCELLED);
		invitation.setRespondedAt(LocalDateTime.now());
		groupInvitationRepository.save(invitation);
	}

	private void requireAdministrator(Conversation conversation, User user) {
		ConversationMember member = conversationMemberRepository.findByConversationAndUser(conversation, user)
				.orElseThrow(() -> new ForbiddenException("You must be a conversation administrator."));
		if (!member.isActive() || member.getStatus() != MemberStatus.ACTIVE
				|| (member.getRole() != ConversationMemberRole.OWNER && member.getRole() != ConversationMemberRole.ADMIN)) {
			throw new ForbiddenException("Only conversation owners and admins can invite members.");
		}
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