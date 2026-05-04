package com.example.messenger.service.impl;
import com.example.messenger.domain.model.Chat;
import com.example.messenger.domain.model.User;
import com.example.messenger.repository.ChatRepository;
import com.example.messenger.repository.UserRepository;
import com.example.messenger.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Chat createChat(String title, List<String> usernames, Long parentId, String type) {
        List<User> members = usernames.stream()
                .map(name -> userRepository.findByUsername(name).orElseThrow())
                .toList();
        Chat chat = Chat.builder().title(title).createdAt(LocalDateTime.now()).members(members).type(Chat.ChatType.valueOf(type.toUpperCase())).build();
        if (parentId != null) {
            Chat parent = chatRepository.findById(parentId).orElseThrow();
            chat.setParentChat(parent);
        }
        return chatRepository.save(chat);
    }

    @Override
    @Transactional
    public void addUserToChat(Long chatId, String username) {
        Chat chat = chatRepository.findById(chatId).orElseThrow();
        User user = userRepository.findByUsername(username).orElseThrow();
        if (!chat.getMembers().contains(user)) { chat.getMembers().add(user); chatRepository.save(chat); }
    }

    @Override
    @Transactional
    public void removeUserFromChat(Long chatId, String username) {
        Chat chat = chatRepository.findById(chatId).orElseThrow();
        User user = userRepository.findByUsername(username).orElseThrow();
        chat.getMembers().remove(user);
        chatRepository.save(chat);
    }

    @Override
    @Transactional
    public void deleteChat(Long chatId) { chatRepository.deleteById(chatId); }

    @Override
    @Transactional
    public Chat updateChatTitle(Long chatId, String newTitle) {
        Chat chat = chatRepository.findById(chatId).orElseThrow();
        chat.setTitle(newTitle);
        return chatRepository.save(chat);
    }

    @Override
    public List<Chat> getSubChats(Long parentId) { return chatRepository.findById(parentId).orElseThrow().getSubChats(); }

    @Override
    public Chat getById(Long id) { return chatRepository.findById(id).orElse(null); }

    @Override
    public List<Chat> getAllChats() { return chatRepository.findAll(); }
}