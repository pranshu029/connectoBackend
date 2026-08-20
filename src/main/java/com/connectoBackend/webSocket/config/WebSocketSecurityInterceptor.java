package com.connectoBackend.webSocket.config;

import com.connectoBackend.chat.entity.Conversation;
import com.connectoBackend.chat.repository.ConversationMemberRepository;
import com.connectoBackend.security.enums.TokenType;
import com.connectoBackend.security.service.JwtService;
import com.connectoBackend.user.entity.User;
import com.connectoBackend.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WebSocketSecurityInterceptor implements ChannelInterceptor {

    private static final String BEARER = "Bearer ";

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ConversationMemberRepository conversationMemberRepository;
    private final ObjectMapper objectMapper;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        if (command == StompCommand.CONNECT) {
            accessor.setUser(authenticate(accessor.getFirstNativeHeader("Authorization")));
            return message;
        }

        Principal principal = accessor.getUser();
        if (principal == null) {
            throw new AccessDeniedException("WebSocket authentication is required.");
        }

        if (command == StompCommand.SUBSCRIBE) {
            authorizeSubscription(accessor.getDestination(), principal);
        } else if (command == StompCommand.SEND) {
            authorizePayload(accessor.getDestination(), message.getPayload(), principal);
        }

        return message;
    }

    private Authentication authenticate(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER)) {
            throw new AccessDeniedException("WebSocket authentication is required.");
        }
        String token = authorizationHeader.substring(BEARER.length());
        if (jwtService.extractTokenType(token) != TokenType.ACCESS) {
            throw new AccessDeniedException("Only access tokens may be used for WebSocket authentication.");
        }
        String email = jwtService.extractUsername(token);
        UserDetails details = new org.springframework.security.core.userdetails.User(
                email, "", java.util.List.of());
        if (!jwtService.isTokenValid(token, details)) {
            throw new AccessDeniedException("Invalid or expired WebSocket token.");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("Authenticated user not found."));
        return new UsernamePasswordAuthenticationToken(user.getEmail(), null, java.util.List.of());
    }

    private void authorizeSubscription(String destination, Principal principal) {
        UUID conversationId = conversationIdFromDestination(destination);
        if (conversationId != null) {
            requireConversationMember(conversationId, principalUserId(principal));
        }
    }

    private void authorizePayload(String destination, Object payload, Principal principal) {
        if (destination == null || !destination.startsWith("/app/chat.")) {
            return;
        }
        JsonNode body;
        try {
            String json = payload instanceof byte[] bytes
                    ? new String(bytes, StandardCharsets.UTF_8)
                    : String.valueOf(payload);
            body = objectMapper.readTree(json);
        } catch (Exception exception) {
            throw new AccessDeniedException("Invalid WebSocket payload.");
        }

        UUID currentUserId = principalUserId(principal);
        String userField = destination.endsWith("chat.message") ? "senderId" : "userId";
        if (body.hasNonNull(userField) && !currentUserId.equals(UUID.fromString(body.get(userField).asText()))) {
            throw new AccessDeniedException("WebSocket user identity does not match the authenticated user.");
        }
        if (body.hasNonNull("conversationId")) {
            requireConversationMember(UUID.fromString(body.get("conversationId").asText()), currentUserId);
        }
    }

    private UUID principalUserId(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new AccessDeniedException("Authenticated user not found."))
                .getId();
    }

    private void requireConversationMember(UUID conversationId, UUID userId) {
        Conversation conversation = new Conversation();
        conversation.setId(conversationId);
        User user = new User();
        user.setId(userId);
        if (conversationMemberRepository.findByConversationAndUser(conversation, user).filter(member -> member.isActive()
                && member.getStatus() == com.connectoBackend.chat.enums.MemberStatus.ACTIVE).isEmpty()) {
            throw new AccessDeniedException("You are not an active member of this conversation.");
        }
    }

    private UUID conversationIdFromDestination(String destination) {
        if (destination == null || !destination.startsWith("/topic/conversations/")) {
            return null;
        }
        String value = destination.substring("/topic/conversations/".length()).split("/")[0];
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException exception) {
            throw new AccessDeniedException("Invalid conversation destination.");
        }
    }
}
