package org.example.chatty.user.controller;

import org.example.chatty.user.dto.UserDto;
import org.example.chatty.user.entity.User;
import org.example.chatty.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequestMapping("/api/users")
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping(params = "email")
    public ResponseEntity<UserDto> getUser(@RequestParam String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping(params = "userName")
    public ResponseEntity<UserDto> getUserByUserName(@RequestParam String userName) {
        return ResponseEntity.ok(userService.getUserByUserName(userName));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable UUID id, @RequestPart User user,
                                              @RequestPart(required = false) MultipartFile image) throws IOException {
        return ResponseEntity.ok(userService.updateUser(id, user, image));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}