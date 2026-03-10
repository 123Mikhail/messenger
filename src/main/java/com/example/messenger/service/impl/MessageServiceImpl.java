package com.example.messenger.service.impl;

import com.example.messenger.domain.dto.MessageDto;
import com.example.messenger.domain.model.Chat;
import com.example.messenger.domain.model.Message;
import com.example.messenger.domain.model.User;
import com.example.messenger.mapper.MessageMapper;
import com.example.messenger.repository.ChatRepository;
import com.example.messenger.repository.MessageRepository;
import com.example.messenger.repository.UserRepository;
import com.example.messenger.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository repository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MessageMapper mapper;

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

    // --- НОВЫЕ МЕТОДЫ ДЛЯ ЧТЕНИЯ И ОБНОВЛЕНИЯ ---

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
        return mapper.toDto(repository.save(message));
    }
}