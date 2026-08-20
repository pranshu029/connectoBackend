package com.connectoBackend.chat.service.impl;

import com.connectoBackend.chat.dto.request.EditMessageRequest;
import com.connectoBackend.chat.dto.request.SendMessageRequest;
import com.connectoBackend.chat.dto.response.AttachmentResponse;
import com.connectoBackend.chat.dto.response.MessageReactionResponse;
import com.connectoBackend.chat.dto.response.MessageResponse;
import com.connectoBackend.chat.entity.Conversation;
import com.connectoBackend.chat.entity.Message;
import com.connectoBackend.chat.enums.MessageStatus;
import com.connectoBackend.chat.enums.MessageType;
import com.connectoBackend.chat.repository.ConversationRepository;
import com.connectoBackend.chat.repository.MessageRepository;
import com.connectoBackend.chat.service.MessageService;
import com.connectoBackend.common.exception.ResourceNotFoundException;
import com.connectoBackend.common.exception.ForbiddenException;
import com.connectoBackend.chat.entity.ConversationMember;
import com.connectoBackend.chat.enums.MemberStatus;
import com.connectoBackend.chat.repository.ConversationMemberRepository;
import com.connectoBackend.user.enums.ConnectionStatus;
import com.connectoBackend.user.repository.ConnectionRepository;
import com.connectoBackend.user.entity.User;
import com.connectoBackend.user.repository.UserRepository;
import com.connectoBackend.user.repository.BlockedUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageServiceImpl implements MessageService {

	private final MessageRepository messageRepository;
	private final ConversationRepository conversationRepository;
	private final UserRepository userRepository;
	private final ConversationMemberRepository conversationMemberRepository;
	private final ConnectionRepository connectionRepository;
	private final BlockedUserRepository blockedUserRepository;

	@Override
	public MessageResponse sendMessage(UUID conversationId, UUID senderId, SendMessageRequest request) {
		Conversation conversation = getConversation(conversationId);
		if (!conversation.isActive()) {
			throw new ForbiddenException("This conversation is inactive.");
		}
		User sender = getUser(senderId);
		ConversationMember senderMember = conversationMemberRepository.findByConversationAndUser(conversation, sender)
				.orElseThrow(() -> new ForbiddenException("You are not a member of this conversation."));
		if (!senderMember.isActive() || senderMember.getStatus() != MemberStatus.ACTIVE) {
			throw new ForbiddenException("You are not an active member of this conversation.");
		}
		conversationMemberRepository.findAllByConversationAndStatus(conversation, MemberStatus.ACTIVE).stream()
				.map(ConversationMember::getUser)
				.filter(member -> !member.getId().equals(senderId))
				.forEach(member -> {
					if (blockedUserRepository.existsByUserAndBlockedUser(sender, member)
							|| blockedUserRepository.existsByUserAndBlockedUser(member, sender)) {
						throw new ForbiddenException("This user interaction is blocked.");
					}
					if (!connectionRepository.existsConnectionBetweenUsers(sender, member, ConnectionStatus.CONNECTED)) {
						throw new ForbiddenException("Messaging requires an accepted connection.");
					}
				});
		Message replyTo = request.replyToMessageId() == null ? null : getMessage(request.replyToMessageId());
		if (replyTo != null && !replyTo.getConversation().getId().equals(conversationId)) {
			throw new ForbiddenException("A reply must reference a message in the same conversation.");
		}
		Long nextSequence = messageRepository.findByConversationAndDeletedFalseOrderBySequenceNumberDesc(conversation, Pageable.ofSize(1))
				.stream().findFirst().map(Message::getSequenceNumber).orElse(0L) + 1;

		Message message = Message.builder()
				.conversation(conversation)
				.sender(sender)
				.type(MessageType.TEXT)
				.status(MessageStatus.SENT)
				.content(request.content())
				.replyToMessage(replyTo)
				.edited(false)
				.sequenceNumber(nextSequence)
				.build();
		message = messageRepository.save(message);
		return toResponse(message);
	}

	@Override
	public MessageResponse editMessage(UUID messageId, UUID editorUserId, EditMessageRequest request) {
		Message message = getMessage(messageId);
		if (!message.getConversation().isActive()) {
			throw new ForbiddenException("This conversation is inactive.");
		}
		if (!message.getSender().getId().equals(editorUserId)) {
			throw new ForbiddenException("You can edit only your own messages.");
		}
		message.setContent(request.content());
		message.setEdited(true);
		message.setEditedAt(LocalDateTime.now());
		return toResponse(messageRepository.save(message));
	}

	@Override
	public void deleteMessage(UUID messageId, UUID deleterUserId) {
		Message message = getMessage(messageId);
		if (!message.getConversation().isActive()) {
			throw new ForbiddenException("This conversation is inactive.");
		}
		if (!message.getSender().getId().equals(deleterUserId)) {
			throw new ForbiddenException("You can delete only your own messages.");
		}
		message.setDeleted(true);
		messageRepository.save(message);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<MessageResponse> getMessages(UUID conversationId, UUID viewerUserId, Pageable pageable) {
		Conversation conversation = getConversation(conversationId);
		ConversationMember member = conversationMemberRepository.findByConversationAndUser(conversation, getUser(viewerUserId))
				.orElseThrow(() -> new ForbiddenException("You are not a member of this conversation."));
		if (!member.isActive() || member.getStatus() != MemberStatus.ACTIVE) {
			throw new ForbiddenException("You are not an active member of this conversation.");
		}
		return messageRepository.findByConversationAndDeletedFalseOrderBySequenceNumberDesc(conversation, pageable)
				.map(this::toResponse);
	}

	private Conversation getConversation(UUID conversationId) {
		return conversationRepository.findById(conversationId)
				.orElseThrow(() -> new ResourceNotFoundException("Conversation not found."));
	}

	private Message getMessage(UUID messageId) {
		return messageRepository.findById(messageId)
				.orElseThrow(() -> new ResourceNotFoundException("Message not found."));
	}

	private User getUser(UUID userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found."));
	}

	private MessageResponse toResponse(Message message) {
		UUID replyToId = message.getReplyToMessage() != null ? message.getReplyToMessage().getId() : null;
		List<AttachmentResponse> attachments = Collections.emptyList();
		List<MessageReactionResponse> reactions = Collections.emptyList();
		return new MessageResponse(
				message.getId(),
				message.getSender().getId(),
				message.getSender().getUsername(),
				message.getSender().getProfilePictureUrl(),
				message.getType(),
				message.getStatus(),
				message.getContent(),
				message.isEdited(),
				message.getEditedAt(),
				message.getCreatedAt(),
				replyToId,
				attachments,
				reactions
		);
	}
}