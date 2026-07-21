package com.connectoBackend.schedular;

import com.connectoBackend.session.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupScheduler {

	private final RefreshTokenService refreshTokenService;

	@Scheduled(fixedDelay = 3600000)
	public void cleanupExpiredRefreshTokens() {
		refreshTokenService.removeExpiredRefreshTokens();
	}
}