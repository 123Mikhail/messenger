package com.example.messenger.service;

import com.example.messenger.domain.model.Chat;
import com.example.messenger.domain.model.User;
import com.example.messenger.repository.ChatRepository;
import com.example.messenger.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LabDemoService {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;


    public void saveDataWithoutTransaction() {
        User user = User.builder().username("hacker_no_tx").email("fail@mail.com").build();
        userRepository.save(user);


        if (true) {
            throw new IllegalStateException("Ошибка сервера! Но юзер уже в БД, так как нет транзакции.");
        }

        Chat chat = Chat.builder().title("Чат без транзакции").createdAt(LocalDateTime.now()).build();
        chatRepository.save(chat);
    }

    @Transactional
    public void saveDataWithTransaction() {
        User user = User.builder().username("hacker_with_tx").email("safe@mail.com").build();
        userRepository.save(user);

        if (true) {
            throw new IllegalStateException("Ошибка! Сработает ROLLBACK, и юзер исчезнет из БД.");
        }

        Chat chat = Chat.builder().title("Транзакционный чат").createdAt(LocalDateTime.now()).build();
        chatRepository.save(chat);
    }



    @Transactional(readOnly = true)
    public void demonstrateNPlusOneProblem() {
        log.info("========== ПРОБЛЕМА N+1 (НАЧАЛО) ==========");

        List<Chat> chats = chatRepository.findAll();

        for (Chat chat : chats) {

            int messagesCount = chat.getMessages().size();
            log.info("Чат {} содержит {} сообщений", chat.getId(), messagesCount);
        }
        log.info("========== ПРОБЛЕМА N+1 (КОНЕЦ) ==========");
    }

    @Transactional(readOnly = true)
    public void demonstrateNPlusOneSolution() {
        log.info("========== РЕШЕНИЕ N+1 (НАЧАЛО) ==========");

        List<Chat> chats = chatRepository.findAllWithMessages();

        for (Chat chat : chats) {

            int messagesCount = chat.getMessages().size();
            log.info("Чат {} содержит {} сообщений", chat.getId(), messagesCount);
        }
        log.info("========== РЕШЕНИЕ N+1 (КОНЕЦ) ==========");
    }
}