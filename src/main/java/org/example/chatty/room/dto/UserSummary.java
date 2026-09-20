package org.example.chatty.room.dto;

import java.util.UUID;

public record UserSummary(
        UUID id,
        String userName,
        String imageName
) {
}