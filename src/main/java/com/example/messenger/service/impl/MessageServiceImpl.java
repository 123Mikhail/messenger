package com.example.messenger.service.impl;

import com.example.messenger.domain.dto.MessageDto;
import com.example.messenger.domain.model.Message;
import com.example.messenger.mapper.MessageMapper;
import com.example.messenger.repository.MessageRepository;
import com.example.messenger.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository repository;
    private final MessageMapper mapper;

    @Override
    public MessageDto save(MessageDto dto) {
        Message entity = mapper.toEntity(dto);
        entity.setTimestamp(LocalDateTime.now());
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public MessageDto getById(Long id) {
        return repository.findById(id).map(mapper::toDto).orElse(null);
    }

    @Override
    public List<MessageDto> getBySender(String sender) {
        return repository.findBySender(sender).stream().map(mapper::toDto).toList();
    }
}