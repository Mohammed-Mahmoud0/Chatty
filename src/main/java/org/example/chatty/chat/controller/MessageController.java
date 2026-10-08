package org.example.chatty.chat.controller;

import org.example.chatty.chat.dto.CreateMessageRequest;
import org.example.chatty.chat.dto.MessageResponse;
import org.example.chatty.chat.dto.UpdateMessageRequest;
import org.example.chatty.chat.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/rooms/{roomId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse createMessage(
            @PathVariable UUID roomId,
            @RequestBody CreateMessageRequest request
    ) {
        return messageService.createMessage(roomId, request);
    }

    @GetMapping("/rooms/{roomId}/messages")
    public List<MessageResponse> getRoomMessages(
            @PathVariable UUID roomId
    ) {
        return messageService.getRoomMessages(roomId);
    }

    @GetMapping("/messages/{messageId}")
    public MessageResponse getMessage(
            @PathVariable UUID messageId
    ) {
        return messageService.getMessage(messageId);
    }

    @PutMapping("/messages/{messageId}")
    public MessageResponse updateMessage(
            @PathVariable UUID messageId,
            @RequestBody UpdateMessageRequest request
    ) {
        return messageService.updateMessage(
                messageId,
                request
        );
    }

    @DeleteMapping("/messages/{messageId}")
    public void deleteMessage(
            @PathVariable UUID messageId
    ) {
        messageService.deleteMessage(messageId);
    }
}