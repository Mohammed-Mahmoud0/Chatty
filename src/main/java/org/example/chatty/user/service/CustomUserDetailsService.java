package org.example.chatty.user.service;

import org.example.chatty.user.entity.User;
import org.example.chatty.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUserName())
                .password(normalizeStoredPassword(user.getPassword()))
                .authorities("USER")
                .build();
    }

    private String normalizeStoredPassword(String storedPassword) {
        if (storedPassword == null || storedPassword.isBlank()) {
            throw new UsernameNotFoundException("Invalid credentials");
        }
        if (storedPassword.startsWith("{")) {
            return storedPassword;
        }
        return "{noop}" + storedPassword;
    }
}
