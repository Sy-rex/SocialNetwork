package com.sobolev.spring.chatservice.repository;

import com.sobolev.spring.chatservice.model.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findBySenderAndReceiver(String sender, String receiver);

    @Query("{ $or: [ { 'sender': ?0 }, { 'receiver':  ?0 } ] }")
    List<String> findDistinctChatPartners(String username);

    List<ChatMessage> findByChatId(String chatId);
}
