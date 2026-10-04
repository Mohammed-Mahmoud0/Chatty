package org.example.chatty.chat.service;

import org.example.chatty.chat.dto.CreateMessageRequest;
import org.example.chatty.chat.dto.MessageResponse;
import org.example.chatty.chat.dto.UpdateMessageRequest;
import org.example.chatty.chat.entity.Message;
import org.example.chatty.chat.mapper.MessageMapper;
import org.example.chatty.chat.repository.MessageRepository;
import org.example.chatty.common.exception.ResourceNotFoundException;
import org.example.chatty.room.entity.Room;
import org.example.chatty.room.repository.RoomMemberRepository;
import org.example.chatty.room.repository.RoomRepository;
import org.example.chatty.user.entity.User;
import org.example.chatty.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;

    public MessageService(
            MessageRepository messageRepository,
            RoomRepository roomRepository,
            RoomMemberRepository roomMemberRepository,
            UserRepository userRepository,
            MessageMapper messageMapper
    ) {
        this.messageRepository = messageRepository;
        this.roomRepository = roomRepository;
        this.roomMemberRepository = roomMemberRepository;
        this.userRepository = userRepository;
        this.messageMapper = messageMapper;
    }

    @Transactional
    public MessageResponse createMessage(
            UUID roomId,
            CreateMessageRequest request
    ) {
        User currentUser = getCurrentUser();

        validateContent(request.content());

        Room room = getRoom(roomId);

        checkMembership(room, currentUser);

        Message message = new Message();
        message.setContent(request.content());
        message.setSentAt(LocalDateTime.now());
        message.setSender(currentUser);
        message.setRoom(room);

        Message savedMessage = messageRepository.save(message);

        return messageMapper.toResponse(savedMessage);
    }

    public List<MessageResponse> getRoomMessages(UUID roomId) {
        User currentUser = getCurrentUser();

        Room room = getRoom(roomId);

        checkMembership(room, currentUser);

        return messageRepository
                .findByRoomOrderBySentAtAsc(room)
                .stream()
                .map(messageMapper::toResponse)
                .toList();
    }

    public MessageResponse getMessage(UUID messageId) {
        User currentUser = getCurrentUser();

        Message message = getMessageById(messageId);

        checkMembership(message.getRoom(), currentUser);

        return messageMapper.toResponse(message);
    }

    @Transactional
    public MessageResponse updateMessage(
            UUID messageId,
            UpdateMessageRequest request
    ) {
        User currentUser = getCurrentUser();

        validateContent(request.content());

        Message message = getMessageById(messageId);

        if (!message.getSender().getId().equals(currentUser.getId())) {
            throw new RuntimeException(
                    "You can only edit your own messages"
            );
        }

        message.setContent(request.content());

        return messageMapper.toResponse(message);
    }

    @Transactional
    public void deleteMessage(UUID messageId) {
        User currentUser = getCurrentUser();

        Message message = getMessageById(messageId);

        if (!message.getSender().getId().equals(currentUser.getId())) {
            throw new RuntimeException(
                    "You can only delete your own messages"
            );
        }

        messageRepository.delete(message);
    }

    private Room getRoom(UUID roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new RuntimeException("Room not found"));
    }

    private Message getMessageById(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Message not found"));
    }

    private void checkMembership(Room room, User user) {
        boolean isMember =
                roomMemberRepository.existsByRoomAndUser(room, user);

        if (!isMember) {
            throw new RuntimeException(
                    "You are not a member of this room"
            );
        }
    }

    private User getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        return userRepository
                .findByUserName(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException(
                    "Message content cannot be empty"
            );
        }
    }
}