package com.sobolev.spring.chatservice.controller;

import com.sobolev.spring.chatservice.model.dto.ChatMessageDTO;
import com.sobolev.spring.chatservice.model.entity.ChatMessage;
import com.sobolev.spring.chatservice.service.ChatMessageService;
import com.sobolev.spring.chatservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageService chatMessageService;
    private final NotificationService notificationService;

    @MessageMapping("/sendMessage")
    @SendTo("/topic/chat")
    public ChatMessageDTO sendMessage(ChatMessageDTO chatMessageDTO, Principal principal) {
        // Устанавливаем отправителя из Principal (из JWT)
        chatMessageDTO.setSender(principal.getName());

        // Сохраняем сообщение в MongoDB
        ChatMessage savedMessage = chatMessageService.saveMessage(chatMessageDTO);

        // Отправляем уведомление в NotificationService через Kafka
//        notificationService.sendNewMessageNotification(savedMessage);

        return new ChatMessageDTO(savedMessage);
    }
}
