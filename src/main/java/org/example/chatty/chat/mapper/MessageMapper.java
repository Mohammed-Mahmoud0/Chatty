package org.example.chatty.chat.mapper;

import org.example.chatty.chat.dto.MessageResponse;
import org.example.chatty.chat.entity.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public MessageResponse toResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getContent(),
                message.getSentAt(),
                message.getSender().getId(),
                message.getSender().getUserName(),
                message.getRoom().getId()
        );
    }
}