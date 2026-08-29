package org.example.chatty.user.dto;

import jakarta.persistence.Lob;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(
        UUID id,
        String userName,
        String email,
        boolean status,
        LocalDateTime lastSeen,
        LocalDateTime createdAt
) {

}
