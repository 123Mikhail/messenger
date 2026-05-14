package com.example.messenger.controller;

import com.example.messenger.domain.dto.MessageDto;
import com.example.messenger.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String TOPIC_CHATS = "/topic/chats/";

    @PostMapping
    public ResponseEntity<MessageDto> createMessage(@RequestBody MessageDto messageDto) {
        MessageDto saved = messageService.save(messageDto);
        messagingTemplate.convertAndSend(TOPIC_CHATS + saved.getChatId(), saved);
        return ResponseEntity.ok(saved);
    }

    // ИСПРАВЛЕНО: <?> заменено на <Object> для Сонара
    @GetMapping
    public ResponseEntity<Object> getMessages(
            @RequestParam(required = false) Long chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {
        if (chatId != null) {
            Page<MessageDto> pagedResult = messageService.getMessagesByChatIdPaged(chatId, PageRequest.of(page, size));
            return ResponseEntity.ok(pagedResult);
        }
        return ResponseEntity.ok(messageService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageDto> updateMessage(@PathVariable Long id, @RequestParam String newContent) {
        MessageDto updated = messageService.updateMessage(id, newContent);
        messagingTemplate.convertAndSend(TOPIC_CHATS + updated.getChatId(), updated);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        MessageDto msg = messageService.getById(id);
        messageService.deleteMessage(id);
        msg.setContent("[DELETED]");
        messagingTemplate.convertAndSend(TOPIC_CHATS + msg.getChatId(), msg);
        return ResponseEntity.noContent().build();
    }
}