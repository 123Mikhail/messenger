package com.example.messenger.controller;

import com.example.messenger.domain.model.Chat;
import com.example.messenger.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public String createChat(
            @RequestParam String title,
            @RequestParam List<String> usernames,
            @RequestParam(required = false) Long parentId) {

        Chat chat = chatService.createChat(title, usernames, parentId);
        String prefix = (parentId != null) ? "Подчат" : "Чат";
        return prefix + " успешно создан! ID: " + chat.getId();
    }

    @PutMapping("/{chatId}/members")
    public String addMember(@PathVariable Long chatId, @RequestParam String username) {
        chatService.addUserToChat(chatId, username);
        return "Пользователь " + username + " успешно добавлен в чат " + chatId;
    }

    @GetMapping("/{chatId}/subchats")
    public List<String> getSubChats(@PathVariable Long chatId) {
        return chatService.getSubChats(chatId).stream()
                .map(Chat::getTitle)
                .toList();
    }

    // НОВЫЙ ЭНДПОИНТ: Удалить пользователя из чата
    @DeleteMapping("/{chatId}/members")
    public ResponseEntity<Void> removeMember(@PathVariable Long chatId, @RequestParam String username) {
        chatService.removeUserFromChat(chatId, username);
        return ResponseEntity.noContent().build(); // Возвращает статус 204 No Content
    }

    // НОВЫЙ ЭНДПОИНТ: Удалить чат (или подчат)
    @DeleteMapping("/{chatId}")
    public ResponseEntity<Void> deleteChat(@PathVariable Long chatId) {
        chatService.deleteChat(chatId);
        return ResponseEntity.noContent().build(); // Возвращает статус 204 No Content
    }
}