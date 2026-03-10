package com.example.messenger.controller;

import com.example.messenger.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // НОВЫЙ ЭНДПОИНТ: Изменить имя (логин) человека
    @PutMapping("/{userId}")
    public ResponseEntity<String> updateUsername(
            @PathVariable Long userId,
            @RequestParam String newUsername) {

        userService.updateUsername(userId, newUsername);
        return ResponseEntity.ok("Имя пользователя успешно изменено на: " + newUsername);
    }
}