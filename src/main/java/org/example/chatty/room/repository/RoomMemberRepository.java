package org.example.chatty.room.repository;

import org.example.chatty.room.entity.Room;
import org.example.chatty.room.entity.RoomMember;
import org.example.chatty.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoomMemberRepository extends JpaRepository<RoomMember, UUID> {
    List<RoomMember> findByRoom(Room room);

    boolean existsByRoomAndUser(Room room, User user);
}
