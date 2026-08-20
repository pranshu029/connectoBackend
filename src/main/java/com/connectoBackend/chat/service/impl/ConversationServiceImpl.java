package com.connectoBackend.chat.service.impl;

import com.connectoBackend.chat.dto.request.CreateConversationRequest;
import com.connectoBackend.chat.dto.request.UpdateConversationRequest;
import com.connectoBackend.chat.dto.request.UpdateMemberRoleRequest;
import com.connectoBackend.chat.dto.response.ConversationMemberResponse;
import com.connectoBackend.chat.dto.response.ConversationResponse;
import com.connectoBackend.chat.dto.response.ConversationSummaryResponse;
import com.connectoBackend.chat.enums.ConversationRole;
import com.connectoBackend.chat.entity.Conversation;
import com.connectoBackend.chat.entity.ConversationMember;
import com.connectoBackend.chat.entity.Message;
import com.connectoBackend.chat.entity.ConversationReadState;
import com.connectoBackend.chat.enums.ConversationMemberRole;
import com.connectoBackend.chat.enums.ConversationType;
import com.connectoBackend.chat.enums.MemberStatus;
import com.connectoBackend.chat.repository.ConversationMemberRepository;
import com.connectoBackend.chat.repository.ConversationRepository;
import com.connectoBackend.chat.repository.MessageRepository;
import com.connectoBackend.chat.service.ConversationService;
import com.connectoBackend.common.exception.ForbiddenException;
import com.connectoBackend.common.exception.ResourceNotFoundException;
import com.connectoBackend.user.enums.ConnectionStatus;
import com.connectoBackend.user.repository.ConnectionRepository;
import com.connectoBackend.user.entity.User;
import com.connectoBackend.user.repository.UserRepository;
import com.connectoBackend.user.repository.UserSettingsRepository;
import com.connectoBackend.user.repository.BlockedUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ConversationServiceImpl implements ConversationService {

	private final ConversationRepository conversationRepository;
	private final ConversationMemberRepository conversationMemberRepository;
	private final MessageRepository messageRepository;
	private final UserRepository userRepository;
	private final com.connectoBackend.chat.repository.ConversationReadStateRepository conversationReadStateRepository;
	private final ConnectionRepository connectionRepository;
	private final UserSettingsRepository userSettingsRepository;
	private final BlockedUserRepository blockedUserRepository;

	@Override
	public ConversationResponse createConversation(UUID creatorUserId, CreateConversationRequest request) {
		User creator = getUser(creatorUserId);
		List<User> participants = request.participantIds().stream()
				.filter(participantId -> !participantId.equals(creatorUserId))
				.map(this::getUser)
				.toList();

		if (request.conversationType() == ConversationType.DIRECT && participants.size() != 1) {
			throw new ForbiddenException("A direct conversation requires exactly one connected participant.");
		}

		for (User participant : participants) {
			if (blockedUserRepository.existsByUserAndBlockedUser(creator, participant)
					|| blockedUserRepository.existsByUserAndBlockedUser(participant, creator)) {
				throw new ForbiddenException("This user interaction is blocked.");
			}
			if (!connectionRepository.existsConnectionBetweenUsers(creator, participant, ConnectionStatus.CONNECTED)) {
				throw new ForbiddenException("You can message users only after the connection is accepted.");
			}
		}

		if (request.conversationType() == ConversationType.DIRECT) {
			User participant = participants.get(0);
			lockUsersForDirectConversation(creatorUserId, participant.getId());
			List<Conversation> existing = conversationRepository.findActiveDirectConversations(
					ConversationType.DIRECT,
					creatorUserId,
					participant.getId()
			);
			if (!existing.isEmpty()) {
				return toConversationResponse(resolveActiveDirectConversation(existing));
			}
		}

		Conversation conversation = Conversation.builder()
				.type(request.conversationType())
				.name(request.name())
				.description(request.description())
				.active(true)
				.createdByUser(creator)
				.build();
		conversation = conversationRepository.save(conversation);

		addMemberInternal(conversation, creator, ConversationMemberRole.OWNER, MemberStatus.ACTIVE);
		for (User participant : participants) {
			addMemberInternal(conversation, participant, ConversationMemberRole.MEMBER, MemberStatus.ACTIVE);
		}

		return toConversationResponse(conversation);
	}

	private void lockUsersForDirectConversation(UUID firstUserId, UUID secondUserId) {
		List<UUID> userIds = List.of(firstUserId, secondUserId).stream()
				.sorted()
				.toList();
		for (UUID userId : userIds) {
			userRepository.findByIdForUpdate(userId)
					.orElseThrow(() -> new ResourceNotFoundException("User not found."));
		}
	}

	private Conversation resolveActiveDirectConversation(List<Conversation> conversations) {
		Conversation survivor = conversations.stream()
				.max(Comparator.comparingLong(messageRepository::countByConversation)
						.thenComparing(Conversation::getCreatedAt, Comparator.nullsFirst(Comparator.naturalOrder())))
				.orElseThrow();

		if (conversations.size() == 1) {
			return survivor;
		}

		log.warn("Found {} active direct conversations for the same participant pair; preserving conversation {} and deactivating duplicates.",
				conversations.size(), survivor.getId());

		List<Message> messages = conversations.stream()
				.flatMap(conversation -> messageRepository.findAllByConversationOrderByCreatedAtAscIdAsc(conversation).stream())
				.sorted(Comparator.comparing(Message::getCreatedAt, Comparator.nullsFirst(Comparator.naturalOrder()))
						.thenComparing(Message::getId, Comparator.nullsFirst(Comparator.naturalOrder())))
				.toList();
		for (int index = 0; index < messages.size(); index++) {
			Message message = messages.get(index);
			message.setConversation(survivor);
			message.setSequenceNumber((long) index + 1);
		}
		messageRepository.saveAll(messages);

		mergeMembers(conversations, survivor);
		mergeReadStates(conversations, survivor);
		for (Conversation conversation : conversations) {
			if (!conversation.equals(survivor)) {
				conversation.setActive(false);
				conversationRepository.save(conversation);
			}
		}
		return survivor;
	}

	private void mergeMembers(List<Conversation> conversations, Conversation survivor) {
		for (Conversation conversation : conversations) {
			if (conversation.equals(survivor)) {
				continue;
			}
			for (ConversationMember duplicateMember : conversationMemberRepository.findAllByConversation(conversation)) {
				ConversationMember survivorMember = conversationMemberRepository
						.findByConversationAndUser(survivor, duplicateMember.getUser())
						.orElse(null);
				if (survivorMember == null) {
					addMemberInternal(survivor, duplicateMember.getUser(), duplicateMember.getRole(), duplicateMember.getStatus());
				} else if (duplicateMember.isActive() && !survivorMember.isActive()) {
					survivorMember.setActive(true);
					survivorMember.setStatus(MemberStatus.ACTIVE);
					survivorMember.setLeftAt(null);
					conversationMemberRepository.save(survivorMember);
				}
			}
		}
	}

	private void mergeReadStates(List<Conversation> conversations, Conversation survivor) {
		for (Conversation conversation : conversations) {
			if (conversation.equals(survivor)) {
				continue;
			}
			for (ConversationReadState duplicateState : conversationReadStateRepository.findAllByConversation(conversation)) {
				if (conversationReadStateRepository.findByConversationAndUser(survivor, duplicateState.getUser()).isEmpty()) {
					duplicateState.setConversation(survivor);
					conversationReadStateRepository.save(duplicateState);
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ConversationResponse getConversation(UUID conversationId, UUID viewerUserId) {
		Conversation conversation = getConversationEntity(conversationId);
		requireActiveMember(conversation, viewerUserId);
		return toConversationResponse(conversation);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ConversationSummaryResponse> getConversationsForUser(UUID userId, Pageable pageable) {
		User user = getUser(userId);
		List<ConversationSummaryResponse> summaries = conversationMemberRepository.findAllActiveByUser(user)
			.stream()
			.map(ConversationMember::getConversation)
			.map(conversation -> toSummaryResponse(conversation, user))
			.toList();
		int start = Math.min((int) pageable.getOffset(), summaries.size());
		int end = Math.min(start + pageable.getPageSize(), summaries.size());
		return new PageImpl<>(summaries.subList(start, end), pageable, summaries.size());
	}

	@Override
	public ConversationResponse updateConversation(UUID conversationId, UUID viewerUserId, UpdateConversationRequest request) {
		Conversation conversation = getConversationEntity(conversationId);
		requireActiveMember(conversation, viewerUserId);
		if (request.name() != null) {
			conversation.setName(request.name());
		}
		if (request.description() != null) {
			conversation.setDescription(request.description());
		}
		if (request.imageUrl() != null) {
			conversation.setImageUrl(request.imageUrl());
		}
		if (request.active() != null) {
			conversation.setActive(request.active());
		}
		return toConversationResponse(conversationRepository.save(conversation));
	}

	@Override
	public void deleteConversation(UUID conversationId, UUID viewerUserId) {
		Conversation conversation = getConversationEntity(conversationId);
		requireActiveMember(conversation, viewerUserId);
		conversation.setActive(false);
		conversationRepository.save(conversation);
	}

	@Override
	public ConversationMemberResponse addMember(UUID conversationId, UUID actorUserId, UUID userId, ConversationMemberRole role) {
		Conversation conversation = getConversationEntity(conversationId);
		requireGroupAdministrator(conversation, actorUserId);
		if (conversation.getType() != ConversationType.GROUP) {
			throw new ForbiddenException("Members can be added only to group conversations.");
		}
		User user = getUser(userId);
		ConversationMember member = addMemberInternal(conversation, user, role, MemberStatus.ACTIVE);
		return toMemberResponse(member);
	}

	@Override
	public ConversationMemberResponse updateMemberRole(UUID conversationId, UUID actorUserId, UUID userId, UpdateMemberRoleRequest request) {
		Conversation conversation = getConversationEntity(conversationId);
		requireGroupAdministrator(conversation, actorUserId);
		ConversationMember member = getMember(conversationId, userId);
		if (member.getRole() == ConversationMemberRole.OWNER || request.role() == ConversationMemberRole.OWNER) {
			throw new ForbiddenException("Owner role cannot be changed through this operation.");
		}
		member.setRole(request.role());
		return toMemberResponse(conversationMemberRepository.save(member));
	}

	@Override
	public void removeMember(UUID conversationId, UUID actorUserId, UUID userId) {
		ConversationMember member = getMember(conversationId, userId);
		requireGroupAdministrator(member.getConversation(), actorUserId);
		if (member.getRole() == ConversationMemberRole.OWNER) {
			throw new ForbiddenException("The conversation owner cannot be removed.");
		}
		member.setActive(false);
		member.setStatus(MemberStatus.REMOVED);
		member.setLeftAt(LocalDateTime.now());
		conversationMemberRepository.save(member);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ConversationMemberResponse> getMembers(UUID conversationId, UUID viewerUserId) {
		Conversation conversation = getConversationEntity(conversationId);
		requireActiveMember(conversation, viewerUserId);
		return conversationMemberRepository.findAllByConversation(conversation)
				.stream()
				.map(this::toMemberResponse)
				.toList();
	}

	private ConversationMember addMemberInternal(Conversation conversation, User user, ConversationMemberRole role, MemberStatus status) {
		ConversationMember member = conversationMemberRepository.findByConversationAndUser(conversation, user)
				.orElseGet(ConversationMember::new);
		member.setConversation(conversation);
		member.setUser(user);
		member.setRole(role);
		member.setStatus(status);
		member.setActive(true);
		member.setJoinedAt(LocalDateTime.now());
		member.setLeftAt(null);
		return conversationMemberRepository.save(member);
	}

	private Conversation getConversationEntity(UUID conversationId) {
		return conversationRepository.findById(conversationId)
				.orElseThrow(() -> new ResourceNotFoundException("Conversation not found."));
	}

	private ConversationMember getMember(UUID conversationId, UUID userId) {
		Conversation conversation = getConversationEntity(conversationId);
		User user = getUser(userId);
		return conversationMemberRepository.findByConversationAndUser(conversation, user)
				.orElseThrow(() -> new ResourceNotFoundException("Conversation member not found."));
	}

	private void requireActiveMember(Conversation conversation, UUID userId) {
		User user = getUser(userId);
		ConversationMember member = conversationMemberRepository.findByConversationAndUser(conversation, user)
				.orElseThrow(() -> new ForbiddenException("You are not a member of this conversation."));
		if (!member.isActive() || member.getStatus() != MemberStatus.ACTIVE) {
			throw new ForbiddenException("You are not an active member of this conversation.");
		}
	}

	private void requireGroupAdministrator(Conversation conversation, UUID userId) {
		if (conversation.getType() != ConversationType.GROUP || !conversation.isActive()) {
			throw new ForbiddenException("Only active group conversations support this operation.");
		}
		ConversationMember member = getMember(conversation.getId(), userId);
		if (!member.isActive() || member.getStatus() != MemberStatus.ACTIVE
				|| (member.getRole() != ConversationMemberRole.OWNER && member.getRole() != ConversationMemberRole.ADMIN)) {
			throw new ForbiddenException("Only conversation owners and admins can manage members.");
		}
	}

	private User getUser(UUID userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found."));
	}

	private ConversationResponse toConversationResponse(Conversation conversation) {
		return new ConversationResponse(
				conversation.getId(),
				conversation.getType(),
				conversation.getName(),
				conversation.getDescription(),
				conversation.getImageUrl(),
				conversation.isActive(),
				conversation.getCreatedByUser() != null ? conversation.getCreatedByUser().getId() : null,
				conversationMemberRepository.findAllByConversation(conversation).stream()
						.map(this::toMemberResponse)
						.toList()
		);
	}

	private ConversationSummaryResponse toSummaryResponse(Conversation conversation, User user) {
		Message latestMessage = messageRepository.findByConversationAndDeletedFalseOrderBySequenceNumberDesc(
				conversation,
				PageRequest.of(0, 1)
		).stream().findFirst().orElse(null);

		long unread = 0L;
		var readStateOpt = conversationReadStateRepository.findByConversationAndUser(conversation, user);
		if (readStateOpt.isPresent()) {
			var readState = readStateOpt.get();
			if (readState.getLastReadMessage() != null) {
				Long lastSeq = readState.getLastReadMessage().getSequenceNumber();
				unread = messageRepository.countByConversationAndSequenceNumberGreaterThan(conversation, lastSeq);
			} else {
				unread = messageRepository.countByConversation(conversation);
			}
		} else {
			// No read state -> all messages are unread
			unread = messageRepository.countByConversation(conversation);
		}

		// Determine other participant for DIRECT conversations
		UUID otherParticipantId = null;
		String otherParticipantName = null;
		String otherParticipantProfileImage = null;

		if (conversation.getType() == ConversationType.DIRECT) {
			// For DIRECT conversations, find the other participant
			User otherParticipant = conversation.getMembers().stream()
					.map(ConversationMember::getUser)
					.filter(u -> !u.getId().equals(user.getId()))
					.findFirst()
					.orElse(null);

			if (otherParticipant != null) {
				otherParticipantId = otherParticipant.getId();
				otherParticipantName = displayName(otherParticipant);
				otherParticipantProfileImage = userSettingsRepository.findByUser(otherParticipant)
						.filter(settings -> Boolean.TRUE.equals(settings.getProfilePicturePublic()))
						.map(settings -> otherParticipant.getProfilePictureUrl())
						.orElse(null);
			}
		}

		return new ConversationSummaryResponse(
				conversation.getId(),
				conversation.getName(),
				conversation.getImageUrl(),
				latestMessage != null ? latestMessage.getContent() : null,
				latestMessage != null ? latestMessage.getCreatedAt() : null,
				unread,
				otherParticipantId,
				otherParticipantName,
				otherParticipantProfileImage
		);
	}

	private ConversationMemberResponse toMemberResponse(ConversationMember member) {
		User user = member.getUser();
		return new ConversationMemberResponse(
				user.getId(),
				user.getUsername(),
				displayName(user),
				userSettingsRepository.findByUser(user)
						.filter(settings -> Boolean.TRUE.equals(settings.getProfilePicturePublic()))
						.map(settings -> user.getProfilePictureUrl())
						.orElse(null),
				ConversationRole.valueOf(member.getRole().name()),
				member.getStatus(),
				member.isMuted(),
				member.isPinned()
		);
	}

	private String displayName(User user) {
		var settings = userSettingsRepository.findByUser(user).orElse(null);
		String firstName = settings != null && Boolean.TRUE.equals(settings.getFirstNamePublic())
				? user.getFirstName() : null;
		String lastName = settings != null && Boolean.TRUE.equals(settings.getLastNamePublic())
				? user.getLastName() : null;
		String fullName = ((firstName == null ? "" : firstName) + " "
				+ (lastName == null ? "" : lastName)).trim();

		if (!fullName.isBlank()) {
			return fullName;
		}

		if (user.getUsername() != null && !user.getUsername().isBlank()) {
			return user.getUsername();
		}

		return "Unknown";
	}
}