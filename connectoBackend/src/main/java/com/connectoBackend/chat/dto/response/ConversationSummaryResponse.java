package com.connectoBackend.chat.dto.response;

import java.time.LocalDateTime;

/**
 * Response DTO used for conversation listing.
 */
public record ConversationSummaryResponse(

        Long conversationId,

        String conversationName,

        String conversationImage,

        String lastMessage,

        LocalDateTime lastMessageTime,

        long unreadCount

) {
}