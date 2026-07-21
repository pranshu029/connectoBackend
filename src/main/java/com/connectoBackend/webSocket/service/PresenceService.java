package com.connectoBackend.webSocket.service;

import java.util.Set;
import java.util.UUID;

public interface PresenceService {

	void markOnline(UUID userId);

	void markOffline(UUID userId);

	boolean isOnline(UUID userId);

	Set<UUID> getOnlineUsers();
}
