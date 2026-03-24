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
    public ResponseEntity<String> createChat(
            @RequestParam String title,
            @RequestParam List<String> usernames,
            @RequestParam(required = false) Long parentId) {
        var chat = chatService.createChat(title, usernames, parentId);
        String prefix = (parentId != null) ? "Подчат" : "Чат";
        return ResponseEntity.ok(prefix + " успешно создан! ID: " + chat.getId());
    }

    @PutMapping("/{chatId}/members")
    public ResponseEntity<String> addMember(@PathVariable Long chatId, @RequestParam String username) {
        chatService.addUserToChat(chatId, username);
        return ResponseEntity.ok("Пользователь успешно добавлен в чат " + chatId);
    }

    @PutMapping("/{chatId}")
    public ResponseEntity<String> updateChatTitle(@PathVariable Long chatId, @RequestParam String newTitle) {
        chatService.updateChatTitle(chatId, newTitle);
        return ResponseEntity.ok("Название чата успешно изменено на: " + newTitle);
    }

    @DeleteMapping("/{chatId}/members")
    public ResponseEntity<Void> removeMember(@PathVariable Long chatId, @RequestParam String username) {
        chatService.removeUserFromChat(chatId, username);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{chatId}")
    public ResponseEntity<Void> deleteChat(@PathVariable Long chatId) {
        chatService.deleteChat(chatId);
        return ResponseEntity.noContent().build();
    }



    @GetMapping
    public ResponseEntity<List<Chat>> getAllChats() {
        return ResponseEntity.ok(chatService.getAllChats());
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<Chat> getChatById(@PathVariable Long chatId) {
        Chat chat = chatService.getById(chatId);
        return chat != null ? ResponseEntity.ok(chat) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{chatId}/subchats")
    public ResponseEntity<List<String>> getSubChats(@PathVariable Long chatId) {
        List<String> subChats = chatService.getSubChats(chatId).stream()
                .map(chat -> chat.getTitle())
                .toList();
        return ResponseEntity.ok(subChats);
    }
}