package com.connectoBackend.common.config;

import com.connectoBackend.common.util.AuthUtil;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Provides the current authenticated user for JPA auditing.
 */
@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

		String currentUsername = AuthUtil.getCurrentUsername();

		if (currentUsername == null) {
            return Optional.of("SYSTEM");
        }

		return Optional.of(currentUsername);
    }

}