package org.example.chatty.chat.repository;

import org.example.chatty.chat.entity.Message;
import org.example.chatty.room.entity.Room;
import org.example.chatty.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findByRoomOrderBySentAtAsc(Room room);

    List<Message> findBySender(User sender);
}