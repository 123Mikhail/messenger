package com.example.messenger.controller;

import com.example.messenger.domain.dto.MessageDto;
import com.example.messenger.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Message Controller", description = "Управление сообщениями в мессенджере")
public class MessageController {

    private final MessageService messageService;

    @Operation(summary = "Создать новое сообщение", description = "Сохраняет сообщение в БД (с валидацией)")
    @PostMapping
    public ResponseEntity<MessageDto> create(@Valid @RequestBody MessageDto dto) {
        return ResponseEntity.ok(messageService.save(dto));
    }

    @Operation(summary = "Получить сообщение по ID")
    @GetMapping("/{id}")
    public ResponseEntity<MessageDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.getById(id));
    }

    @Operation(summary = "Получить все сообщения (с фильтрацией)")
    @GetMapping
    public ResponseEntity<List<MessageDto>> getMessages(
            @Parameter(description = "Имя отправителя") @RequestParam(required = false) String sender,
            @Parameter(description = "ID чата") @RequestParam(required = false) Long chatId) {

        if (sender != null) {
            return ResponseEntity.ok(messageService.getBySender(sender));
        } else if (chatId != null) {
            return ResponseEntity.ok(messageService.getByChatId(chatId));
        }
        return ResponseEntity.ok(messageService.getAll());
    }

    @Operation(summary = "Обновить текст сообщения")
    @PutMapping("/{id}")
    public ResponseEntity<MessageDto> updateMessage(
            @PathVariable Long id,
            @RequestParam String newContent) {
        return ResponseEntity.ok(messageService.updateMessage(id, newContent));
    }

    @Operation(summary = "Удалить сообщение")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Поиск сообщений (JPQL) с кэшем")
    @GetMapping("/search/jpql")
    public ResponseEntity<Page<MessageDto>> searchJpql(
            @RequestParam String chatTitle,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        return ResponseEntity.ok(messageService.searchMessagesJpql(chatTitle, keyword, pageable));
    }

    @Operation(summary = "Поиск сообщений (Native Query)")
    @GetMapping("/search/native")
    public ResponseEntity<Page<MessageDto>> searchNative(
            @RequestParam String chatTitle,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        return ResponseEntity.ok(messageService.searchMessagesNative(chatTitle, keyword, pageable));
    }
}