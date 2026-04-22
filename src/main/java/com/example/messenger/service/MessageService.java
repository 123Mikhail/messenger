package com.example.messenger.service;

import com.example.messenger.domain.dto.MessageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MessageService {
    MessageDto save(MessageDto dto);

    List<MessageDto> saveAll(List<MessageDto> dtos);

    MessageDto getById(Long id);
    List<MessageDto> getAll();
    List<MessageDto> getBySender(String sender);
    List<MessageDto> getByChatId(Long chatId);
    MessageDto updateMessage(Long id, String newContent);
    void deleteMessage(Long id);
    Page<MessageDto> searchMessagesJpql(String chatTitle, String keyword, Pageable pageable);
    Page<MessageDto> searchMessagesNative(String chatTitle, String keyword, Pageable pageable);
}