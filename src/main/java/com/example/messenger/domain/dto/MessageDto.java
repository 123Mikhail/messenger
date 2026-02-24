package com.example.messenger.domain.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageDto {
    private Long id;
    private String sender;
    private String content;
    private LocalDateTime timestamp;
}