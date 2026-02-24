package com.example.messenger.service;

import com.example.messenger.domain.dto.MessageDto;
import java.util.List;

public interface MessageService {
    MessageDto save(MessageDto dto);
    MessageDto getById(Long id);
    List<MessageDto> getBySender(String sender);
}