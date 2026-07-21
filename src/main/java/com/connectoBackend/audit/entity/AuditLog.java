package com.connectoBackend.audit.entity;

import com.connectoBackend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
		name = "audit_logs",
		indexes = {
				@Index(name = "idx_audit_log_entity_name", columnList = "entity_name"),
				@Index(name = "idx_audit_log_actor", columnList = "performed_by")
		}
)
public class AuditLog extends BaseEntity {

	@Column(name = "action", nullable = false, length = 100)
	private String action;

	@Column(name = "entity_name", nullable = false, length = 100)
	private String entityName;

	@Column(name = "entity_id")
	private UUID entityId;

	@Column(name = "details", length = 1000)
	private String details;

	@Column(name = "performed_by", length = 100)
	private String performedBy;
}