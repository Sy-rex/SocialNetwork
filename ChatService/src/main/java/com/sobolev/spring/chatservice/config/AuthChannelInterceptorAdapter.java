package com.sobolev.spring.chatservice.config;

import com.sobolev.spring.chatservice.security.JwtTokenUtils;
import com.sobolev.spring.chatservice.service.JwtBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class AuthChannelInterceptorAdapter implements ChannelInterceptor {

    private final JwtTokenUtils jwtTokenUtils;
    private final JwtBlacklistService jwtBlacklistService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            if (token != null && jwtTokenUtils.validateToken(token) && !jwtBlacklistService.isBlacklisted(token)) {
                Authentication auth = jwtTokenUtils.getAuthentication(token);
                accessor.setUser(auth);
            }
        }
        return message;
    }
}
