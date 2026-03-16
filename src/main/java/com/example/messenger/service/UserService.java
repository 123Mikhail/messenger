package com.example.messenger.service;

import com.example.messenger.domain.model.User;
import java.util.List;

public interface UserService {
    User createUser(User user);
    User updateUsername(Long userId, String newUsername);
    void deleteUser(Long userId);

    // НОВЫЕ МЕТОДЫ: Получение пользователей
    List<User> getAllUsers();
    User getUserById(Long userId);
}