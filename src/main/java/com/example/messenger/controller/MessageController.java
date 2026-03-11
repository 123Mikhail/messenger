package com.example.messenger.controller;

import com.example.messenger.domain.dto.MessageDto;
import com.example.messenger.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageDto> create(@RequestBody MessageDto dto) {
        return ResponseEntity.ok(messageService.save(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<MessageDto>> getMessages(
            @RequestParam(required = false) String sender,
            @RequestParam(required = false) Long chatId) {

        if (sender != null) {
            return ResponseEntity.ok(messageService.getBySender(sender));
        } else if (chatId != null) {
            return ResponseEntity.ok(messageService.getByChatId(chatId));
        }
        return ResponseEntity.ok(messageService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageDto> updateMessage(
            @PathVariable Long id,
            @RequestParam String newContent) {
        return ResponseEntity.ok(messageService.updateMessage(id, newContent));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }
}