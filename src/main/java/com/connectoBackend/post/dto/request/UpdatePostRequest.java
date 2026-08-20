package com.connectoBackend.post.dto.request;

import jakarta.validation.constraints.Size;

public record UpdatePostRequest(
        @Size(max = 5000, message = "Post content cannot exceed 5000 characters.")
        String content,

        String imageUrl
) {
}
