package com.connectoBackend.chat.entity;

import com.connectoBackend.chat.enums.AttachmentType;
import com.connectoBackend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Represents an attachment associated with a message.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "attachments",
        indexes = {
                @Index(
                        name = "idx_attachment_message",
                        columnList = "message_id"
                )
        }
)
public class Attachment extends BaseEntity {

    // -> Message to which this attachment belongs.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    // -> Attachment type.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttachmentType type;

    // -> Original file name.
    @Column(nullable = false, length = 255)
    private String fileName;

    // -> Storage URL/path.
    @Column(nullable = false, length = 1000)
    private String fileUrl;

    // -> MIME type.
    @Column(nullable = false, length = 100)
    private String contentType;

    // -> File size in bytes.
    @Column(nullable = false)
    private Long fileSize;

    // -> Optional image/video width.
    private Integer width;

    // -> Optional image/video height.
    private Integer height;

}