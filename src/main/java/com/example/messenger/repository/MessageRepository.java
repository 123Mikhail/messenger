package com.example.messenger.repository;

import com.example.messenger.domain.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByUserUsername(String username);

    List<Message> findByChatId(Long chatId);

    // 1. Сложный запрос через JPQL (фильтрация по вложенной сущности Chat)
    @Query("SELECT m FROM Message m JOIN m.chat c WHERE c.title LIKE %:chatTitle% AND m.content LIKE %:keyword%")
    Page<Message> searchByChatAndContentJpql(
            @Param("chatTitle") String chatTitle,
            @Param("keyword") String keyword,
            Pageable pageable);

    // 2. Аналогичный запрос через Native Query (чистый SQL)
    @Query(value = "SELECT m.* FROM messages m INNER JOIN chats c ON m.chat_id = c.id WHERE c.title ILIKE %:chatTitle% AND m.content ILIKE %:keyword%",
            countQuery = "SELECT count(*) FROM messages m INNER JOIN chats c ON m.chat_id = c.id WHERE c.title ILIKE %:chatTitle% AND m.content ILIKE %:keyword%",
            nativeQuery = true)
    Page<Message> searchByChatAndContentNative(
            @Param("chatTitle") String chatTitle,
            @Param("keyword") String keyword,
            Pageable pageable);
}