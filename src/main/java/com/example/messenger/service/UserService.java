package com.example.messenger.service;

import com.example.messenger.domain.model.User;

public interface UserService {
    User updateUsername(Long userId, String newUsername);
}