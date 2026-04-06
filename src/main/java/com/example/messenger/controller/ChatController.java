package com.example.messenger.controller;

import com.example.messenger.domain.model.Chat;
import com.example.messenger.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
@Tag(name = "Chat Controller", description = "Управление чатами мессенджера")
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "Создать новый чат", description = "Создает новый чат или подчат (если указан parentId) с заданным списком пользователей.")
    @PostMapping
    public ResponseEntity<String> createChat(
            @Parameter(description = "Название чата") @RequestParam String title,
            @Parameter(description = "Список имен пользователей (участников)") @RequestParam List<String> usernames,
            @Parameter(description = "ID родительского чата (опционально для создания подчата)") @RequestParam(required = false) Long parentId) {
        var chat = chatService.createChat(title, usernames, parentId);
        String prefix = (parentId != null) ? "Подчат" : "Чат";
        return ResponseEntity.ok(prefix + " успешно создан! ID: " + chat.getId());
    }

    @Operation(summary = "Добавить пользователя в чат", description = "Добавляет одного пользователя в существующий чат по username.")
    @PutMapping("/{chatId}/members")
    public ResponseEntity<String> addMember(
            @Parameter(description = "ID чата") @PathVariable Long chatId,
            @Parameter(description = "Имя добавляемого пользователя") @RequestParam String username) {
        chatService.addUserToChat(chatId, username);
        return ResponseEntity.ok("Пользователь успешно добавлен в чат " + chatId);
    }

    @Operation(summary = "Изменить название чата", description = "Обновляет заголовок существующего чата.")
    @PutMapping("/{chatId}")
    public ResponseEntity<String> updateChatTitle(
            @Parameter(description = "ID чата") @PathVariable Long chatId,
            @Parameter(description = "Новое название чата") @RequestParam String newTitle) {
        chatService.updateChatTitle(chatId, newTitle);
        return ResponseEntity.ok("Название чата успешно изменено на: " + newTitle);
    }

    @Operation(summary = "Удалить пользователя из чата", description = "Удаляет участника из чата по его username.")
    @DeleteMapping("/{chatId}/members")
    public ResponseEntity<Void> removeMember(
            @Parameter(description = "ID чата") @PathVariable Long chatId,
            @Parameter(description = "Имя удаляемого пользователя") @RequestParam String username) {
        chatService.removeUserFromChat(chatId, username);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Удалить чат", description = "Полностью удаляет чат по его ID.")
    @DeleteMapping("/{chatId}")
    public ResponseEntity<Void> deleteChat(
            @Parameter(description = "ID удаляемого чата") @PathVariable Long chatId) {
        chatService.deleteChat(chatId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить список всех чатов", description = "Возвращает полный список чатов.")
    @GetMapping
    public ResponseEntity<List<Chat>> getAllChats() {
        return ResponseEntity.ok(chatService.getAllChats());
    }

    @Operation(summary = "Получить чат по ID", description = "Возвращает чат по его уникальному идентификатору.")
    @GetMapping("/{chatId}")
    public ResponseEntity<Chat> getChatById(
            @Parameter(description = "ID чата") @PathVariable Long chatId) {
        Chat chat = chatService.getById(chatId);
        return chat != null ? ResponseEntity.ok(chat) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Получить подчаты", description = "Возвращает список названий всех подчатов для указанного чата.")
    @GetMapping("/{chatId}/subchats")
    public ResponseEntity<List<String>> getSubChats(
            @Parameter(description = "ID родительского чата") @PathVariable Long chatId) {
        List<String> subChats = chatService.getSubChats(chatId).stream()
                .map(chat -> chat.getTitle())
                .toList();
        return ResponseEntity.ok(subChats);
    }
}