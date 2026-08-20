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
import com.connectoBackend.chat.enums.ConversationMemberRole;
import com.connectoBackend.chat.enums.ConversationType;
import com.connectoBackend.chat.enums.MemberStatus;
import com.connectoBackend.chat.repository.ConversationMemberRepository;
import com.connectoBackend.chat.repository.ConversationRepository;
import com.connectoBackend.chat.repository.MessageRepository;
import com.connectoBackend.chat.service.ConversationService;
import com.connectoBackend.common.exception.ResourceNotFoundException;
import com.connectoBackend.user.entity.User;
import com.connectoBackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ConversationServiceImpl implements ConversationService {

	private final ConversationRepository conversationRepository;
	private final ConversationMemberRepository conversationMemberRepository;
	private final MessageRepository messageRepository;
	private final UserRepository userRepository;
	private final com.connectoBackend.chat.repository.ConversationReadStateRepository conversationReadStateRepository;

	@Override
	public ConversationResponse createConversation(UUID creatorUserId, CreateConversationRequest request) {
		User creator = getUser(creatorUserId);
		Conversation conversation = Conversation.builder()
				.type(request.conversationType())
				.name(request.name())
				.description(request.description())
				.active(true)
				.createdByUser(creator)
				.build();
		conversation = conversationRepository.save(conversation);

		addMemberInternal(conversation, creator, ConversationMemberRole.OWNER, MemberStatus.ACTIVE);
		for (UUID participantId : request.participantIds()) {
			if (!participantId.equals(creatorUserId)) {
				addMemberInternal(conversation, getUser(participantId), ConversationMemberRole.MEMBER, MemberStatus.ACTIVE);
			}
		}

		return toConversationResponse(conversation);
	}

	@Override
	@Transactional(readOnly = true)
	public ConversationResponse getConversation(UUID conversationId) {
		return toConversationResponse(getConversationEntity(conversationId));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ConversationSummaryResponse> getConversationsForUser(UUID userId, Pageable pageable) {
		User user = getUser(userId);
		List<ConversationSummaryResponse> summaries = conversationMemberRepository.findAllByUser(user)
			.stream()
			.map(ConversationMember::getConversation)
			.map(conversation -> toSummaryResponse(conversation, user))
			.toList();
		int start = Math.min((int) pageable.getOffset(), summaries.size());
		int end = Math.min(start + pageable.getPageSize(), summaries.size());
		return new PageImpl<>(summaries.subList(start, end), pageable, summaries.size());
	}

	@Override
	public ConversationResponse updateConversation(UUID conversationId, UpdateConversationRequest request) {
		Conversation conversation = getConversationEntity(conversationId);
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
	public void deleteConversation(UUID conversationId) {
		Conversation conversation = getConversationEntity(conversationId);
		conversation.setActive(false);
		conversationRepository.save(conversation);
	}

	@Override
	public ConversationMemberResponse addMember(UUID conversationId, UUID userId, ConversationMemberRole role) {
		Conversation conversation = getConversationEntity(conversationId);
		User user = getUser(userId);
		ConversationMember member = addMemberInternal(conversation, user, role, MemberStatus.ACTIVE);
		return toMemberResponse(member);
	}

	@Override
	public ConversationMemberResponse updateMemberRole(UUID conversationId, UUID userId, UpdateMemberRoleRequest request) {
		ConversationMember member = getMember(conversationId, userId);
		member.setRole(request.role());
		return toMemberResponse(conversationMemberRepository.save(member));
	}

	@Override
	public void removeMember(UUID conversationId, UUID userId) {
		ConversationMember member = getMember(conversationId, userId);
		member.setActive(false);
		member.setStatus(MemberStatus.REMOVED);
		member.setLeftAt(LocalDateTime.now());
		conversationMemberRepository.save(member);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ConversationMemberResponse> getMembers(UUID conversationId) {
		Conversation conversation = getConversationEntity(conversationId);
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
				getMembers(conversation.getId())
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
				otherParticipantName = (otherParticipant.getFirstName() == null ? "" : otherParticipant.getFirstName()) + 
										" " + (otherParticipant.getLastName() == null ? "" : otherParticipant.getLastName()).trim();
				otherParticipantProfileImage = otherParticipant.getProfilePictureUrl();
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
				(user.getFirstName() == null ? "" : user.getFirstName()) + " " + (user.getLastName() == null ? "" : user.getLastName()).trim(),
				user.getProfilePictureUrl(),
				ConversationRole.valueOf(member.getRole().name()),
				member.getStatus(),
				member.isMuted(),
				member.isPinned()
		);
	}
}