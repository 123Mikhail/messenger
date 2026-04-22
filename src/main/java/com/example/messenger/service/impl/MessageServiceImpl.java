package com.example.messenger.service.impl;

import com.example.messenger.domain.dto.MessageDto;
import com.example.messenger.domain.dto.MessageSearchKey;
import com.example.messenger.domain.model.Chat;
import com.example.messenger.domain.model.Message;
import com.example.messenger.domain.model.User;
import com.example.messenger.mapper.MessageMapper;
import com.example.messenger.repository.ChatRepository;
import com.example.messenger.repository.MessageRepository;
import com.example.messenger.repository.UserRepository;
import com.example.messenger.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {

    private final MessageRepository repository;
    private final MessageMapper mapper;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    private final Map<MessageSearchKey, Page<MessageDto>> messageCache = new ConcurrentHashMap<>();

    private void invalidateCache() {
        messageCache.clear();
    }

    @Override
    public MessageDto save(MessageDto dto) {
        if (dto.getChatId() == null || dto.getSender() == null) {
            throw new IllegalArgumentException("Ошибка: chatId и sender обязательны для заполнения!");
        }

        if ("конфликт".equalsIgnoreCase(dto.getContent())) {
            throw new IllegalStateException("Сообщение заблокировано системой антиспама!");
        }

        dto.setId(null);
        dto.setTimestamp(LocalDateTime.now());

        Message entity = mapper.toEntity(dto);

        Chat chat = chatRepository.findById(dto.getChatId())
                .orElseThrow(() -> new IllegalArgumentException("Чат с ID " + dto.getChatId() + " не найден"));
        entity.setChat(chat);

        User user = userRepository.findByUsername(dto.getSender())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь " + dto.getSender() + " не найден"));
        entity.setUser(user);

        Message savedEntity = repository.save(entity);
        invalidateCache();
        return mapper.toDto(savedEntity);
    }

    @Transactional
    @Override
    public List<MessageDto> saveAll(List<MessageDto> dtos) {
        log.info("Начало массового сохранения {} сообщений", dtos != null ? dtos.size() : 0);

        return Optional.ofNullable(dtos)
                .orElse(Collections.emptyList())
                .stream()
                .map(this::save)
                .toList();
    }

    @Override
    public MessageDto getById(Long id) {
        Message entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Сообщение с ID " + id + " не найдено"));
        return mapper.toDto(entity);
    }

    @Override
    public List<MessageDto> getAll() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    public List<MessageDto> getBySender(String sender) {
        return getAll().stream().filter(m -> m.getSender().equals(sender)).toList();
    }

    @Override
    public List<MessageDto> getByChatId(Long chatId) {
        return getAll().stream().filter(m -> m.getChatId().equals(chatId)).toList();
    }

    @Override
    public MessageDto updateMessage(Long id, String newContent) {
        Message entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Сообщение с ID " + id + " не найдено"));
        entity.setContent(newContent);
        Message updatedEntity = repository.save(entity);
        invalidateCache();
        return mapper.toDto(updatedEntity);
    }

    @Override
    public void deleteMessage(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Сообщение с ID " + id + " не найдено");
        }
        repository.deleteById(id);
        invalidateCache();
    }

    @Override
    public Page<MessageDto> searchMessagesJpql(String chatTitle, String keyword, Pageable pageable) {
        MessageSearchKey key = new MessageSearchKey(chatTitle, keyword, pageable.getPageNumber(), pageable.getPageSize());
        if (messageCache.containsKey(key)) {return messageCache.get(key);}
        Page<MessageDto> result = repository.searchByChatAndContentJpql(chatTitle, keyword, pageable).map(mapper::toDto);
        messageCache.put(key, result);
        return result;
    }

    @Override
    public Page<MessageDto> searchMessagesNative(String chatTitle, String keyword, Pageable pageable) {
        return repository.searchByChatAndContentNative(chatTitle, keyword, pageable).map(mapper::toDto);
    }
}