package com.example.messenger.mapper;

import com.example.messenger.domain.dto.MessageDto;
import com.example.messenger.domain.model.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {
    public MessageDto toDto(Message message) {
        if (message == null) {
            return null;
        }
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setSender(message.getSender());
        dto.setContent(message.getContent());
        dto.setTimestamp(message.getTimestamp());
        return dto;
    }

    public Message toEntity(MessageDto dto) {
        if (dto == null) return null;
        return Message.builder()
                .id(dto.getId())
                .sender(dto.getSender())
                .content(dto.getContent())
                .timestamp(dto.getTimestamp())
                .build();
    }
}