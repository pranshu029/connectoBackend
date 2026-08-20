package com.connectoBackend.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePostRequest(
        @NotBlank(message = "Post content is required.")
        @Size(max = 5000, message = "Post content cannot exceed 5000 characters.")
        String content,

        String imageUrl
) {
}
