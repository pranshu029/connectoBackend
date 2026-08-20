package com.connectoBackend.post.repository;

import com.connectoBackend.post.entity.Post;
import com.connectoBackend.post.entity.PostLike;
import com.connectoBackend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, UUID> {

    Optional<PostLike> findByPostAndUser(Post post, User user);

    List<PostLike> findAllByPost(Post post);

    long countByPost(Post post);

    boolean existsByPostAndUser(Post post, User user);

    void deleteByPostAndUser(Post post, User user);
}
