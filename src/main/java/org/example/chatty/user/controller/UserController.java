package org.example.chatty.user.controller;

import org.example.chatty.security.jwt.JwtService;
import org.example.chatty.user.dto.UserDto;
import org.example.chatty.user.entity.User;
import org.example.chatty.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequestMapping("/api/users")
@RestController
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable UUID id) {
        UserDto userDto = userService.getUserById(id);

        return ResponseEntity.ok(userDto);
    }

    @GetMapping(params = "email")
    public ResponseEntity<?> getUser(@RequestParam String email) {
        UserDto userDto = userService.getUserByEmail(email);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @GetMapping(params = "userName")
    public ResponseEntity<?> getUserByUserName(@RequestParam String userName) {
        UserDto userDto = userService.getUserByUserName(userName);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> addUser(@RequestPart User user, @RequestPart(required = false) MultipartFile image) {
        try {
            UserDto savedUserDto = userService.addUser(user, image);
            return new ResponseEntity<>(savedUserDto, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUserName(),
                        user.getPassword()
                )
        );
        if (authentication.isAuthenticated()) {
            return new ResponseEntity<>(jwtService.generatedToken(user.getUserName()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable UUID id, @RequestPart User user, @RequestPart(required = false) MultipartFile image) {
        try {
            UserDto updated = userService.updateUser(id, user, image);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

