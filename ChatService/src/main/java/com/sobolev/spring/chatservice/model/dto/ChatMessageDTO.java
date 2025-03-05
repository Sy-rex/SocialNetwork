package com.sobolev.spring.chatservice.model.dto;

import com.sobolev.spring.chatservice.model.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {
    private String sender;
    private String receiver;
    private String content;
    private long timestamp;

    public ChatMessageDTO(ChatMessage message) {
        this.sender = message.getSender();
        this.receiver = message.getReceiver();
        this.content = message.getContent();
        this.timestamp = message.getTimestamp();
    }
}
