package com.connectoBackend.user.service.impl;

import com.connectoBackend.common.exception.BadRequestException;
import com.connectoBackend.common.exception.ConflictException;
import com.connectoBackend.common.exception.ResourceNotFoundException;
import com.connectoBackend.notification.entity.Notification;
import com.connectoBackend.notification.enums.NotificationType;
import com.connectoBackend.notification.repository.NotificationRepository;
import com.connectoBackend.user.dto.response.UserSummaryResponse;
import com.connectoBackend.user.entity.Connection;
import com.connectoBackend.user.entity.User;
import com.connectoBackend.user.enums.ConnectionStatus;
import com.connectoBackend.user.repository.ConnectionRepository;
import com.connectoBackend.user.repository.UserRepository;
import com.connectoBackend.user.service.ConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ConnectionServiceImpl implements ConnectionService {

    private final UserRepository userRepository;
    private final ConnectionRepository connectionRepository;
    private final NotificationRepository notificationRepository;

    @Override
    public void sendConnectionRequest(UUID userId, UUID targetUserId) {
        if (userId.equals(targetUserId)) {
            throw new BadRequestException("You cannot connect with yourself.");
        }

        User user = getUser(userId);
        User targetUser = getUser(targetUserId);

        if (connectionRepository.existsConnectionBetweenUsers(user, targetUser)) {
            throw new ConflictException("A connection already exists or a request is already pending.");
        }

        Connection connection = Connection.builder()
                .user(user)
                .targetUser(targetUser)
                .status(ConnectionStatus.REQUEST_SENT)
                .build();

        connectionRepository.save(connection);

        Notification notification = Notification.builder()
                .user(targetUser)
                .actor(user)
                .type(NotificationType.CONNECTION_REQUEST)
                .title("Connection request")
                .message(user.getFirstName() + " " + user.getLastName() + " sent you a connection request.")
                .entityId(targetUserId)
                .entityType("USER")
                .readStatus(false)
                .build();

        notificationRepository.save(notification);
    }

    @Override
    public void acceptConnectionRequest(UUID userId, UUID targetUserId) {
        User user = getUser(userId);
        User targetUser = getUser(targetUserId);

        Connection request = connectionRepository.findByUserAndTargetUser(targetUser, user)
                .orElseThrow(() -> new ResourceNotFoundException("Connection request not found."));

        if (request.getStatus() != ConnectionStatus.REQUEST_SENT) {
            throw new BadRequestException("This request cannot be accepted.");
        }

        request.setStatus(ConnectionStatus.CONNECTED);

        connectionRepository.save(request);

        connectionRepository.findByUserAndTargetUser(user, targetUser)
                .ifPresentOrElse(
                        existing -> existing.setStatus(ConnectionStatus.CONNECTED),
                        () -> connectionRepository.save(
                                Connection.builder()
                                        .user(user)
                                        .targetUser(targetUser)
                                        .status(ConnectionStatus.CONNECTED)
                                        .build()
                        )
                );

        Notification notification = Notification.builder()
                .user(targetUser)
                .actor(user)
                .type(NotificationType.CONNECTION_ACCEPTED)
                .title("Connection accepted")
                .message(user.getFirstName() + " " + user.getLastName() + " accepted your connection request.")
                .entityId(targetUserId)
                .entityType("USER")
                .readStatus(false)
                .build();

        notificationRepository.save(notification);
    }

    @Override
    public void rejectConnectionRequest(UUID userId, UUID targetUserId) {
        User user = getUser(userId);
        User targetUser = getUser(targetUserId);

        Connection request = connectionRepository.findByUserAndTargetUser(targetUser, user)
                .orElseThrow(() -> new ResourceNotFoundException("Connection request not found."));

        if (request.getStatus() != ConnectionStatus.REQUEST_SENT) {
            throw new BadRequestException("This request cannot be rejected.");
        }

        connectionRepository.delete(request);

        Notification notification = Notification.builder()
                .user(targetUser)
                .actor(user)
                .type(NotificationType.CONNECTION_REJECTED)
                .title("Connection rejected")
                .message(user.getFirstName() + " " + user.getLastName() + " rejected your connection request.")
                .entityId(targetUserId)
                .entityType("USER")
                .readStatus(false)
                .build();

        notificationRepository.save(notification);
    }

    @Override
    public void removeConnection(UUID userId, UUID targetUserId) {
        User user = getUser(userId);
        User targetUser = getUser(targetUserId);

        if (userId.equals(targetUserId)) {
            throw new BadRequestException("You cannot remove yourself.");
        }

        connectionRepository.deleteByUserAndTargetUser(user, targetUser);
        connectionRepository.deleteByUserAndTargetUser(targetUser, user);
    }

    @Override
    public void cancelConnectionRequest(UUID userId, UUID targetUserId) {
        User user = getUser(userId);
        User targetUser = getUser(targetUserId);

        Connection request = connectionRepository.findByUserAndTargetUser(user, targetUser)
                .orElseThrow(() -> new ResourceNotFoundException("Connection request not found."));

        if (request.getStatus() != ConnectionStatus.REQUEST_SENT) {
            throw new BadRequestException("This request cannot be cancelled.");
        }

        connectionRepository.delete(request);
    }

    @Override
    @Transactional(readOnly = true)
    public ConnectionStatus getConnectionStatus(UUID userId, UUID targetUserId) {
        User user = getUser(userId);
        User targetUser = getUser(targetUserId);

        if (userId.equals(targetUserId)) {
            return ConnectionStatus.CONNECTED;
        }

        if (connectionRepository.existsConnectionBetweenUsers(user, targetUser)) {
            Connection connection = connectionRepository.findByUserAndTargetUser(user, targetUser)
                    .or(() -> connectionRepository.findByUserAndTargetUser(targetUser, user))
                    .orElseThrow(() -> new ResourceNotFoundException("Connection not found."));

            if (connection.getStatus() == ConnectionStatus.CONNECTED) {
                return ConnectionStatus.CONNECTED;
            }

            if (connection.getUser().equals(user) && connection.getStatus() == ConnectionStatus.REQUEST_SENT) {
                return ConnectionStatus.REQUEST_SENT;
            }

            if (connection.getTargetUser().equals(user) && connection.getStatus() == ConnectionStatus.REQUEST_SENT) {
                return ConnectionStatus.REQUEST_RECEIVED;
            }
        }

        return ConnectionStatus.NOT_CONNECTED;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSummaryResponse> getConnections(UUID userId) {
        User user = getUser(userId);
        return connectionRepository.findAllByUserAndStatus(user, ConnectionStatus.CONNECTED)
                .stream()
                .map(connection -> toSummary(connection.getTargetUser()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSummaryResponse> getIncomingRequests(UUID userId) {
        User user = getUser(userId);
        return connectionRepository.findAllByTargetUserAndStatus(user, ConnectionStatus.REQUEST_SENT)
                .stream()
                .map(connection -> toSummary(connection.getUser()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSummaryResponse> getSentRequests(UUID userId) {
        User user = getUser(userId);
        return connectionRepository.findAllByUserAndStatus(user, ConnectionStatus.REQUEST_SENT)
                .stream()
                .map(connection -> toSummary(connection.getTargetUser()))
                .toList();
    }

    private UserSummaryResponse toSummary(User user) {
        return new UserSummaryResponse(
                user.getId(),
                user.getUsername(),
                user.getFirstName() + " " + user.getLastName(),
                user.getProfilePictureUrl(),
                Boolean.TRUE.equals(user.getEmailVerified())
        );
    }

    private User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }
}
