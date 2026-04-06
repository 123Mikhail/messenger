package com.example.messenger.service.impl;

import com.example.messenger.domain.dto.MessageDto;
import com.example.messenger.domain.dto.MessageSearchKey;
import com.example.messenger.domain.model.Message;
import com.example.messenger.mapper.MessageMapper;
import com.example.messenger.repository.MessageRepository;
import com.example.messenger.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {

    private final MessageRepository repository;
    private final MessageMapper mapper;

    // IN-MEMORY КЭШ
    private final Map<MessageSearchKey, Page<MessageDto>> messageCache = new ConcurrentHashMap<>();

    // ИНВАЛИДАЦИЯ КЭША
    private void invalidateCache() {
        log.info("ИНВАЛИДАЦИЯ КЭША: очистка {} записей из-за изменения данных.", messageCache.size());
        messageCache.clear();
    }

    @Override
    public MessageDto save(MessageDto dto) {
        // Искусственный конфликт для генерации 409 ошибки
        if ("конфликт".equalsIgnoreCase(dto.getContent())) {
            throw new IllegalStateException("Сообщение заблокировано системой антиспама!");
        }

        Message entity = mapper.toEntity(dto);
        Message savedEntity = repository.save(entity);
        invalidateCache();
        return mapper.toDto(savedEntity);
    }

    @Override
    public MessageDto getById(Long id) {
        Message entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Сообщение с ID " + id + " не найдено"));
        return mapper.toDto(entity);
    }

    @Override
    public List<MessageDto> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList(); // Исправлен Code Smell
    }

    @Override
    public List<MessageDto> getBySender(String sender) {
        return getAll().stream()
                .filter(m -> m.getSender().equals(sender))
                .toList(); // Исправлен Code Smell
    }

    @Override
    public List<MessageDto> getByChatId(Long chatId) {
        return getAll().stream()
                .filter(m -> m.getChatId().equals(chatId))
                .toList(); // Исправлен Code Smell
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

        if (messageCache.containsKey(key)) {
            log.info("Данные взяты из IN-MEMORY КЭША! Ключ: chatTitle={}, keyword={}, page={}", chatTitle, keyword, pageable.getPageNumber());
            return messageCache.get(key);
        }

        log.info("Данных нет в кэше. Делаем запрос в БД (JPQL)...");
        Page<MessageDto> result = repository.searchByChatAndContentJpql(chatTitle, keyword, pageable)
                .map(mapper::toDto);

        messageCache.put(key, result);
        return result;
    }

    @Override
    public Page<MessageDto> searchMessagesNative(String chatTitle, String keyword, Pageable pageable) {
        log.info("Вызов Native Query (без кэша для сравнения производительности)");
        return repository.searchByChatAndContentNative(chatTitle, keyword, pageable)
                .map(mapper::toDto);
    }
}