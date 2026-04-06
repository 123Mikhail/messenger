package com.example.messenger.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для передачи данных о сообщении")
public class MessageDto {

    @Schema(description = "ID сообщения", example = "1")
    private Long id;

    @Schema(description = "Содержимое сообщения", example = "Привет, как дела?")
    @NotBlank(message = "Сообщение не должно быть пустым")
    @Size(max = 255, message = "Длина сообщения не должна превышать 255 символов")
    private String content;

    @Schema(description = "Имя отправителя", example = "student_bsuir")
    @NotBlank(message = "Имя отправителя обязательно")
    private String sender;

    @Schema(description = "ID чата", example = "1")
    @NotNull(message = "ID чата обязателен")
    private Long chatId;

    @Schema(description = "Время отправки")
    private LocalDateTime timestamp;
}