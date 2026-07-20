package com.connectoBackend.chat.dto.response;

import com.connectoBackend.chat.enums.MessageStatus;
import com.connectoBackend.chat.enums.MessageType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for a chat message.
 */
public record MessageResponse(

        Long id,

        Long senderId,

        String senderName,

        String senderProfileImage,

        MessageType type,

        MessageStatus status,

        String content,

        boolean edited,

        LocalDateTime editedAt,

        LocalDateTime createdAt,

        Long replyToMessageId,

        List<AttachmentResponse> attachments,

        List<MessageReactionResponse> reactions

) {
}