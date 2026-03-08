package com.example.messenger.repository;

import com.example.messenger.domain.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // Поиск сообщений по имени пользователя (через связь с сущностью User)
    List<Message> findByUserUsername(String username);

    // Кастомный запрос для поиска по тексту (часто требуют на защите)
    @Query("SELECT m FROM Message m WHERE m.content LIKE %:text%")
    List<Message> searchByText(@Param("text") String text);
}