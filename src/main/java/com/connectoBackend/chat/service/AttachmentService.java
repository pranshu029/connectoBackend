package com.connectoBackend.chat.service;

import com.connectoBackend.chat.dto.response.AttachmentResponse;
import com.connectoBackend.chat.enums.AttachmentType;

import java.util.List;
import java.util.UUID;

public interface AttachmentService {

	AttachmentResponse addAttachment(
			UUID messageId,
			AttachmentType type,
			String fileName,
			String fileUrl,
			String contentType,
			Long fileSize,
			Integer width,
			Integer height
	);

	List<AttachmentResponse> getAttachments(UUID messageId);
}