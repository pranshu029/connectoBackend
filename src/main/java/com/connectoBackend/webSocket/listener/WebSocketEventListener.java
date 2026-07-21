package com.connectoBackend.webSocket.listener;

import com.connectoBackend.webSocket.event.MessageEvent;
import com.connectoBackend.webSocket.event.PresenceEvent;
import com.connectoBackend.webSocket.event.TypingEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WebSocketEventListener {

	@EventListener
	public void onMessageEvent(MessageEvent event) {
		log.debug("WebSocket message event: conversation={}, message={}, sender={}", event.conversationId(), event.messageId(), event.senderId());
	}

	@EventListener
	public void onTypingEvent(TypingEvent event) {
		log.debug("WebSocket typing event: conversation={}, user={}, typing={}", event.conversationId(), event.userId(), event.typing());
	}

	@EventListener
	public void onPresenceEvent(PresenceEvent event) {
		log.debug("WebSocket presence event: user={}, online={}", event.userId(), event.online());
	}
}