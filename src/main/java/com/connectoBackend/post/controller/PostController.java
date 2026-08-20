package com.connectoBackend.post.controller;

import com.connectoBackend.common.response.ApiResponse;
import com.connectoBackend.common.exception.ForbiddenException;
import com.connectoBackend.post.dto.request.CreateCommentRequest;
import com.connectoBackend.post.dto.request.CreatePostRequest;
import com.connectoBackend.post.dto.request.UpdatePostRequest;
import com.connectoBackend.post.dto.response.PostCommentResponse;
import com.connectoBackend.post.dto.response.PostResponse;
import com.connectoBackend.post.service.PostService;
import com.connectoBackend.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
        private final UserRepository userRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PostResponse> createPost(
            @RequestParam(required = false) UUID userId,
            @Valid @RequestBody CreatePostRequest request,
            Authentication authentication
    ) {
        return ApiResponse.<PostResponse>builder()
                .success(true)
                .message("Post created successfully.")
                .data(postService.createPost(resolveUserId(userId, authentication), request))
                .build();
    }

    @GetMapping("/feed")
    public ApiResponse<Page<PostResponse>> getFeed(
                        @RequestParam(required = false) UUID userId,
                        Pageable pageable,
                        Authentication authentication
    ) {
        return ApiResponse.<Page<PostResponse>>builder()
                .success(true)
                .message("Feed fetched successfully.")
                                .data(postService.getFeed(resolveUserId(userId, authentication), pageable))
                .build();
    }

    @GetMapping("/users/{userId}")
    public ApiResponse<Page<PostResponse>> getPostsByUser(
            @PathVariable UUID userId,
            Pageable pageable,
            Authentication authentication
    ) {
        return ApiResponse.<Page<PostResponse>>builder()
                .success(true)
                .message("User posts fetched successfully.")
                .data(postService.getPostsByUser(userId, resolveUserId(null, authentication), pageable))
                .build();
    }

        private UUID resolveUserId(UUID userId, Authentication authentication) {
                UUID authenticatedUserId = userRepository.findByEmail(authentication.getName())
                                .orElseThrow(() -> new IllegalStateException("Authenticated user not found."))
                                .getId();
                if (userId != null && !userId.equals(authenticatedUserId)) {
                        throw new ForbiddenException("You can act only as the authenticated user.");
                }
                return authenticatedUserId;
        }

    @GetMapping("/{postId}")
        public ApiResponse<PostResponse> getPostById(@PathVariable UUID postId, Authentication authentication) {
        return ApiResponse.<PostResponse>builder()
                .success(true)
                .message("Post fetched successfully.")
                .data(postService.getPostById(postId, resolveUserId(null, authentication)))
                .build();
    }

    @PutMapping("/{postId}")
    public ApiResponse<PostResponse> updatePost(
            @RequestParam(required = false) UUID userId,
            @PathVariable UUID postId,
            @Valid @RequestBody UpdatePostRequest request,
            Authentication authentication
    ) {
        return ApiResponse.<PostResponse>builder()
                .success(true)
                .message("Post updated successfully.")
                .data(postService.updatePost(resolveUserId(null, authentication), postId, request))
                .build();
    }

    @DeleteMapping("/{postId}")
    public ApiResponse<Void> deletePost(
                        @RequestParam(required = false) UUID userId,
                        @PathVariable UUID postId,
                        Authentication authentication
    ) {
                postService.deletePost(resolveUserId(null, authentication), postId);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Post deleted successfully.")
                .build();
    }

    @PostMapping("/{postId}/likes/users/{userId}")
    public ApiResponse<Void> likePost(
            @PathVariable UUID postId,
            @PathVariable UUID userId,
            Authentication authentication
    ) {
        postService.likePost(resolveUserId(userId, authentication), postId);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Post liked successfully.")
                .build();
    }

    @DeleteMapping("/{postId}/likes/users/{userId}")
    public ApiResponse<Void> unlikePost(
            @PathVariable UUID postId,
            @PathVariable UUID userId,
            Authentication authentication
    ) {
        postService.unlikePost(resolveUserId(userId, authentication), postId);
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
            @RequestParam(required = false) UUID userId,
            @PathVariable UUID postId,
            @Valid @RequestBody CreateCommentRequest request,
            Authentication authentication
    ) {
        return ApiResponse.<PostCommentResponse>builder()
                .success(true)
                .message("Comment added successfully.")
                .data(postService.addComment(resolveUserId(userId, authentication), postId, request))
                .build();
    }

    @PutMapping("/{postId}/comments/{commentId}")
    public ApiResponse<PostCommentResponse> updateComment(
            @RequestParam UUID userId,
            @PathVariable UUID postId,
            @PathVariable UUID commentId,
            @Valid @RequestBody CreateCommentRequest request,
            Authentication authentication
    ) {
        return ApiResponse.<PostCommentResponse>builder()
                .success(true)
                .message("Comment updated successfully.")
                .data(postService.updateComment(resolveUserId(userId, authentication), postId, commentId, request))
                .build();
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ApiResponse<Void> deleteComment(
            @RequestParam UUID userId,
            @PathVariable UUID postId,
            @PathVariable UUID commentId,
            Authentication authentication
    ) {
        postService.deleteComment(resolveUserId(userId, authentication), postId, commentId);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Comment deleted successfully.")
                .build();
    }

    @PostMapping("/{postId}/share/users/{userId}")
    public ApiResponse<PostResponse> sharePost(
            @PathVariable UUID postId,
            @PathVariable UUID userId,
            Authentication authentication
    ) {
        return ApiResponse.<PostResponse>builder()
                .success(true)
                .message("Post shared successfully.")
                .data(postService.sharePost(resolveUserId(userId, authentication), postId))
                .build();
    }
}
