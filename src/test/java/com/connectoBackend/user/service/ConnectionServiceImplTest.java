package com.connectoBackend.user.service;

import com.connectoBackend.common.exception.ResourceNotFoundException;
import com.connectoBackend.user.entity.User;
import com.connectoBackend.user.enums.ConnectionStatus;
import com.connectoBackend.user.repository.ConnectionRepository;
import com.connectoBackend.user.repository.UserRepository;
import com.connectoBackend.user.service.impl.ConnectionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConnectionServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConnectionRepository connectionRepository;

    @InjectMocks
    private ConnectionServiceImpl connectionService;

    private User userA;
    private User userB;

    @BeforeEach
    void setUp() {
        userA = new User();
        userA.setId(UUID.randomUUID());
        userA.setEmail("alice@test.com");

        userB = new User();
        userB.setId(UUID.randomUUID());
        userB.setEmail("bob@test.com");
    }

    @Test
    void getConnectionStatus_shouldReturnConnected_whenUsersAreConnected() {
        when(userRepository.findById(userA.getId())).thenReturn(Optional.of(userA));
        when(userRepository.findById(userB.getId())).thenReturn(Optional.of(userB));
        when(connectionRepository.existsConnectionBetweenUsers(userA, userB)).thenReturn(true);
        when(connectionRepository.findByUserAndTargetUser(userA, userB))
                .thenReturn(Optional.of(new com.connectoBackend.user.entity.Connection(userA, userB, ConnectionStatus.CONNECTED)));

        ConnectionStatus status = connectionService.getConnectionStatus(userA.getId(), userB.getId());

        assertEquals(ConnectionStatus.CONNECTED, status);
    }

    @Test
    void getConnectionStatus_shouldThrow_whenEitherUserDoesNotExist() {
        when(userRepository.findById(userA.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> connectionService.getConnectionStatus(userA.getId(), userB.getId()));
    }
}
