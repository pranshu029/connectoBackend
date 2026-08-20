package com.connectoBackend.user.service;

import com.connectoBackend.user.dto.response.UserSummaryResponse;
import com.connectoBackend.user.enums.ConnectionStatus;

import java.util.List;
import java.util.UUID;

public interface ConnectionService {

    void sendConnectionRequest(UUID userId, UUID targetUserId);

    void acceptConnectionRequest(UUID userId, UUID targetUserId);

    void rejectConnectionRequest(UUID userId, UUID targetUserId);

    void removeConnection(UUID userId, UUID targetUserId);

    void cancelConnectionRequest(UUID userId, UUID targetUserId);

    ConnectionStatus getConnectionStatus(UUID userId, UUID targetUserId);

    List<UserSummaryResponse> getConnections(UUID userId);

    List<UserSummaryResponse> getIncomingRequests(UUID userId);

    List<UserSummaryResponse> getSentRequests(UUID userId);
}
