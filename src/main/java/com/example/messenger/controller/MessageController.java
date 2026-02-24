package com.example.messenger.controller;

import com.example.messenger.domain.dto.MessageDto;
import com.example.messenger.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping
    public MessageDto create(@RequestBody MessageDto dto) {
        return messageService.save(dto);
    }

    @GetMapping("/{id}")
    public MessageDto getById(@PathVariable Long id) {
        return messageService.getById(id);
    }

    @GetMapping
    public List<MessageDto> getBySender(@RequestParam String sender) {
        return messageService.getBySender(sender);
    }
}