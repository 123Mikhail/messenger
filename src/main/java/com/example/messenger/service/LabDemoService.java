package com.example.messenger.service;

import com.example.messenger.domain.model.Chat;
import com.example.messenger.domain.model.User;
import com.example.messenger.repository.ChatRepository;
import com.example.messenger.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LabDemoService {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    // ==========================================
    // ДЕМОНСТРАЦИЯ ТРАНЗАКЦИЙ (ОТКАТ ОШИБКИ)
    // ==========================================

    public void saveDataWithoutTransaction() {
        User user = User.builder().username("hacker_no_tx").email("fail@mail.com").build();
        userRepository.save(user); // 1. Юзер сохранится в БД НАВСЕГДА

        // 2. Выбрасываем исключение
        if (true) throw new IllegalStateException("Ошибка сервера! Но юзер уже в БД, так как нет транзакции.");

        Chat chat = Chat.builder().title("Чат без транзакции").createdAt(LocalDateTime.now()).build();
        chatRepository.save(chat); // До сюда код никогда не дойдет
    }

    @Transactional
    public void saveDataWithTransaction() {
        User user = User.builder().username("hacker_with_tx").email("safe@mail.com").build();
        userRepository.save(user); // 1. Юзер "как бы" сохраняется

        // 2. Выбрасываем исключение
        if (true) throw new IllegalStateException("Ошибка! Сработает ROLLBACK, и юзер исчезнет из БД.");

        Chat chat = Chat.builder().title("Транзакционный чат").createdAt(LocalDateTime.now()).build();
        chatRepository.save(chat); // До сюда код никогда не дойдет
    }

    // ==========================================
    // ДЕМОНСТРАЦИЯ ПРОБЛЕМЫ N+1
    // ==========================================

    @Transactional(readOnly = true)
    public void demonstrateNPlusOneProblem() {
        System.out.println("========== ПРОБЛЕМА N+1 (НАЧАЛО) ==========");
        // 1 запрос достает все чаты
        List<Chat> chats = chatRepository.findAll();

        for (Chat chat : chats) {
            // N запросов: для каждого чата Hibernate делает отдельный SELECT в таблицу messages
            int messagesCount = chat.getMessages().size();
            System.out.println("Чат " + chat.getId() + " содержит " + messagesCount + " сообщений");
        }
        System.out.println("========== ПРОБЛЕМА N+1 (КОНЕЦ) ==========");
    }

    @Transactional(readOnly = true)
    public void demonstrateNPlusOneSolution() {
        System.out.println("========== РЕШЕНИЕ N+1 (НАЧАЛО) ==========");
        // Всего 1 запрос достает ВСЁ: и чаты, и их сообщения через JOIN благодаря @EntityGraph
        List<Chat> chats = chatRepository.findAllWithMessages();

        for (Chat chat : chats) {
            // Запросов к БД больше нет, данные уже в оперативной памяти
            int messagesCount = chat.getMessages().size();
            System.out.println("Чат " + chat.getId() + " содержит " + messagesCount + " сообщений");
        }
        System.out.println("========== РЕШЕНИЕ N+1 (КОНЕЦ) ==========");
    }
}