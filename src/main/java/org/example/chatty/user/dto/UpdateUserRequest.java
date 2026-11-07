package org.example.chatty.user.dto;

import jakarta.validation.constraints.Size;

/**
 * Only fields that a user may change through the profile endpoint.
 * Null userName means "leave it unchanged" (for example, an image-only update).
 */
public record UpdateUserRequest(
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String userName
) {
}
