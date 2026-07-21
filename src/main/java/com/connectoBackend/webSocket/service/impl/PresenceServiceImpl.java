package com.connectoBackend.webSocket.service.impl;

import com.connectoBackend.webSocket.service.PresenceService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

@Service
public class PresenceServiceImpl implements PresenceService {

	private final Set<UUID> onlineUsers = ConcurrentHashMap.newKeySet();

	@Override
	public void markOnline(UUID userId) {
		onlineUsers.add(userId);
	}

	@Override
	public void markOffline(UUID userId) {
		onlineUsers.remove(userId);
	}

	@Override
	public boolean isOnline(UUID userId) {
		return onlineUsers.contains(userId);
	}

	@Override
	public Set<UUID> getOnlineUsers() {
		return Collections.unmodifiableSet(onlineUsers);
	}
}