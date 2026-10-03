package org.example.chatty.chat.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        String content,
        LocalDateTime sentAt,
        UUID senderId,
        String senderUserName,
        UUID roomId
) {
}