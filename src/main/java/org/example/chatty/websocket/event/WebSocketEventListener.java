
package org.example.chatty.websocket.event;

import org.example.chatty.websocket.service.PresenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
public class WebSocketEventListener {

    private static final Logger log =
            LoggerFactory.getLogger(WebSocketEventListener.class);

    private final PresenceService presenceService;

    public WebSocketEventListener(PresenceService presenceService) {
        this.presenceService = presenceService;
    }

    @EventListener
    public void onConnect(SessionConnectedEvent event) {

        Principal user = event.getUser();

        if (user == null) return;

        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = accessor.getSessionId();

        if (sessionId == null) return;

        presenceService.userConnected(
                sessionId,
                user.getName()
        );

        log.info("User {} connected: {}",
                user.getName(), sessionId);
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {

        String sessionId = event.getSessionId();

        presenceService.userDisconnected(sessionId);

        log.info("WebSocket session disconnected: {}", sessionId);
    }
}
