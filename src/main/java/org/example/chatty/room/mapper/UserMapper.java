package org.example.chatty.room.mapper;

import org.example.chatty.room.dto.UserSummary;
import org.example.chatty.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserSummary toSummary(User user) {
        return new UserSummary(
                user.getId(),
                user.getUserName(),
                user.getImageName()
        );
    }
}