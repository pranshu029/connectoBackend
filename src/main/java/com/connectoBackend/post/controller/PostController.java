package com.connectoBackend.post.controller;

import com.connectoBackend.common.response.ApiResponse;
import com.connectoBackend.post.dto.request.CreateCommentRequest;
import com.connectoBackend.post.dto.request.CreatePostRequest;
import com.connectoBackend.post.dto.request.UpdatePostRequest;
import com.connectoBackend.post.dto.response.PostCommentResponse;
import com.connectoBackend.post.dto.response.PostResponse;
import com.connectoBackend.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PostResponse> createPost(
            @RequestParam UUID userId,
            @Valid @RequestBody CreatePostRequest request
    ) {
        return ApiResponse.<PostResponse>builder()
                .success(true)
                .message("Post created successfully.")
                .data(postService.createPost(userId, request))
                .build();
    }

    @GetMapping("/feed")
    public ApiResponse<Page<PostResponse>> getFeed(
            @RequestParam UUID userId,
            Pageable pageable
    ) {
        return ApiResponse.<Page<PostResponse>>builder()
                .success(true)
                .message("Feed fetched successfully.")
                .data(postService.getFeed(userId, pageable))
                .build();
    }

    @GetMapping("/{postId}")
    public ApiResponse<PostResponse> getPostById(@PathVariable UUID postId) {
        return ApiResponse.<PostResponse>builder()
                .success(true)
                .message("Post fetched successfully.")
                .data(postService.getPostById(postId))
                .build();
    }

    @PutMapping("/{postId}")
    public ApiResponse<PostResponse> updatePost(
            @RequestParam UUID userId,
            @PathVariable UUID postId,
            @Valid @RequestBody UpdatePostRequest request
    ) {
        return ApiResponse.<PostResponse>builder()
                .success(true)
                .message("Post updated successfully.")
                .data(postService.updatePost(userId, postId, request))
                .build();
    }

    @DeleteMapping("/{postId}")
    public ApiResponse<Void> deletePost(
            @RequestParam UUID userId,
            @PathVariable UUID postId
    ) {
        postService.deletePost(userId, postId);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Post deleted successfully.")
                .build();
    }

    @PostMapping("/{postId}/likes/users/{userId}")
    public ApiResponse<Void> likePost(
            @PathVariable UUID postId,
            @PathVariable UUID userId
    ) {
        postService.likePost(userId, postId);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Post liked successfully.")
                .build();
    }

    @DeleteMapping("/{postId}/likes/users/{userId}")
    public ApiResponse<Void> unlikePost(
            @PathVariable UUID postId,
            @PathVariable UUID userId
    ) {
        postService.unlikePost(userId, postId);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Post unliked successfully.")
                .build();
    }

    @GetMapping("/{postId}/comments")
    public ApiResponse<List<PostCommentResponse>> getComments(@PathVariable UUID postId) {
        return ApiResponse.<List<PostCommentResponse>>builder()
                .success(true)
                .message("Comments fetched successfully.")
                .data(postService.getComments(postId))
                .build();
    }

    @PostMapping("/{postId}/comments")
    public ApiResponse<PostCommentResponse> addComment(
            @RequestParam UUID userId,
            @PathVariable UUID postId,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        return ApiResponse.<PostCommentResponse>builder()
                .success(true)
                .message("Comment added successfully.")
                .data(postService.addComment(userId, postId, request))
                .build();
    }

    @PutMapping("/{postId}/comments/{commentId}")
    public ApiResponse<PostCommentResponse> updateComment(
            @RequestParam UUID userId,
            @PathVariable UUID postId,
            @PathVariable UUID commentId,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        return ApiResponse.<PostCommentResponse>builder()
                .success(true)
                .message("Comment updated successfully.")
                .data(postService.updateComment(userId, postId, commentId, request))
                .build();
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ApiResponse<Void> deleteComment(
            @RequestParam UUID userId,
            @PathVariable UUID postId,
            @PathVariable UUID commentId
    ) {
        postService.deleteComment(userId, postId, commentId);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Comment deleted successfully.")
                .build();
    }

    @PostMapping("/{postId}/share/users/{userId}")
    public ApiResponse<PostResponse> sharePost(
            @PathVariable UUID postId,
            @PathVariable UUID userId
    ) {
        return ApiResponse.<PostResponse>builder()
                .success(true)
                .message("Post shared successfully.")
                .data(postService.sharePost(userId, postId))
                .build();
    }
}
