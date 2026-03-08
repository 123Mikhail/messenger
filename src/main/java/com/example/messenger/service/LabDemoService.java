package com.example.messenger.service;

import com.example.messenger.domain.model.Chat;
import com.example.messenger.domain.model.User;
import com.example.messenger.repository.ChatRepository;
import com.example.messenger.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LabDemoService {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    /**
     * ДЕМОНСТРАЦИЯ ДЛЯ ЗАЩИТЫ (Без транзакции)
     * Если убрать @Transactional, то User сохранится в БД,
     * а потом вылетит ошибка, и Chat НЕ сохранится. Возникнет мусор в базе (частичное сохранение).
     */
    // @Transactional <-- ЗАКОММЕНТИРОВАНО ДЛЯ ТЕСТА
    public void saveDataWithoutTransaction() {
        User user = User.builder().username("hacker_no_tx").email("fail@mail.com").build();
        userRepository.save(user); // Сохранится успешно

        // Имитируем непредвиденную ошибку сервера
        if (true) throw new RuntimeException("Искусственная ошибка во время сохранения!");

        Chat chat = Chat.builder().title("Секретный чат").createdAt(LocalDateTime.now()).build();
        chatRepository.save(chat); // До сюда код не дойдет
    }

    /**
     * ДЕМОНСТРАЦИЯ ДЛЯ ЗАЩИТЫ (С транзакцией)
     * Благодаря @Transactional, из-за ошибки в середине метода,
     * база данных сделает ROLLBACK. User не появится в БД. База останется целостной.
     */
    @Transactional
    public void saveDataWithTransaction() {
        User user = User.builder().username("hacker_with_tx").email("safe@mail.com").build();
        userRepository.save(user);

        // Имитируем ошибку
        if (true) throw new RuntimeException("Ошибка, но транзакция всё откатит!");

        Chat chat = Chat.builder().title("Транзакционный чат").createdAt(LocalDateTime.now()).build();
        chatRepository.save(chat);
    }
}