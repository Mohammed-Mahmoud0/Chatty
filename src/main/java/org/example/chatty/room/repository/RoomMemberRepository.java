package org.example.chatty.room.repository;

import org.example.chatty.room.entity.Room;
import org.example.chatty.room.entity.RoomMember;
import org.example.chatty.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RoomMemberRepository extends JpaRepository<RoomMember, UUID> {
    List<RoomMember> findByRoom(Room room);

    boolean existsByRoomAndUser(Room room, User user);

    RoomMember findByRoomAndUser(Room room, User user);

    List<RoomMember> findByUserId(UUID userId);

    boolean existsByRoom_IdAndUser_UserName(UUID roomId, String userName);

    @Query("""
            SELECT rm.user.userName
            FROM RoomMember rm
            WHERE rm.room.id = :roomId
            """)
    List<String> findMemberUserNames(@Param("roomId") UUID roomId);
}
