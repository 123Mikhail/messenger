package com.example.messenger.service.impl;

import com.example.messenger.domain.model.Chat;
import com.example.messenger.domain.model.User;
import com.example.messenger.repository.UserRepository;
import com.example.messenger.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUsername(Long userId, String newUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        user.setUsername(newUsername);
        return userRepository.save(user);
    }

    // ИСПРАВЛЕННЫЙ МЕТОД УДАЛЕНИЯ ПОЛЬЗОВАТЕЛЯ
    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        // 1. Отвязываем пользователя от всех чатов,
        // чтобы не было ошибки внешнего ключа в таблице chat_members
        for (Chat chat : user.getChats()) {
            chat.getMembers().remove(user);
        }

        // 2. Теперь безопасно удаляем пользователя
        // (его сообщения удалятся автоматически благодаря CascadeType.ALL в классе User)
        userRepository.delete(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
}