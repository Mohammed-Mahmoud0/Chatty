package org.example.chatty.user.service;

import org.example.chatty.user.dto.UserDto;
import org.example.chatty.user.entity.User;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDto addUser(User user, MultipartFile image) throws IOException;
    List<UserDto> getUsers();
    UserDto getUserById(UUID id);
    UserDto getUserByEmail(String email);
    UserDto getUserByUserName(String userName);
    UserDto updateUser(UUID id, User user, MultipartFile image) throws IOException;
    void deleteUser(UUID id);
}
