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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository repository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MessageMapper mapper;


    private final Map<MessageSearchKey, Page<MessageDto>> messageCache = new ConcurrentHashMap<>();



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
        return repository.searchByChatAndContentNative(chatTitle, keyword, pageable).map(mapper::toDto);
    }



    private void invalidateCache() {
        log.info("ИНВАЛИДАЦИЯ КЭША: очистка {} записей из-за изменения данных.", messageCache.size());
        messageCache.clear();
    }



    @Override
    @Transactional
    public MessageDto save(MessageDto dto) {
        Message entity = mapper.toEntity(dto);
        User user = userRepository.findByUsername(dto.getSender())
                .orElseThrow(() -> new IllegalArgumentException("Отправитель не найден: " + dto.getSender()));
        Chat chat = chatRepository.findById(dto.getChatId())
                .orElseThrow(() -> new IllegalArgumentException("Чат не найден: " + dto.getChatId()));

        if (!chat.getMembers().contains(user)) {
            throw new IllegalStateException("Отказано в доступе: Пользователь не состоит в чате");
        }

        entity.setUser(user);
        entity.setChat(chat);
        entity.setTimestamp(LocalDateTime.now());

        invalidateCache(); // СБРОС КЭША
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public MessageDto getById(Long id) {
        return repository.findById(id).map(mapper::toDto).orElse(null);
    }

    @Override
    public List<MessageDto> getBySender(String sender) {
        return repository.findByUserUsername(sender).stream().map(mapper::toDto).toList();
    }

    @Override
    public List<MessageDto> getByChatId(Long chatId) {
        return repository.findByChatId(chatId).stream().map(mapper::toDto).toList();
    }

    @Override
    public List<MessageDto> getAll() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    @Transactional
    public MessageDto updateMessage(Long id, String newContent) {
        Message message = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Сообщение не найдено"));
        message.setContent(newContent);

        invalidateCache(); // СБРОС КЭША
        return mapper.toDto(repository.save(message));
    }

    @Override
    @Transactional
    public void deleteMessage(Long id) {
        Message message = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Сообщение не найдено"));
        repository.delete(message);

        invalidateCache(); // СБРОС КЭША
    }
}