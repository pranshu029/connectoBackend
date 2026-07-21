package com.connectoBackend.audit.service;

import com.connectoBackend.audit.entity.AuditLog;

import java.util.List;
import java.util.UUID;

public interface AuditService {

	AuditLog logAction(String action, String entityName, UUID entityId, String details);

	List<AuditLog> getRecentLogs();
}