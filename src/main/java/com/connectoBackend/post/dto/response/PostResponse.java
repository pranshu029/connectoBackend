package com.connectoBackend.post.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PostResponse(
        UUID id,
        UUID authorId,
        String authorUsername,
        String authorFullName,
        String authorProfileImageUrl,
        String content,
        String imageUrl,
        UUID sharedPostId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        long likeCount,
        long commentCount,
        List<PostCommentResponse> comments
) {
}
