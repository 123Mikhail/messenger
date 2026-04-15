package com.example.messenger.exception;

import com.example.messenger.domain.dto.ApiErrorResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ApiResponse(responseCode = "400", description = "Ошибка валидации данных")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return buildResponse(HttpStatus.BAD_REQUEST, "Ошибка валидации данных", errors);
    }

    @ApiResponse(responseCode = "400", description = "Некорректный синтаксис JSON")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Ошибка парсинга JSON: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "Ошибка в синтаксисе JSON (проверьте лишние запятые или кавычки)", null);
    }

    @ApiResponse(responseCode = "400", description = "Ошибка сохранения в БД")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String realCause = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
        log.error("Реальная ошибка БД: {}", realCause);
        return buildResponse(HttpStatus.BAD_REQUEST, "Ошибка сохранения в БД: " + realCause, null);
    }

    @ApiResponse(responseCode = "409", description = "Конфликт бизнес-логики")
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    @ApiResponse(responseCode = "404", description = "Ресурс не найден")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleAllOtherExceptions(Exception ex) {
        log.error("НЕПРЕДВИДЕННАЯ ОШИБКА: ", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Произошла непредвиденная ошибка на сервере", null);
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String message, Map<String, String> validationErrors) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .message(message)
                .validationErrors(validationErrors)
                .build();
        return new ResponseEntity<>(response, status);
    }
}