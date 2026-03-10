package com.example.messenger.repository;

import com.example.messenger.domain.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByUserUsername(String username);
    List<Message> findByChatId(Long chatId); // НОВЫЙ МЕТОД: поиск по чату
}