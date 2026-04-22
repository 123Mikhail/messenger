package com.example.messenger.service.impl;

import com.example.messenger.domain.dto.MessageDto;
import com.example.messenger.domain.model.Chat;
import com.example.messenger.domain.model.Message;
import com.example.messenger.domain.model.User;
import com.example.messenger.mapper.MessageMapper;
import com.example.messenger.repository.ChatRepository;
import com.example.messenger.repository.MessageRepository;
import com.example.messenger.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private MessageMapper messageMapper;
    @Mock
    private ChatRepository chatRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MessageServiceImpl messageService;

    private MessageDto validDto;
    private Chat chat;
    private User user;
    private Message messageEntity;

    @BeforeEach
    void setUp() {
        validDto = new MessageDto();
        validDto.setContent("Тестовое сообщение");
        validDto.setChatId(1L);
        validDto.setSender("student_bsuir");

        chat = new Chat();
        chat.setId(1L);

        user = new User();
        user.setUsername("student_bsuir");

        messageEntity = new Message();
        messageEntity.setContent("Тестовое сообщение");
    }

    @Test
    void save_Successful() {
        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));
        when(userRepository.findByUsername("student_bsuir")).thenReturn(Optional.of(user));
        when(messageMapper.toEntity(validDto)).thenReturn(messageEntity);
        when(messageRepository.save(any(Message.class))).thenReturn(messageEntity);
        when(messageMapper.toDto(messageEntity)).thenReturn(validDto);

        MessageDto result = messageService.save(validDto);

        assertNotNull(result);
        assertEquals("Тестовое сообщение", result.getContent());
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    void save_ThrowsAntiSpamException() {
        validDto.setContent("конфликт");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            messageService.save(validDto);
        });

        assertEquals("Сообщение заблокировано системой антиспама!", exception.getMessage());
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    void saveAll_SuccessfulBulkOperation() {
        MessageDto dto2 = new MessageDto();
        dto2.setContent("Второе сообщение");
        dto2.setChatId(1L);
        dto2.setSender("student_bsuir");

        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));
        when(userRepository.findByUsername("student_bsuir")).thenReturn(Optional.of(user));
        when(messageMapper.toEntity(any())).thenReturn(messageEntity);
        when(messageRepository.save(any(Message.class))).thenReturn(messageEntity);
        when(messageMapper.toDto(any())).thenReturn(validDto);

        List<MessageDto> result = messageService.saveAll(Arrays.asList(validDto, dto2));

        assertEquals(2, result.size());
        verify(messageRepository, times(2)).save(any(Message.class));
    }

    @Test
    void saveAll_HandlesNullListGracefully() {
        List<MessageDto> result = messageService.saveAll(null);
        assertTrue(result.isEmpty());
        verify(messageRepository, never()).save(any());
    }
}