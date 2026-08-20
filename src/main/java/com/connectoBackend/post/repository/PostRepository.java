package com.connectoBackend.post.repository;

import com.connectoBackend.post.entity.Post;
import com.connectoBackend.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    Page<Post> findAllByAuthorAndDeletedFalseOrderByCreatedAtDesc(User author, Pageable pageable);

    Page<Post> findAllByDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    java.util.Optional<Post> findByIdAndDeletedFalse(UUID id);
}
