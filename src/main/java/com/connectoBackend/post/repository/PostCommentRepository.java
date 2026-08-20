package com.connectoBackend.post.repository;

import com.connectoBackend.post.entity.Post;
import com.connectoBackend.post.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, UUID> {

    List<PostComment> findAllByPostAndDeletedFalseOrderByCreatedAtAsc(Post post);

    long countByPostAndDeletedFalse(Post post);
}
