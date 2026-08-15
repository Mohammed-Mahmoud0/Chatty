package org.example.chatty.user.service;

import org.example.chatty.user.dto.UserDto;
import org.example.chatty.user.entity.User;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface UserService {
    UserDto addUser(User user, MultipartFile image) throws IOException;
    UserDto getUser(String email);
    List<UserDto> getUsers();
    UserDto updateUser(User user, MultipartFile image) throws IOException;
    void deleteUser(User user);
}
