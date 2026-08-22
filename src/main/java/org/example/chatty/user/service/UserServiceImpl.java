package org.example.chatty.user.service;

import org.example.chatty.user.dto.UserDto;
import org.example.chatty.user.entity.User;
import org.example.chatty.user.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;

    @Autowired
    public UserServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDto addUser(User user, MultipartFile image) throws IOException {
        user.setCreatedAt(LocalDateTime.now());

        if (image != null && !image.isEmpty()) {
            user.setImageName(image.getOriginalFilename());
            user.setImageType(image.getContentType());
            user.setImageData(image.getBytes());
        }
        User savedUser = userRepo.save(user);
        return toDto(savedUser);
    }

    @Override
    public UserDto getUserById(UUID id) {

        User user = userRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        return toDto(user);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepo.findByEmail(email).orElseThrow(() ->
                new RuntimeException("User not found"));
        return toDto(user);
    }

    @Override
    public UserDto getUserByUserName(String userName) {
        User user = userRepo.findByUserName(userName).orElseThrow(() ->
                new RuntimeException("User not found"));
        return toDto(user);
    }

    @Override
    public List<UserDto> getUsers() {
        List<User> users = userRepo.findAll();

        return users.stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public UserDto updateUser(UUID id, User user, MultipartFile image) throws IOException {
        User existing = userRepo.findById(id).orElseThrow(() ->
                new RuntimeException("User not found"));

        if (user.getUserName() != null) existing.setUserName(user.getUserName());
        if (user.getPassword() != null) existing.setPassword(user.getPassword());
        existing.setStatus(user.isStatus());
        if (user.getLastSeen() != null) existing.setLastSeen(user.getLastSeen());

        if (image != null && !image.isEmpty()) {
            existing.setImageName(image.getOriginalFilename());
            existing.setImageType(image.getContentType());
            existing.setImageData(image.getBytes());
        } else {
            if (user.getImageName() != null) existing.setImageName(user.getImageName());
            if (user.getImageType() != null) existing.setImageType(user.getImageType());
            if (user.getImageData() != null) existing.setImageData(user.getImageData());
        }

        User saved = userRepo.save(existing);
        return toDto(saved);
    }

    @Override
    public void deleteUser(UUID id) {
        User existing = userRepo.findById(id).orElseThrow(() ->
                new RuntimeException("User not found"));
        userRepo.delete(existing);
    }

    private UserDto toDto(User user) {

        return new UserDto(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.isStatus(),
                user.getLastSeen(),
                user.getCreatedAt()
        );
    }
}
