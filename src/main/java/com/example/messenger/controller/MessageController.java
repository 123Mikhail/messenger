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

    @Operation(summary = "Создать новое сообщение", description = "Сохраняет сообщение в БД. Включает валидацию и антиспам.")
    @PostMapping
    public ResponseEntity<MessageDto> create(@Valid @RequestBody MessageDto dto) {
        return ResponseEntity.ok(messageService.save(dto));
    }

    @Operation(summary = "Получить сообщение по ID", description = "Возвращает конкретное сообщение или ошибку 404, если оно не найдено.")
    @GetMapping("/{id}")
    public ResponseEntity<MessageDto> getById(
            @Parameter(description = "Уникальный идентификатор сообщения") @PathVariable Long id) {
        return ResponseEntity.ok(messageService.getById(id));
    }

    @Operation(summary = "Получить список сообщений", description = "Возвращает все сообщения. Поддерживает фильтрацию по отправителю или ID чата.")
    @GetMapping
    public ResponseEntity<List<MessageDto>> getMessages(
            @Parameter(description = "Имя отправителя для фильтрации") @RequestParam(required = false) String sender,
            @Parameter(description = "ID чата для фильтрации") @RequestParam(required = false) Long chatId) {

        if (sender != null) {
            return ResponseEntity.ok(messageService.getBySender(sender));
        } else if (chatId != null) {
            return ResponseEntity.ok(messageService.getByChatId(chatId));
        }
        return ResponseEntity.ok(messageService.getAll());
    }

    @Operation(summary = "Обновить текст сообщения", description = "Изменяет текст существующего сообщения и сбрасывает кэш.")
    @PutMapping("/{id}")
    public ResponseEntity<MessageDto> updateMessage(
            @Parameter(description = "ID обновляемого сообщения") @PathVariable Long id,
            @Parameter(description = "Новый текст сообщения") @RequestParam String newContent) {
        return ResponseEntity.ok(messageService.updateMessage(id, newContent));
    }

    @Operation(summary = "Удалить сообщение", description = "Удаляет сообщение по ID и очищает кэш.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(
            @Parameter(description = "ID удаляемого сообщения") @PathVariable Long id) {
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Поиск сообщений (с использованием JPQL и кэша)", description = "Ищет сообщения по названию чата и ключевому слову с поддержкой пагинации.")
    @GetMapping("/search/jpql")
    public ResponseEntity<Page<MessageDto>> searchJpql(
            @Parameter(description = "Название или часть названия чата") @RequestParam String chatTitle,
            @Parameter(description = "Слово для поиска в тексте сообщения") @RequestParam String keyword,
            @Parameter(description = "Номер страницы (начиная с 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Количество элементов на странице") @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        return ResponseEntity.ok(messageService.searchMessagesJpql(chatTitle, keyword, pageable));
    }

    @Operation(summary = "Поиск сообщений (с использованием Native Query)", description = "Ищет сообщения с помощью чистого SQL-запроса (без кэша).")
    @GetMapping("/search/native")
    public ResponseEntity<Page<MessageDto>> searchNative(
            @Parameter(description = "Название или часть названия чата") @RequestParam String chatTitle,
            @Parameter(description = "Слово для поиска в тексте сообщения") @RequestParam String keyword,
            @Parameter(description = "Номер страницы (начиная с 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Количество элементов на странице") @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        return ResponseEntity.ok(messageService.searchMessagesNative(chatTitle, keyword, pageable));
    }
}