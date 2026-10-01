package org.example.chatty.room.dto;

import org.example.chatty.room.entity.RoomType;

import java.util.List;
import java.util.UUID;

public record CreateRoomRequest(
        String name,
        RoomType type,
        List<UUID> memberIds
) {
}
