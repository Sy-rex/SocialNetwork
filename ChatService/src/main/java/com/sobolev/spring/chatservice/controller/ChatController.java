package com.sobolev.spring.chatservice.controller;

import com.sobolev.spring.chatservice.model.dto.ChatMessageDTO;
import com.sobolev.spring.chatservice.model.entity.ChatMessage;
import com.sobolev.spring.chatservice.service.ChatMessageService;
import com.sobolev.spring.chatservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageService chatMessageService;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    // Получение сообщений чата (для двух пользователей или группового)
    @GetMapping("/messages/{user1}/{user2}")
    public List<ChatMessageDTO> getChatMessages(@PathVariable String user1,
                                                @PathVariable String user2,
                                                @RequestParam(required = false) String chatId) {
        return chatMessageService.getChatMessages(user1, user2, chatId);
    }

    @GetMapping("/user/{username}/chats")
    public List<String> getUserChats(@PathVariable String username) {
        return chatMessageService.getUserChats(username);
    }

    @MessageMapping("/sendMessage")
    public void sendMessage(ChatMessageDTO chatMessageDTO, Principal principal) {

        // Устанавливаем отправителя из Principal (из JWT)
        chatMessageDTO.setSender(principal.getName());

        // Сохраняем сообщение в MongoDB
        ChatMessage savedMessage = chatMessageService.saveMessage(chatMessageDTO);

        if (chatMessageDTO.getChatId() == null) {
            messagingTemplate.convertAndSend("/topic/private." + chatMessageDTO.getReceiver(), new ChatMessageDTO(savedMessage));
        } else{
            messagingTemplate.convertAndSend("/topic/group." + chatMessageDTO.getChatId(), new ChatMessageDTO(savedMessage));
        }

        // Отправляем уведомление в NotificationService через Kafka
//        notificationService.sendNewMessageNotification(savedMessage);
    }
}
