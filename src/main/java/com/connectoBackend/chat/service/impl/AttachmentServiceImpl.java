package com.connectoBackend.chat.service.impl;

import com.connectoBackend.chat.dto.response.AttachmentResponse;
import com.connectoBackend.chat.entity.Attachment;
import com.connectoBackend.chat.entity.Message;
import com.connectoBackend.chat.enums.AttachmentType;
import com.connectoBackend.chat.repository.AttachmentRepository;
import com.connectoBackend.chat.repository.MessageRepository;
import com.connectoBackend.chat.service.AttachmentService;
import com.connectoBackend.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AttachmentServiceImpl implements AttachmentService {

	private final AttachmentRepository attachmentRepository;
	private final MessageRepository messageRepository;

	@Override
	public AttachmentResponse addAttachment(UUID messageId, AttachmentType type, String fileName, String fileUrl, String contentType, Long fileSize, Integer width, Integer height) {
		Message message = getMessage(messageId);
		Attachment attachment = Attachment.builder()
				.message(message)
				.type(type)
				.fileName(fileName)
				.fileUrl(fileUrl)
				.contentType(contentType)
				.fileSize(fileSize)
				.width(width)
				.height(height)
				.build();
		attachment = attachmentRepository.save(attachment);
		return toResponse(attachment);
	}

	@Override
	@Transactional(readOnly = true)
	public List<AttachmentResponse> getAttachments(UUID messageId) {
		Message message = getMessage(messageId);
		return attachmentRepository.findAllByMessage(message)
				.stream()
				.map(this::toResponse)
				.toList();
	}

	private Message getMessage(UUID messageId) {
		return messageRepository.findById(messageId)
				.orElseThrow(() -> new ResourceNotFoundException("Message not found."));
	}

	private AttachmentResponse toResponse(Attachment attachment) {
		return new AttachmentResponse(
				attachment.getId(),
				attachment.getType(),
				attachment.getFileName(),
				attachment.getFileUrl(),
				attachment.getContentType(),
				attachment.getFileSize(),
				attachment.getWidth(),
				attachment.getHeight()
		);
	}
}