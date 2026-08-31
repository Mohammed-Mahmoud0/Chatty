package org.example.chatty.auth.dto;

public record AuthResponse(
        String token,
        String tokenType
) {
    public AuthResponse(String token) {
        this(token,  "Bearer");
    }
}