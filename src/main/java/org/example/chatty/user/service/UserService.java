package org.example.chatty.user.service;

import org.example.chatty.user.dto.UserDto;
import org.example.chatty.user.entity.User;

import java.util.List;

public interface UserService {
    public UserDto addUser(User user);
    public UserDto getUserById(long id);
    public List<UserDto> getUsers();
    public UserDto updateUser(User user);
    public void deleteUser(User user);
}
