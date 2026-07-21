package com.connectoBackend.chat.repository;

import com.connectoBackend.chat.entity.Attachment;
import com.connectoBackend.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for Attachment entity.
 */
@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {

    List<Attachment> findAllByMessage(Message message);

}