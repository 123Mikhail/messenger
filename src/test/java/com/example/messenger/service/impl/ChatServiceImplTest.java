package com.example.messenger.service.impl;

import com.example.messenger.domain.model.Chat;
import com.example.messenger.domain.model.User;
import com.example.messenger.repository.ChatRepository;
import com.example.messenger.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceImplTest {

    @Mock private ChatRepository chatRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private ChatServiceImpl chatService;

    @Test
    void createChat_Success_WithoutParent() {
        when(userRepository.findByUsername("User1")).thenReturn(Optional.of(new User()));
        when(chatRepository.save(any(Chat.class))).thenReturn(new Chat());

        assertNotNull(chatService.createChat("Title", Collections.singletonList("User1"), null));
    }

    @Test
    void createChat_Success_WithParent() {
        when(userRepository.findByUsername("User1")).thenReturn(Optional.of(new User()));
        when(chatRepository.findById(2L)).thenReturn(Optional.of(new Chat()));
        when(chatRepository.save(any(Chat.class))).thenReturn(new Chat());

        assertNotNull(chatService.createChat("Title", Collections.singletonList("User1"), 2L));
    }

    @Test
    void createChat_EmptyUsernamesList() {
        // Проверяем условие, когда список юзеров пустой (закрываем скрытую ветку Stream API)
        when(chatRepository.save(any(Chat.class))).thenReturn(new Chat());
        assertNotNull(chatService.createChat("Title", Collections.emptyList(), null));
    }

    @Test
    void createChat_UserNotFound() {
        when(userRepository.findByUsername("Ghost")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> chatService.createChat("Title", List.of("Ghost"), null));
    }

    @Test
    void createChat_ParentNotFound() {
        when(userRepository.findByUsername("User1")).thenReturn(Optional.of(new User()));
        when(chatRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> chatService.createChat("Title", List.of("User1"), 2L));
    }

    @Test
    void addUserToChat_Success() {
        Chat chat = new Chat();
        chat.setMembers(new ArrayList<>());
        User user = new User();

        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));
        when(userRepository.findByUsername("User1")).thenReturn(Optional.of(user));

        chatService.addUserToChat(1L, "User1");
        assertTrue(chat.getMembers().contains(user));
        verify(chatRepository).save(chat);
    }

    @Test
    void addUserToChat_AlreadyMember() {
        Chat chat = new Chat();
        User user = new User();
        chat.setMembers(new ArrayList<>(List.of(user)));

        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));
        when(userRepository.findByUsername("User1")).thenReturn(Optional.of(user));

        chatService.addUserToChat(1L, "User1");
        verify(chatRepository, never()).save(any());
    }

    @Test
    void removeUserFromChat_Success() {
        Chat chat = new Chat();
        User user = new User();
        chat.setMembers(new ArrayList<>(List.of(user)));

        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));
        when(userRepository.findByUsername("User1")).thenReturn(Optional.of(user));

        chatService.removeUserFromChat(1L, "User1");
        assertFalse(chat.getMembers().contains(user));
        verify(chatRepository).save(chat);
    }

    @Test
    void removeUserFromChat_NotMember() {
        Chat chat = new Chat();
        chat.setMembers(new ArrayList<>());
        User user = new User();

        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));
        when(userRepository.findByUsername("User1")).thenReturn(Optional.of(user));

        chatService.removeUserFromChat(1L, "User1");
        verify(chatRepository, never()).save(any());
    }

    @Test
    void deleteChat_Success() {
        when(chatRepository.findById(1L)).thenReturn(Optional.of(new Chat()));
        chatService.deleteChat(1L);
        verify(chatRepository).delete(any(Chat.class));
    }

    @Test
    void updateChatTitle_Success() {
        Chat chat = new Chat();
        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));
        when(chatRepository.save(chat)).thenReturn(chat);

        chatService.updateChatTitle(1L, "New");
        assertEquals("New", chat.getTitle());
    }

    @Test
    void getSubChats_Success() {
        Chat parent = new Chat();
        parent.setSubChats(Collections.emptyList());
        when(chatRepository.findById(1L)).thenReturn(Optional.of(parent));

        assertNotNull(chatService.getSubChats(1L));
    }

    @Test
    void getById_Success() {
        when(chatRepository.findById(1L)).thenReturn(Optional.of(new Chat()));
        assertNotNull(chatService.getById(1L));
    }

    @Test
    void getById_NotFound() {
        // Закрываем скрытую ветку orElse(null)
        when(chatRepository.findById(1L)).thenReturn(Optional.empty());
        assertNull(chatService.getById(1L));
    }

    @Test
    void getAllChats_Success() {
        when(chatRepository.findAll()).thenReturn(Collections.emptyList());
        assertNotNull(chatService.getAllChats());
    }

    @Test
    void common_ChatNotFound() {
        when(chatRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> chatService.addUserToChat(99L, "user"));
        assertThrows(IllegalArgumentException.class, () -> chatService.removeUserFromChat(99L, "user"));
        assertThrows(IllegalArgumentException.class, () -> chatService.deleteChat(99L));
        assertThrows(IllegalArgumentException.class, () -> chatService.updateChatTitle(99L, "title"));
        assertThrows(IllegalArgumentException.class, () -> chatService.getSubChats(99L));
    }

    @Test
    void common_UserNotFound() {
        when(chatRepository.findById(1L)).thenReturn(Optional.of(new Chat()));
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> chatService.addUserToChat(1L, "ghost"));
        assertThrows(IllegalArgumentException.class, () -> chatService.removeUserFromChat(1L, "ghost"));
    }
}