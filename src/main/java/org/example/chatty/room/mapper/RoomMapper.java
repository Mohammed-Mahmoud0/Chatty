package org.example.chatty.room.mapper;

import org.example.chatty.room.dto.RoomResponse;
import org.example.chatty.room.entity.Room;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {
    private final UserMapper userMapper;

    public RoomMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public RoomResponse toRoomResponse(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getName(),
                room.getType(),
                userMapper.toSummary(room.getCreatedBy()),
                room.getCreatedAt(),
                room.getMembers()
                        .stream()
                        .map(member -> userMapper.toSummary(member.getUser()))
                        .toList()
        );
    }
}
