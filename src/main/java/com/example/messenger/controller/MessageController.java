package com.example.messenger.controller;

import com.example.messenger.domain.dto.ApiErrorResponse;
import com.example.messenger.domain.dto.MessageDto;
import com.example.messenger.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Message Controller", description = "Управление сообщениями")
public class MessageController {

    private final MessageService messageService;

    @Operation(summary = "Отправить новое сообщение")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Сообщение успешно создано"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные или чат/пользователь не существует",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Запрашиваемая сущность не найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Сообщение заблокировано антиспамом",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<MessageDto> createMessage(@RequestBody MessageDto messageDto) {
        return ResponseEntity.ok(messageService.save(messageDto));
    }

    @Operation(summary = "Массовая отправка сообщений (Bulk)")
    @PostMapping("/bulk")
    public ResponseEntity<List<MessageDto>> createMessagesBulk(@RequestBody List<MessageDto> messageDtos) {
        return ResponseEntity.ok(messageService.saveAll(messageDtos));
    }

    @Operation(summary = "Получить сообщение по ID")
    @GetMapping("/{id}")
    public ResponseEntity<MessageDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.getById(id));
    }

    @Operation(summary = "Получить все сообщения")
    @GetMapping
    public ResponseEntity<List<MessageDto>> getAll() {
        return ResponseEntity.ok(messageService.getAll());
    }

    @Operation(summary = "Поиск сообщений (JPQL)")
    @GetMapping("/search/jpql")
    public ResponseEntity<Page<MessageDto>> searchJpql(
            @RequestParam String chatTitle,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(messageService.searchMessagesJpql(chatTitle, keyword, PageRequest.of(page, size)));
    }

    @Operation(summary = "Поиск сообщений (Native SQL)")
    @GetMapping("/search/native")
    public ResponseEntity<Page<MessageDto>> searchNative(
            @RequestParam String chatTitle,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(messageService.searchMessagesNative(chatTitle, keyword, PageRequest.of(page, size)));
    }

    @Operation(summary = "Обновить сообщение")
    @PutMapping("/{id}")
    public ResponseEntity<MessageDto> updateMessage(@PathVariable Long id, @RequestParam String newContent) {
        return ResponseEntity.ok(messageService.updateMessage(id, newContent));
    }

    @Operation(summary = "Удалить сообщение")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }
}