package org.example.chatty.user.dto;

import jakarta.persistence.Lob;

import java.time.LocalDateTime;

public record UserDto(
        String userName,
        String email,
        boolean status,
        LocalDateTime lastSeen,
        LocalDateTime createdAt,
        String imageName,
        String imageType,
        @Lob
        byte[] imageData
    )
{

}
