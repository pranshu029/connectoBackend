package com.connectoBackend.chat.repository;

import com.connectoBackend.chat.entity.Attachment;
import com.connectoBackend.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Attachment entity.
 */
@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    List<Attachment> findAllByMessage(Message message);

}