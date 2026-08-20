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
import com.connectoBackend.user.repository.UserSettingsRepository;
import com.connectoBackend.user.repository.BlockedUserRepository;
import com.connectoBackend.common.exception.ForbiddenException;
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
    private final UserSettingsRepository userSettingsRepository;
    private final BlockedUserRepository blockedUserRepository;

    @Override
    public PostResponse createPost(UUID userId, CreatePostRequest request) {
        User user = getUser(userId);
        Post post = Post.builder()
                .author(user)
                .content(request.content())
                .imageUrl(request.imageUrl())
                .build();

        post = postRepository.save(post);
        return toResponse(post, user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getFeed(UUID userId, Pageable pageable) {
        User viewer = getUser(userId);
        return postRepository.findAllByDeletedFalseOrderByCreatedAtDesc(pageable)
            .map(post -> toResponse(post, viewer));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getPostsByUser(UUID userId, UUID viewerId, Pageable pageable) {
        User user = getUser(userId);
        User viewer = getUser(viewerId);
        return postRepository.findAllByAuthorAndDeletedFalseOrderByCreatedAtDesc(user, pageable)
                .map(post -> toResponse(post, viewer));
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponse getPostById(UUID postId, UUID viewerId) {
        Post post = getPost(postId);
        return toResponse(post, getUser(viewerId));
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
        return toResponse(post, getUser(userId));
    }

    @Override
    public void deletePost(UUID userId, UUID postId) {
        Post post = getPost(postId);
        if (!post.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException("You are not allowed to delete this post.");
        }
        post.setDeleted(true);
        postRepository.save(post);
    }

    @Override
    public void likePost(UUID userId, UUID postId) {
        User user = getUser(userId);
        Post post = getPost(postId);
		rejectBlocked(user, post.getAuthor());

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
        return postCommentRepository.findAllByPostAndDeletedFalseOrderByCreatedAtAsc(post)
                .stream()
                .map(comment -> new PostCommentResponse(
                        comment.getId(),
                        comment.getUser().getId(),
                        comment.getUser().getUsername(),
                        publicDisplayName(comment.getUser()),
                        publicProfileImage(comment.getUser()),
                        comment.getContent(),
                        comment.getCreatedAt()))
                .toList();
    }

    @Override
    public PostCommentResponse addComment(UUID userId, UUID postId, CreateCommentRequest request) {
        User user = getUser(userId);
        Post post = getPost(postId);
		rejectBlocked(user, post.getAuthor());

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
                publicDisplayName(user),
                publicProfileImage(user),
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
                publicDisplayName(comment.getUser()),
                publicProfileImage(comment.getUser()),
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
        comment.setDeleted(true);
        postCommentRepository.save(comment);
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

        return toResponse(share, user);
    }

    private PostResponse toResponse(Post post, User viewer) {
        return new PostResponse(
                post.getId(),
                post.getAuthor().getId(),
                post.getAuthor().getUsername(),
                publicDisplayName(post.getAuthor()),
                publicProfileImage(post.getAuthor()),
                post.getContent(),
                post.getImageUrl(),
                post.getSharedPostId(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                postLikeRepository.countByPost(post),
                postCommentRepository.countByPostAndDeletedFalse(post),
                postLikeRepository.existsByPostAndUser(post, viewer),
                getComments(post.getId())
        );
    }

    private String publicDisplayName(User user) {
        var settings = userSettingsRepository.findByUser(user).orElse(null);
        String firstName = settings != null && Boolean.TRUE.equals(settings.getFirstNamePublic())
            ? user.getFirstName() : null;
        String lastName = settings != null && Boolean.TRUE.equals(settings.getLastNamePublic())
            ? user.getLastName() : null;
        String fullName = ((firstName == null ? "" : firstName) + " "
            + (lastName == null ? "" : lastName)).trim();
        if (!fullName.isBlank()) {
            return fullName;
        }
        return user.getUsername();
    }

    private String publicProfileImage(User user) {
        return userSettingsRepository.findByUser(user)
                .filter(settings -> Boolean.TRUE.equals(settings.getProfilePicturePublic()))
                .map(settings -> user.getProfilePictureUrl())
                .orElse(null);
    }

    private Post getPost(UUID postId) {
        return postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found."));
    }

    private User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    private void rejectBlocked(User first, User second) {
        if (blockedUserRepository.existsByUserAndBlockedUser(first, second)
                || blockedUserRepository.existsByUserAndBlockedUser(second, first)) {
            throw new ForbiddenException("This user interaction is blocked.");
        }
    }
}
