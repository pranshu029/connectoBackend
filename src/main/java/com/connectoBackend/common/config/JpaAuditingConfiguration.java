package com.connectoBackend.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables JPA auditing support.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfiguration {
}