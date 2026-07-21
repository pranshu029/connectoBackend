package com.connectoBackend.chat.dto.response;

import com.connectoBackend.chat.enums.MessageStatus;
import com.connectoBackend.chat.enums.MessageType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for a chat message.
 */
public record MessageResponse(

        UUID id,

        UUID senderId,

        String senderName,

        String senderProfileImage,

        MessageType type,

        MessageStatus status,

        String content,

        boolean edited,

        LocalDateTime editedAt,

        LocalDateTime createdAt,

        UUID replyToMessageId,

        List<AttachmentResponse> attachments,

        List<MessageReactionResponse> reactions

) {
}