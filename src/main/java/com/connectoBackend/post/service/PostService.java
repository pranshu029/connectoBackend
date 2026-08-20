package com.connectoBackend.post.service;

import com.connectoBackend.post.dto.request.CreateCommentRequest;
import com.connectoBackend.post.dto.request.CreatePostRequest;
import com.connectoBackend.post.dto.request.UpdatePostRequest;
import com.connectoBackend.post.dto.response.PostCommentResponse;
import com.connectoBackend.post.dto.response.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PostService {

    PostResponse createPost(UUID userId, CreatePostRequest request);

    Page<PostResponse> getFeed(UUID userId, Pageable pageable);

    Page<PostResponse> getPostsByUser(UUID userId, UUID viewerId, Pageable pageable);

    PostResponse getPostById(UUID postId, UUID viewerId);

    PostResponse updatePost(UUID userId, UUID postId, UpdatePostRequest request);

    void deletePost(UUID userId, UUID postId);

    void likePost(UUID userId, UUID postId);

    void unlikePost(UUID userId, UUID postId);

    List<PostCommentResponse> getComments(UUID postId);

    PostCommentResponse addComment(UUID userId, UUID postId, CreateCommentRequest request);

    PostCommentResponse updateComment(UUID userId, UUID postId, UUID commentId, CreateCommentRequest request);

    void deleteComment(UUID userId, UUID postId, UUID commentId);

    PostResponse sharePost(UUID userId, UUID postId);
}
