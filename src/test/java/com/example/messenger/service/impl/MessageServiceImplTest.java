package com.example.messenger.service.impl;

import com.example.messenger.domain.dto.MessageDto;
import com.example.messenger.domain.model.Chat;
import com.example.messenger.domain.model.Message;
import com.example.messenger.domain.model.User;
import com.example.messenger.mapper.MessageMapper;
import com.example.messenger.repository.ChatRepository;
import com.example.messenger.repository.MessageRepository;
import com.example.messenger.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

    @Mock private MessageRepository repository;
    @Mock private MessageMapper mapper;
    @Mock private ChatRepository chatRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private MessageServiceImpl messageService;

    @Test
    void save_Success() {
        MessageDto dto = new MessageDto();
        dto.setChatId(1L);
        dto.setSender("User");
        dto.setContent("Hello");

        when(mapper.toEntity(any())).thenReturn(new Message());
        when(chatRepository.findById(1L)).thenReturn(Optional.of(new Chat()));
        when(userRepository.findByUsername("User")).thenReturn(Optional.of(new User()));
        when(repository.save(any())).thenReturn(new Message());
        when(mapper.toDto(any())).thenReturn(dto);

        assertNotNull(messageService.save(dto));
    }

    @Test
    void save_MissingFields_ThrowsException() {
        // Обе переменные null
        assertThrows(IllegalArgumentException.class, () -> messageService.save(new MessageDto()));

        // chatId есть, sender null (Это то самое условие, которого не хватало для 100%!)
        MessageDto dto2 = new MessageDto();
        dto2.setChatId(1L);
        assertThrows(IllegalArgumentException.class, () -> messageService.save(dto2));
    }

    @Test
    void save_SpamConflict_ThrowsException() {
        MessageDto dto = new MessageDto();
        dto.setChatId(1L);
        dto.setSender("User");
        dto.setContent("конфликт");
        assertThrows(IllegalStateException.class, () -> messageService.save(dto));
    }

    @Test
    void save_ChatNotFound_ThrowsException() {
        MessageDto dto = new MessageDto();
        dto.setChatId(1L);
        dto.setSender("User");
        when(mapper.toEntity(any())).thenReturn(new Message());
        when(chatRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> messageService.save(dto));
    }

    @Test
    void save_UserNotFound_ThrowsException() {
        MessageDto dto = new MessageDto();
        dto.setChatId(1L);
        dto.setSender("User");
        when(mapper.toEntity(any())).thenReturn(new Message());
        when(chatRepository.findById(1L)).thenReturn(Optional.of(new Chat()));
        when(userRepository.findByUsername("User")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> messageService.save(dto));
    }

    @Test
    void saveAll_HandleList() {
        assertTrue(messageService.saveAll(null).isEmpty());

        MessageDto dto = new MessageDto();
        dto.setChatId(1L);
        dto.setSender("User");
        when(mapper.toEntity(any())).thenReturn(new Message());
        when(chatRepository.findById(1L)).thenReturn(Optional.of(new Chat()));
        when(userRepository.findByUsername("User")).thenReturn(Optional.of(new User()));

        messageService.saveAll(Collections.singletonList(dto));
        verify(repository).save(any());
    }

    @Test
    void getById_Success_And_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(new Message()));
        when(mapper.toDto(any())).thenReturn(new MessageDto());
        assertNotNull(messageService.getById(1L));

        when(repository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> messageService.getById(2L));
    }

    @Test
    void getAll_And_Filters() {
        MessageDto dto = new MessageDto();
        dto.setSender("User");
        dto.setChatId(1L);

        when(repository.findAll()).thenReturn(Collections.singletonList(new Message()));
        when(mapper.toDto(any())).thenReturn(dto);

        assertFalse(messageService.getAll().isEmpty());
        assertFalse(messageService.getBySender("User").isEmpty());
        assertFalse(messageService.getByChatId(1L).isEmpty());
    }

    @Test
    void updateMessage_Success() {
        Message message = new Message();
        when(repository.findById(1L)).thenReturn(Optional.of(message));
        when(repository.save(message)).thenReturn(message);
        when(mapper.toDto(message)).thenReturn(new MessageDto());

        assertNotNull(messageService.updateMessage(1L, "New"));
        assertEquals("New", message.getContent());
    }

    @Test
    void deleteMessage_Success_And_NotFound() {
        when(repository.existsById(1L)).thenReturn(true);
        messageService.deleteMessage(1L);
        verify(repository).deleteById(1L);

        when(repository.existsById(2L)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> messageService.deleteMessage(2L));
    }

    @Test
    void searchMessages_Jpql_And_Native() {
        Page<Message> page = new PageImpl<>(Collections.singletonList(new Message()));
        when(repository.searchByChatAndContentJpql(any(), any(), any())).thenReturn(page);
        when(mapper.toDto(any())).thenReturn(new MessageDto());

        assertNotNull(messageService.searchMessagesJpql("Chat", "Key", PageRequest.of(0, 10)));
        assertNotNull(messageService.searchMessagesJpql("Chat", "Key", PageRequest.of(0, 10)));

        when(repository.searchByChatAndContentNative(any(), any(), any())).thenReturn(page);
        assertNotNull(messageService.searchMessagesNative("Chat", "Key", PageRequest.of(0, 10)));
    }
}