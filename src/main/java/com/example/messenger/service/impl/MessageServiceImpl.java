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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository repository;
    private final MessageMapper mapper;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    @Override
    public MessageDto save(MessageDto dto) {
        dto.setTimestamp(LocalDateTime.now());
        Message entity = mapper.toEntity(dto);
        Chat chat = chatRepository.findById(dto.getChatId()).orElseThrow();
        entity.setChat(chat);
        User user = userRepository.findByUsername(dto.getSender()).orElseThrow();
        entity.setUser(user);
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public List<MessageDto> saveAll(List<MessageDto> dtos) {
        return dtos.stream().map(this::save).toList();
    }

    @Override
    public MessageDto getById(Long id) {
        return mapper.toDto(repository.findById(id).orElseThrow());
    }

    @Override
    public List<MessageDto> getAll() {
        return repository.findAll().stream().map(mapper::toDto).toList();
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
    public Page<MessageDto> getMessagesByChatIdPaged(Long chatId, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("id").descending()
        );
        return repository.findByChatId(chatId, sortedPageable).map(mapper::toDto);
    }

    @Override
    public MessageDto updateMessage(Long id, String newContent) {
        Message entity = repository.findById(id).orElseThrow();
        entity.setContent(newContent);
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public void deleteMessage(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Page<MessageDto> searchMessagesJpql(String chatTitle, String keyword, Pageable pageable) {
        return repository.searchByChatAndContentJpql(chatTitle, keyword, pageable).map(mapper::toDto);
    }

    @Override
    public Page<MessageDto> searchMessagesNative(String chatTitle, String keyword, Pageable pageable) {
        return repository.searchByChatAndContentNative(chatTitle, keyword, pageable).map(mapper::toDto);
    }
}