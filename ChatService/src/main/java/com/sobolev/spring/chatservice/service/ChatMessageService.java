package com.sobolev.spring.chatservice.service;

import com.sobolev.spring.chatservice.model.dto.ChatMessageDTO;
import com.sobolev.spring.chatservice.model.entity.ChatMessage;
import com.sobolev.spring.chatservice.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ModelMapper modelMapper;

    public ChatMessage saveMessage(ChatMessageDTO chatMessageDTO) {
        ChatMessage chatMessage = convertChatMessageDTOToChatMessage(chatMessageDTO);

        if (chatMessageDTO.getChatId() != null) {
            chatMessage.setReceiver(null);
        }

        return chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessageDTO> getChatMessages(String user1, String user2, String chatId) {
        List<ChatMessage> messages;

        // Если chatId предоставлен, получаем сообщения для группового чата
        if (chatId != null) {
            messages = chatMessageRepository.findByChatId(chatId);
        } else {
            // Если chatId не предоставлен, получаем сообщения между двумя пользователями
            messages = chatMessageRepository.findBySenderAndReceiver(user1, user2);
            messages.addAll(chatMessageRepository.findBySenderAndReceiver(user2, user1));
        }

        // Сортируем сообщения по ID
        messages.sort(Comparator.comparing(ChatMessage::getId));

        return messages.stream().map(ChatMessageDTO::new).collect(Collectors.toList());
    }

    public List<String> getUserChats(String username){
        return chatMessageRepository.findDistinctChatPartners(username);
    }

    private ChatMessage convertChatMessageDTOToChatMessage(ChatMessageDTO chatMessageDTO) {
        return modelMapper.map(chatMessageDTO, ChatMessage.class);
    }
}
