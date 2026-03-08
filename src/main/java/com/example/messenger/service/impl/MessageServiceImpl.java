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

        // 1. Находим отправителя
        User user = userRepository.findByUsername(dto.getSender())
                .orElseThrow(() -> new RuntimeException("User not found: " + dto.getSender()));

        // 2. Находим чат (диалог)
        Chat chat = chatRepository.findById(dto.getChatId())
                .orElseThrow(() -> new RuntimeException("Chat not found: " + dto.getChatId()));

        // 3. ПРОВЕРКА БЕЗОПАСНОСТИ: Состоит ли пользователь в этом чате?
        if (!chat.getMembers().contains(user)) {
            throw new RuntimeException("Отказано в доступе: Пользователь " + user.getUsername() +
                    " не состоит в чате с ID " + chat.getId());
        }

        // 4. Привязываем сообщение и к юзеру, и к чату
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
        return repository.findByUserUsername(sender).stream()
                .map(mapper::toDto)
                .toList();
    }
}