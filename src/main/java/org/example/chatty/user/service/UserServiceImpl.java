package org.example.chatty.user.service;

import org.example.chatty.user.dto.UserDto;
import org.example.chatty.user.entity.User;
import org.example.chatty.user.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private UserRepo userRepo;

    @Autowired
    public UserServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDto addUser(User user, MultipartFile image) throws IOException {
        user.setCreatedAt(LocalDateTime.now());

        user.setImageName(image.getOriginalFilename());
        user.setImageType(image.getContentType());
        user.setImageData(image.getBytes());
        User savedUser = userRepo.save(user);
        return new UserDto(
                savedUser.getUserName(),
                savedUser.getEmail(),
                savedUser.isStatus(),
                savedUser.getLastSeen(),
                savedUser.getCreatedAt(),
                savedUser.getImageName(),
                savedUser.getImageType(),
                savedUser.getImageData()
        );
    }

    @Override
    public UserDto getUser(String email) {
        User user = userRepo.findByEmail(email);
        if (user != null) {
            return new UserDto(
                    user.getUserName(),
                    user.getEmail(),
                    user.isStatus(),
                    user.getLastSeen(),
                    user.getCreatedAt(),
                    user.getImageName(),
                    user.getImageType(),
                    user.getImageData()
            );
        } else {
            return null;
        }
    }

    @Override
    public List<UserDto> getUsers() {
        List<User> users = userRepo.findAll();

        return users.stream()
                .map(user -> new UserDto(
                        user.getUserName(),
                        user.getEmail(),
                        user.isStatus(),
                        user.getLastSeen(),
                        user.getCreatedAt(),
                        user.getImageName(),
                        user.getImageType(),
                        user.getImageData()
                ))
                .toList();
    }

    @Override
    public UserDto updateUser(User user) {
        return null;
    }

    @Override
    public void deleteUser(User user) {


    }
}
