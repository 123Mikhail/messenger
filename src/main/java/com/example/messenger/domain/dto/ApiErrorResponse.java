package com.example.messenger.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Builder
@Schema(description = "Единый формат ответа при ошибках API")
public class ApiErrorResponse {

    @Schema(description = "Время возникновения ошибки")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP статус код", example = "400")
    private int status;

    @Schema(description = "Общее сообщение об ошибке", example = "Ошибка валидации данных")
    private String message;

    @Schema(description = "Детализация ошибок по полям (если есть)")
    private Map<String, String> validationErrors;
}