package com.example.messenger.service.impl;

import com.example.messenger.domain.model.Chat;
import com.example.messenger.domain.model.User;
import com.example.messenger.repository.ChatRepository;
import com.example.messenger.repository.UserRepository;
import com.example.messenger.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    // Выносим повторяющиеся строки в константы для SonarCloud
    private static final String CHAT_NOT_FOUND = "Чат не найден";
    private static final String USER_NOT_FOUND = "Пользователь не найден";

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Chat createChat(String title, List<String> usernames, Long parentId) {
        List<User> members = usernames.stream()
                .map(name -> userRepository.findByUsername(name)
                        .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND + ": " + name)))
                .toList();

        Chat chat = Chat.builder()
                .title(title)
                .createdAt(LocalDateTime.now())
                .members(members)
                .build();

        if (parentId != null) {
            Chat parent = chatRepository.findById(parentId)
                    .orElseThrow(() -> new IllegalArgumentException("Родительский чат не найден"));
            chat.setParentChat(parent);
        }

        return chatRepository.save(chat);
    }

    @Override
    @Transactional
    public void addUserToChat(Long chatId, String username) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException(CHAT_NOT_FOUND));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));

        if (!chat.getMembers().contains(user)) {
            chat.getMembers().add(user);
            chatRepository.save(chat);
        }
    }

    @Override
    public List<Chat> getSubChats(Long parentId) {
        Chat parent = chatRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException(CHAT_NOT_FOUND));
        return parent.getSubChats();
    }

    @Override
    public Chat getById(Long id) {
        return chatRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void removeUserFromChat(Long chatId, String username) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException(CHAT_NOT_FOUND));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));

        if (chat.getMembers().contains(user)) {
            chat.getMembers().remove(user);
            chatRepository.save(chat);
        }
    }

    @Override
    @Transactional
    public void deleteChat(Long chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException(CHAT_NOT_FOUND));

        chatRepository.delete(chat);
    }
}