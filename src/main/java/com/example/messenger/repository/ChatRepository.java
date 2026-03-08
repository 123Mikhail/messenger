package com.example.messenger.repository;

import com.example.messenger.domain.model.Chat;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    // РЕШЕНИЕ ПРОБЛЕМЫ N+1 (Требование №5)
    // EntityGraph заставляет Hibernate сразу загрузить коллекцию members одним SQL-запросом
    @EntityGraph(attributePaths = {"members"})
    @Query("SELECT c FROM Chat c")
    List<Chat> findAllWithMembers();
}