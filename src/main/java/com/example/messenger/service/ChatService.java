package com.example.messenger.service;

import com.example.messenger.domain.model.Chat;
import java.util.List;

public interface ChatService {
    Chat createChat(String title, List<String> usernames, Long parentId);
    void addUserToChat(Long chatId, String username);
    List<Chat> getSubChats(Long parentId);
    Chat getById(Long id);

    // НОВЫЕ МЕТОДЫ ДЛЯ УДАЛЕНИЯ
    void removeUserFromChat(Long chatId, String username);
    void deleteChat(Long chatId);
}