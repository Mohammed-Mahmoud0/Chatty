
package org.example.chatty.websocket.service;

import org.example.chatty.user.repository.UserRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class PresenceService {

    private final UserRepository userRepository;

    // sessionId -> username
    private final Map<String, String> sessions = new HashMap<>();

    public PresenceService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public synchronized void userConnected(
            String sessionId,
            String username
    ) {
        if (sessions.containsKey(sessionId)) {
            return;
        }

        // Only update DB when the first session connects.
        if (!sessions.containsValue(username)) {
            userRepository.markOnline(username);
        }

        sessions.put(sessionId, username);
    }

    public synchronized void userDisconnected(String sessionId) {

        String username = sessions.get(sessionId);

        // Ignore duplicate or unknown disconnects.
        if (username == null) {
            return;
        }

        boolean hasOtherSessions = sessions.entrySet()
                .stream()
                .anyMatch(entry ->
                        !entry.getKey().equals(sessionId)
                                && entry.getValue().equals(username)
                );

        if (!hasOtherSessions) {
            userRepository.markOffline(
                    username,
                    LocalDateTime.now()
            );
        }

        sessions.remove(sessionId);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void resetPresenceOnStartup() {
        userRepository.resetOnlineStatuses();
    }
}
