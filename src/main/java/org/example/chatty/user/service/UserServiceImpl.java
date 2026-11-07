package org.example.chatty.user.service;

import org.example.chatty.common.exception.DuplicateUserException;
import org.example.chatty.common.exception.ForbiddenException;
import org.example.chatty.common.exception.UserNotFoundException;
import org.example.chatty.user.dto.UpdateUserRequest;
import org.example.chatty.user.dto.UserDto;
import org.example.chatty.user.entity.User;
import org.example.chatty.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private static final long MAX_IMAGE_BYTES = 1_048_576; // 1 MiB
    private static final Set<String> ALLOWED_IMAGE_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(rollbackFor = IOException.class)
    public UserDto addUser(User user, MultipartFile image) throws IOException {
        String username = normalizeUsername(user.getUserName());
        String email = normalizeEmail(user.getEmail());

        if (userRepository.existsByUserName(username)) {
            throw new DuplicateUserException("Username '" + username + "' is already taken");
        }
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateUserException("Email '" + email + "' is already registered");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        User newUser = new User();
        newUser.setUserName(username);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setStatus(false);
        applyImage(newUser, image);

        return toDto(userRepository.save(newUser));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(UUID id) {
        return toDto(userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        return toDto(userRepository.findByEmail(normalizeEmail(email))
                .orElseThrow(() -> new UserNotFoundException("User not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserByUserName(String userName) {
        return toDto(userRepository.findByUserName(userName)
                .orElseThrow(() -> new UserNotFoundException("User not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(rollbackFor = IOException.class)
    public UserDto updateUser(UUID id, UpdateUserRequest request, MultipartFile image)
            throws IOException {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        requireOwner(existing);

        if (request.userName() != null) {
            String username = normalizeUsername(request.userName());
            if (!username.equals(existing.getUserName())) {
                if (userRepository.existsByUserName(username)) {
                    throw new DuplicateUserException("Username '" + username + "' is already taken");
                }
                existing.setUserName(username);
            }
        }

        applyImage(existing, image);
        return toDto(userRepository.save(existing));
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        requireOwner(existing);
        userRepository.delete(existing);
        userRepository.flush();
    }

    private void requireOwner(User target) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserDetails)) {
            throw new AuthenticationCredentialsNotFoundException("Authentication required");
        }
        if (!auth.getName().equals(target.getUserName())) {
            throw new ForbiddenException("You can only modify your own profile");
        }
    }

    private void applyImage(User user, MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            return;
        }
        if (image.getSize() > MAX_IMAGE_BYTES) {
            throw new IllegalArgumentException("Profile image must be 1 MB or smaller");
        }
        String contentType = image.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("Profile image must be PNG, JPEG, or WebP");
        }

        String fileName = image.getOriginalFilename();
        if (fileName == null || fileName.isBlank()) {
            fileName = "profile-image";
        }
        fileName = fileName.replace('\\', '/');
        fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
        if (fileName.length() > 255) {
            throw new IllegalArgumentException("Profile image filename is too long");
        }

        user.setImageName(fileName);
        user.setImageType(contentType);
        user.setImageData(image.getBytes());
    }

    private String normalizeUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        String normalized = username.trim();
        if (normalized.length() < 3 || normalized.length() > 50) {
            throw new IllegalArgumentException("Username must be between 3 and 50 characters");
        }
        return normalized;
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private UserDto toDto(User user) {
        return new UserDto(
                user.getId(), user.getUserName(), user.getEmail(),
                user.isStatus(), user.getLastSeen(), user.getCreatedAt()
        );
    }
}
