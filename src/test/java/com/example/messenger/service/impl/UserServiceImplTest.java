package com.example.messenger.service.impl;

import com.example.messenger.domain.model.Chat;
import com.example.messenger.domain.model.User;
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
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_Success() {
        User user = new User();
        when(userRepository.save(user)).thenReturn(user);
        assertNotNull(userService.createUser(user));
    }

    @Test
    void updateUsername_Success() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        User result = userService.updateUsername(1L, "NewName");
        assertEquals("NewName", user.getUsername());
        assertNotNull(result);
    }

    @Test
    void updateUsername_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> userService.updateUsername(1L, "Name"));
    }

    @Test
    void deleteUser_Success_WithChats() {
        User user = new User();
        Chat chat = new Chat();
        List<User> members = new ArrayList<>();
        members.add(user);
        chat.setMembers(members);

        List<Chat> chats = new ArrayList<>();
        chats.add(chat);
        user.setChats(chats);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.deleteUser(1L);

        assertFalse(chat.getMembers().contains(user));
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void getAllUsers_Success() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(new User()));
        assertFalse(userService.getAllUsers().isEmpty());
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        assertNotNull(userService.getUserById(1L));
    }

    @Test
    void getUserById_NotFound_ReturnsNull() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertNull(userService.getUserById(1L));
    }
}