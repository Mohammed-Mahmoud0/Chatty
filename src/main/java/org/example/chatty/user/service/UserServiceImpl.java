package org.example.chatty.user.service;

import org.example.chatty.common.exception.DuplicateUserException;
import org.example.chatty.common.exception.UserNotFoundException;
import org.example.chatty.user.dto.UserDto;
import org.example.chatty.user.entity.User;
import org.example.chatty.user.repository.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto addUser(User user, MultipartFile image) throws IOException {
        if (userRepo.existsByUserName(user.getUserName())) {
            throw new DuplicateUserException("Username '" + user.getUserName() + "' is already taken");
        }
        if (userRepo.existsByEmail(user.getEmail())) {
            throw new DuplicateUserException("Email '" + user.getEmail() + "' is already registered");
        }

        user.setCreatedAt(LocalDateTime.now());
        user.setPassword(encodeIfNeeded(user.getPassword()));

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
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return toDto(user);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return toDto(user);
    }

    @Override
    public UserDto getUserByUserName(String userName) {
        User user = userRepo.findByUserName(userName)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return toDto(user);
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepo.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public UserDto updateUser(UUID id, User user, MultipartFile image) throws IOException {
        User existing = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getUserName() != null) existing.setUserName(user.getUserName());
        if (user.getPassword() != null) existing.setPassword(encodeIfNeeded(user.getPassword()));
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
        User existing = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
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

    private String encodeIfNeeded(String password) {
        if (password == null) return null;
        if (password.startsWith("{")) return password;
        return passwordEncoder.encode(password);
    }
}