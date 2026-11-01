
package org.example.chatty.websocket.security;

import org.example.chatty.security.jwt.JwtService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.JwtException;

@Component
public class WebSocketJwtInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public WebSocketJwtInterceptor(
            JwtService jwtService,
            UserDetailsService userDetailsService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Message<?> preSend(
            Message<?> message,
            MessageChannel channel
    ) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(
                        message,
                        StompHeaderAccessor.class
                );

        if (accessor == null) {
            return message;
        }

        StompCommand command = accessor.getCommand();

        // 1. Authenticate user during STOMP CONNECT
        if (StompCommand.CONNECT.equals(command)) {

            String header =
                    accessor.getFirstNativeHeader("Authorization");

            if (header == null || !header.startsWith("Bearer ")) {
                throw new AccessDeniedException("JWT is required");
            }

            String token = header.substring(7);

            try {
                String username = jwtService.extractUsername(token);

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(username);

                if (!jwtService.validateToken(token, userDetails)) {
                    throw new AccessDeniedException("Invalid JWT");
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                accessor.setUser(authentication);

            } catch (JwtException | IllegalArgumentException |
                     UsernameNotFoundException ex) {
                throw new AccessDeniedException("Invalid JWT", ex);
            }
        }

        // 2. SEND and SUBSCRIBE require authentication
        if (StompCommand.SEND.equals(command) ||
                StompCommand.SUBSCRIBE.equals(command)) {

            if (accessor.getUser() == null) {
                throw new AccessDeniedException(
                        "Authentication required"
                );
            }
        }

        // 3. Clients can send only to application message handlers
        if (StompCommand.SEND.equals(command)) {

            String destination = accessor.getDestination();

            if (destination == null ||
                    !destination.startsWith("/app/rooms/") ||
                    !destination.endsWith("/messages")) {

                throw new AccessDeniedException(
                        "Sending to this destination is not allowed"
                );
            }
        }

        // 4. Clients can subscribe only to their own message queue
        if (StompCommand.SUBSCRIBE.equals(command)) {

            String destination = accessor.getDestination();

            if (!"/user/queue/messages".equals(destination)) {
                throw new AccessDeniedException(
                        "Subscription destination not allowed"
                );
            }
        }

        return message;
    }
}
