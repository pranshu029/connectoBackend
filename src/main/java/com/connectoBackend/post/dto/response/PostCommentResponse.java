package com.connectoBackend.post.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record PostCommentResponse(
        UUID id,
        UUID userId,
        String username,
        String fullName,
        String profileImageUrl,
        String content,
        LocalDateTime createdAt
) {
}
