package com.example.messenger.controller;

import com.example.messenger.domain.dto.UserDto;
import com.example.messenger.domain.model.User;
import com.example.messenger.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "Управление пользователями мессенджера")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Создать пользователя", description = "Регистрирует нового пользователя на основе переданных данных.")
    @PostMapping
    public ResponseEntity<User> createUser(
            @Parameter(description = "Данные нового пользователя") @RequestBody UserDto userDto) {
        User user = User.builder()
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .build();
        return ResponseEntity.ok(userService.createUser(user));
    }

    @Operation(summary = "Обновить имя пользователя", description = "Изменяет username существующего пользователя.")
    @PutMapping("/{userId}")
    public ResponseEntity<String> updateUsername(
            @Parameter(description = "ID пользователя") @PathVariable Long userId,
            @Parameter(description = "Новое имя пользователя") @RequestParam String newUsername) {
        userService.updateUsername(userId, newUsername);
        return ResponseEntity.ok("Имя пользователя успешно изменено на: " + newUsername);
    }

    @Operation(summary = "Удалить пользователя", description = "Полностью удаляет пользователя из системы по ID.")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID удаляемого пользователя") @PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех зарегистрированных пользователей.")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Получить пользователя по ID", description = "Возвращает данные пользователя по его уникальному идентификатору.")
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(
            @Parameter(description = "ID пользователя") @PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }
}