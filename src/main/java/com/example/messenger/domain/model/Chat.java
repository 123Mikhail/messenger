package com.example.messenger.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private LocalDateTime createdAt;

    // ManyToMany: Участники чата
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "chat_members",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> members = new ArrayList<>();

    // OneToMany: Сообщения в этом чате
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> messages;

    // СВЯЗЬ ДЛЯ ПОДЧАТОВ (Self-referencing OneToMany)
    // Указывает на "родительский" чат
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Chat parentChat;

    // Список "дочерних" подчатов
    @OneToMany(mappedBy = "parentChat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Chat> subChats = new ArrayList<>();
}