package org.example.chatty.user.service;

import org.example.chatty.user.dto.UserDto;
import org.example.chatty.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    public UserDto addUser(User user, MultipartFile image) throws IOException;
    public UserDto getUser(String email);
    public List<UserDto> getUsers();
    public UserDto updateUser(User user);
    public void deleteUser(User user);
}
