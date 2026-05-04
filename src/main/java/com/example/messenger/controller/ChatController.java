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
            @RequestParam(required = false) Long parentId,
            @RequestParam(defaultValue = "GROUP") String type) {
        var chat = chatService.createChat(title, usernames, parentId, type);
        return ResponseEntity.ok("Успешно создан! ID: " + chat.getId());
    }

    @PutMapping("/{chatId}/members")
    public ResponseEntity<String> addMember(@PathVariable Long chatId, @RequestParam String username) {
        chatService.addUserToChat(chatId, username);
        return ResponseEntity.ok("Добавлен");
    }

    @PutMapping("/{chatId}")
    public ResponseEntity<String> updateChatTitle(@PathVariable Long chatId, @RequestParam String newTitle) {
        chatService.updateChatTitle(chatId, newTitle);
        return ResponseEntity.ok("Изменено");
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
    public ResponseEntity<List<Chat>> getAllChats() { return ResponseEntity.ok(chatService.getAllChats()); }

    @GetMapping("/{chatId}")
    public ResponseEntity<Chat> getChatById(@PathVariable Long chatId) { return ResponseEntity.ok(chatService.getById(chatId)); }

    @GetMapping("/{chatId}/subchats")
    public ResponseEntity<List<String>> getSubChats(@PathVariable Long chatId) {
        return ResponseEntity.ok(chatService.getSubChats(chatId).stream().map(Chat::getTitle).toList());
    }
}