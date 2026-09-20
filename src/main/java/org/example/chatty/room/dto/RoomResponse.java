package org.example.chatty.room.dto;

import org.example.chatty.room.entity.RoomType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RoomResponse(
        UUID id,
        String name,
        RoomType type,
        UserSummary createdBy,
        LocalDateTime createdAt,
        List<UserSummary> members
) {
}