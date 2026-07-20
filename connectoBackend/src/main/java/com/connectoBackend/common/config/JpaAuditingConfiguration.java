package com.connectoBackend.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/**
 * Enables JPA auditing support.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfiguration {

    // Provides the current authenticated user for auditing
    @Bean
    public AuditorAware<String> auditorProvider() {

        // TODO: Replace with authenticated username once Spring Security is implemented
        return () -> Optional.of("SYSTEM");
    }
}