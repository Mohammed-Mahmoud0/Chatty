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
        Optional<User> userOpt = userRepo.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
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
    public UserDto updateUser(User user, MultipartFile image) throws IOException {
        Optional<User> existingOpt = userRepo.findByEmail(user.getEmail());
        if (existingOpt.isEmpty()) {
            return null;
        }
        User existing = existingOpt.get();

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
        return new UserDto(
                saved.getUserName(),
                saved.getEmail(),
                saved.isStatus(),
                saved.getLastSeen(),
                saved.getCreatedAt(),
                saved.getImageName(),
                saved.getImageType(),
                saved.getImageData()
        );
    }

    @Override
    public void deleteUser(User user) {
        Optional<User> existingOpt = userRepo.findByEmail(user.getEmail());
        userRepo.delete(existingOpt.orElseThrow(() -> new RuntimeException("User not found")));
    }
}
