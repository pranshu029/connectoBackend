package com.connectoBackend.post.service.impl;

import com.connectoBackend.common.exception.BadRequestException;
import com.connectoBackend.common.exception.ForbiddenException;
import com.connectoBackend.common.exception.ResourceNotFoundException;
import com.connectoBackend.notification.entity.Notification;
import com.connectoBackend.notification.enums.NotificationType;
import com.connectoBackend.notification.repository.NotificationRepository;
import com.connectoBackend.post.dto.request.CreateCommentRequest;
import com.connectoBackend.post.dto.request.CreatePostRequest;
import com.connectoBackend.post.dto.request.UpdatePostRequest;
import com.connectoBackend.post.dto.response.PostCommentResponse;
import com.connectoBackend.post.dto.response.PostResponse;
import com.connectoBackend.post.entity.Post;
import com.connectoBackend.post.entity.PostComment;
import com.connectoBackend.post.entity.PostLike;
import com.connectoBackend.post.repository.PostCommentRepository;
import com.connectoBackend.post.repository.PostLikeRepository;
import com.connectoBackend.post.repository.PostRepository;
import com.connectoBackend.post.service.PostService;
import com.connectoBackend.user.entity.User;
import com.connectoBackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostCommentRepository postCommentRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    @Override
    public PostResponse createPost(UUID userId, CreatePostRequest request) {
        User user = getUser(userId);
        Post post = Post.builder()
                .author(user)
                .content(request.content())
                .imageUrl(request.imageUrl())
                .build();

        post = postRepository.save(post);
        return toResponse(post);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getFeed(UUID userId, Pageable pageable) {
        getUser(userId);
        return postRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponse getPostById(UUID postId) {
        Post post = getPost(postId);
        return toResponse(post);
    }

    @Override
    public PostResponse updatePost(UUID userId, UUID postId, UpdatePostRequest request) {
        Post post = getPost(postId);
        if (!post.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException("You are not allowed to edit this post.");
        }

        if (request.content() != null) {
            post.setContent(request.content());
        }
        if (request.imageUrl() != null) {
            post.setImageUrl(request.imageUrl());
        }
        post = postRepository.save(post);
        return toResponse(post);
    }

    @Override
    public void deletePost(UUID userId, UUID postId) {
        Post post = getPost(postId);
        if (!post.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException("You are not allowed to delete this post.");
        }
        postRepository.delete(post);
    }

    @Override
    public void likePost(UUID userId, UUID postId) {
        User user = getUser(userId);
        Post post = getPost(postId);

        if (postLikeRepository.existsByPostAndUser(post, user)) {
            return;
        }

        postLikeRepository.save(PostLike.builder().post(post).user(user).build());

        if (!post.getAuthor().getId().equals(userId)) {
            Notification notification = Notification.builder()
                    .user(post.getAuthor())
                    .actor(user)
                    .type(NotificationType.POST_LIKE)
                    .title("New like")
                    .message(user.getFirstName() + " " + user.getLastName() + " liked your post.")
                    .entityId(postId)
                    .entityType("POST")
                    .readStatus(false)
                    .build();
            notificationRepository.save(notification);
        }
    }

    @Override
    public void unlikePost(UUID userId, UUID postId) {
        User user = getUser(userId);
        Post post = getPost(postId);
        postLikeRepository.deleteByPostAndUser(post, user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostCommentResponse> getComments(UUID postId) {
        Post post = getPost(postId);
        return postCommentRepository.findAllByPostOrderByCreatedAtAsc(post)
                .stream()
                .map(comment -> new PostCommentResponse(
                        comment.getId(),
                        comment.getUser().getId(),
                        comment.getUser().getUsername(),
                        comment.getUser().getFirstName() + " " + comment.getUser().getLastName(),
                        comment.getUser().getProfilePictureUrl(),
                        comment.getContent(),
                        comment.getCreatedAt()))
                .toList();
    }

    @Override
    public PostCommentResponse addComment(UUID userId, UUID postId, CreateCommentRequest request) {
        User user = getUser(userId);
        Post post = getPost(postId);

        PostComment comment = PostComment.builder()
                .post(post)
                .user(user)
                .content(request.content())
                .build();

        comment = postCommentRepository.save(comment);

        if (!post.getAuthor().getId().equals(userId)) {
            Notification notification = Notification.builder()
                    .user(post.getAuthor())
                    .actor(user)
                    .type(NotificationType.POST_COMMENT)
                    .title("New comment")
                    .message(user.getFirstName() + " " + user.getLastName() + " commented on your post.")
                    .entityId(postId)
                    .entityType("POST")
                    .readStatus(false)
                    .build();
            notificationRepository.save(notification);
        }

        return new PostCommentResponse(
                comment.getId(),
                user.getId(),
                user.getUsername(),
                user.getFirstName() + " " + user.getLastName(),
                user.getProfilePictureUrl(),
                comment.getContent(),
                comment.getCreatedAt());
    }

    @Override
    public PostCommentResponse updateComment(UUID userId, UUID postId, UUID commentId, CreateCommentRequest request) {
        Post post = getPost(postId);
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found."));
        if (!comment.getPost().getId().equals(post.getId()) || !comment.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You are not allowed to edit this comment.");
        }
        comment.setContent(request.content());
        comment = postCommentRepository.save(comment);
        return new PostCommentResponse(
                comment.getId(),
                comment.getUser().getId(),
                comment.getUser().getUsername(),
                comment.getUser().getFirstName() + " " + comment.getUser().getLastName(),
                comment.getUser().getProfilePictureUrl(),
                comment.getContent(),
                comment.getCreatedAt());
    }

    @Override
    public void deleteComment(UUID userId, UUID postId, UUID commentId) {
        Post post = getPost(postId);
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found."));
        if (!comment.getPost().getId().equals(post.getId()) || (!comment.getUser().getId().equals(userId) && !post.getAuthor().getId().equals(userId))) {
            throw new ForbiddenException("You are not allowed to delete this comment.");
        }
        postCommentRepository.delete(comment);
    }

    @Override
    public PostResponse sharePost(UUID userId, UUID postId) {
        User user = getUser(userId);
        Post originalPost = getPost(postId);

        Post share = Post.builder()
                .author(user)
                .content(originalPost.getContent())
                .imageUrl(originalPost.getImageUrl())
                .sharedPostId(originalPost.getId())
                .build();

        share = postRepository.save(share);

        Notification notification = Notification.builder()
                .user(originalPost.getAuthor())
                .actor(user)
                .type(NotificationType.POST_SHARE)
                .title("Post shared")
                .message(user.getFirstName() + " " + user.getLastName() + " shared your post.")
                .entityId(originalPost.getId())
                .entityType("POST")
                .readStatus(false)
                .build();
        notificationRepository.save(notification);

        return toResponse(share);
    }

    private PostResponse toResponse(Post post) {
        return new PostResponse(
                post.getId(),
                post.getAuthor().getId(),
                post.getAuthor().getUsername(),
                post.getAuthor().getFirstName() + " " + post.getAuthor().getLastName(),
                post.getAuthor().getProfilePictureUrl(),
                post.getContent(),
                post.getImageUrl(),
                post.getSharedPostId(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                postLikeRepository.countByPost(post),
                postCommentRepository.countByPost(post),
                getComments(post.getId())
        );
    }

    private Post getPost(UUID postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found."));
    }

    private User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }
}
