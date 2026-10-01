package org.example.chatty.auth.controller;

import jakarta.validation.Valid;
import org.example.chatty.auth.dto.LoginRequest;
import org.example.chatty.auth.dto.RegisterRequest;
import org.example.chatty.security.jwt.JwtService;
import org.example.chatty.user.dto.UserDto;
import org.example.chatty.user.entity.User;
import org.example.chatty.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(UserService userService, AuthenticationManager authenticationManager,
                          JwtService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestPart RegisterRequest request,
                                            @RequestPart(required = false) MultipartFile image) throws IOException {
        User user = new User();
        user.setUserName(request.userName());
        user.setEmail(request.email());
        user.setPassword(request.password());

        UserDto saved = userService.addUser(user, image);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.userName(), request.password()));

        String token = jwtService.generatedToken(request.userName());
        return ResponseEntity.ok(token);
    }
}