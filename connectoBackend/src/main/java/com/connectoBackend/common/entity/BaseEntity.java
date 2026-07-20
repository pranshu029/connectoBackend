package com.connectoBackend.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Base entity containing common audit and lifecycle fields.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@FieldNameConstants
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class BaseEntity {

    // -> Primary key.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    // -> Entity creation timestamp.
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // -> Entity last modification timestamp.
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // -> User who created the entity.
    @CreatedBy
    @Column(name = "created_by", updatable = false, length = 100)
    private String createdBy;

    // -> User who last modified the entity.
    @LastModifiedBy
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    // -> Optimistic locking version.
    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    // -> Indicates whether the entity has been soft deleted.
    @Builder.Default
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    // -> Timestamp of soft deletion.
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

}