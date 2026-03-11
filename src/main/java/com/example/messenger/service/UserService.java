package com.example.messenger.service;

import com.example.messenger.domain.model.User;

public interface UserService {
    User createUser(User user);
    User updateUsername(Long userId, String newUsername);
    void deleteUser(Long userId);
}