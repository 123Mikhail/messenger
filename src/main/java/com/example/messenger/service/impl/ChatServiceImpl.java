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

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Chat createChat(String title, List<String> usernames, Long parentId) {
        List<User> members = usernames.stream()
                .map(name -> userRepository.findByUsername(name)
                        .orElseThrow(() -> new RuntimeException("Пользователь " + name + " не найден")))
                .toList();

        Chat chat = Chat.builder()
                .title(title)
                .createdAt(LocalDateTime.now())
                .members(members)
                .build();

        if (parentId != null) {
            Chat parent = chatRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Родительский чат не найден"));
            chat.setParentChat(parent);
        }

        return chatRepository.save(chat);
    }

    @Override
    @Transactional
    public void addUserToChat(Long chatId, String username) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Чат не найден"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!chat.getMembers().contains(user)) {
            chat.getMembers().add(user);
            chatRepository.save(chat);
        }
    }

    @Override
    public List<Chat> getSubChats(Long parentId) {
        Chat parent = chatRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Чат не найден"));
        return parent.getSubChats();
    }

    @Override
    public Chat getById(Long id) {
        return chatRepository.findById(id).orElse(null);
    }

    // НОВЫЙ МЕТОД: Удаление пользователя из чата
    @Override
    @Transactional
    public void removeUserFromChat(Long chatId, String username) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Чат не найден"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (chat.getMembers().contains(user)) {
            chat.getMembers().remove(user);
            chatRepository.save(chat); // Hibernate автоматически удалит связь из таблицы chat_members
        }
    }

    // НОВЫЙ МЕТОД: Удаление самого чата (и всех его подчатов)
    @Override
    @Transactional
    public void deleteChat(Long chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Чат не найден"));

        // Благодаря CascadeType.ALL в сущности Chat, удаление этого объекта
        // приведет к удалению всех связанных сообщений и подчатов из БД.
        chatRepository.delete(chat);
    }
}