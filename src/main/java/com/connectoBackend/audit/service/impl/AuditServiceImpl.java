package com.connectoBackend.audit.service.impl;

import com.connectoBackend.audit.entity.AuditLog;
import com.connectoBackend.audit.repository.AuditLogRepository;
import com.connectoBackend.audit.service.AuditService;
import com.connectoBackend.common.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditServiceImpl implements AuditService {

	private final AuditLogRepository auditLogRepository;

	@Override
	public AuditLog logAction(String action, String entityName, UUID entityId, String details) {
		AuditLog auditLog = AuditLog.builder()
				.action(action)
				.entityName(entityName)
				.entityId(entityId)
				.details(details)
				.performedBy(AuthUtil.getCurrentUsername())
				.build();
		return auditLogRepository.save(auditLog);
	}

	@Override
	@Transactional(readOnly = true)
	public List<AuditLog> getRecentLogs() {
		return auditLogRepository.findAll();
	}
}