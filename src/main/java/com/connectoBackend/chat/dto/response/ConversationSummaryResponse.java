package com.connectoBackend.chat.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO used for conversation listing.
 */
public record ConversationSummaryResponse(

        UUID conversationId,

        String conversationName,

        String conversationImage,

        String lastMessage,

        LocalDateTime lastMessageTime,

        long unreadCount

) {
}