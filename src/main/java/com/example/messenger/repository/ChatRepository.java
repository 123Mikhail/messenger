package com.example.messenger.repository;

import com.example.messenger.domain.model.Chat;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {


    @EntityGraph(attributePaths = {"messages"})
    @Query("SELECT c FROM Chat c")
    List<Chat> findAllWithMessages();
}