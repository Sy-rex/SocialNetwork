package com.sobolev.spring.chatservice.model.dto;

import com.sobolev.spring.chatservice.model.entity.ChatMessage;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {
    private String chatId;
    private String sender;
    private String receiver;
    private String content;
    private long timestamp;

    public ChatMessageDTO(ChatMessage message) {
        this.chatId = message.getChatId();
        this.sender = message.getSender();
        this.receiver = message.getReceiver();
        this.content = message.getContent();
        this.timestamp = message.getTimestamp();
    }
}
