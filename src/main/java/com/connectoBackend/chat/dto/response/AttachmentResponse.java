package com.connectoBackend.chat.dto.response;

import com.connectoBackend.chat.enums.AttachmentType;

import java.util.UUID;

/**
 * Response DTO for an attachment.
 */
public record AttachmentResponse(

        UUID id,

        AttachmentType type,

        String fileName,

        String fileUrl,

        String contentType,

        Long fileSize,

        Integer width,

        Integer height

) {
}