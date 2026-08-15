package org.example.chatty.user.controller;

import org.example.chatty.user.dto.UserDto;
import org.example.chatty.user.entity.User;
import org.example.chatty.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequestMapping("/api")
@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getUsers() {
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<?> getUser(@PathVariable String email) {
        UserDto userDto = userService.getUser(email);
        if (userDto != null)
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        else
            return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
    }

    @PostMapping("/user")
    public ResponseEntity<?> addUser(@RequestPart User user, @RequestPart MultipartFile image) {
        try {
            UserDto savedUserDto = userService.addUser(user, image);
            return new ResponseEntity<>(savedUserDto, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/user")
    public ResponseEntity<?> updateUser(@RequestPart User user, @RequestPart(required = false) MultipartFile image) {
        UserDto updated;
        try {
            updated = userService.updateUser(user, image);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        if (updated != null) return new ResponseEntity<>(updated, HttpStatus.OK);
        else return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/user/{email}")
    public ResponseEntity<?> deleteUser(@PathVariable String email) {
        UserDto existing = userService.getUser(email);
        if (existing == null) return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
        User tmp = new User();
        tmp.setEmail(email);
        userService.deleteUser(tmp);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

