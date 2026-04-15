package com.example.messenger.repository;

import com.example.messenger.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Этот метод необходим для поиска автора сообщения
    Optional<User> findByUsername(String username);
}