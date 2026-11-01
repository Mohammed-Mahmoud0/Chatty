
package org.example.chatty.websocket.controller;

import org.example.chatty.chat.dto.CreateMessageRequest;
import org.example.chatty.chat.dto.MessageResponse;
import org.example.chatty.chat.service.MessageService;
import org.example.chatty.room.repository.RoomMemberRepository;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Controller
public class MessageWebSocketController {

    private final MessageService messageService;
    private final RoomMemberRepository roomMemberRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageWebSocketController(
            MessageService messageService,
            RoomMemberRepository roomMemberRepository,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.messageService = messageService;
        this.roomMemberRepository = roomMemberRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/rooms/{roomId}/messages")
    public void sendMessage(
            @DestinationVariable UUID roomId,
            @Payload CreateMessageRequest request,
            Principal principal
    ) {
        if (principal == null) {
            throw new AccessDeniedException("Authentication required");
        }

        MessageResponse savedMessage =
                messageService.createMessageFromWebSocket(
                        roomId,
                        request,
                        principal.getName()
                );

        // Get current members AFTER saving the message.
        var usernames = roomMemberRepository.findMemberUserNames(roomId);

        // Deliver only to current room members.
        for (String username : usernames) {
            messagingTemplate.convertAndSendToUser(
                    username,
                    "/queue/messages",
                    savedMessage
            );
        }
    }
}
