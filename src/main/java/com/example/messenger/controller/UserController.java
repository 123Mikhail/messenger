package com.example.messenger.controller;

import com.example.messenger.domain.dto.UserDto;
import com.example.messenger.domain.model.User;
import com.example.messenger.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserDto userDto) {
        User user = User.builder()
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .build();
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<String> updateUsername(
            @PathVariable Long userId,
            @RequestParam String newUsername) {
        userService.updateUsername(userId, newUsername);
        return ResponseEntity.ok("Имя пользователя успешно изменено на: " + newUsername);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    // --- НОВЫЕ ЭНДПОИНТЫ ДЛЯ ПОЛУЧЕНИЯ ПОЛЬЗОВАТЕЛЕЙ ---

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }
}