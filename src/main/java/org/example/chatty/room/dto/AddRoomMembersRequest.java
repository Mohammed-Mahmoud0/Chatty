package org.example.chatty.room.dto;

import java.util.List;
import java.util.UUID;

public record AddRoomMembersRequest(
        List<UUID> userIds
) {
}
