package com.sobolev.spring.chatservice.config;

import com.sobolev.spring.chatservice.security.JwtTokenUtils;
import com.sobolev.spring.chatservice.service.JwtBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenUtils jwtTokenUtils;
    private final JwtBlacklistService jwtBlacklistService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        log.debug("STOMP Command: {}", command);

        if (command == StompCommand.CONNECT) {
            handleConnect(accessor);
        }

        return message;
    }

    private void handleConnect(StompHeaderAccessor accessor) {
        String token = extractToken(accessor);
        log.debug("Extracted JWT Token: {}", token);

        if (token == null) {
            log.error("Missing JWT token in CONNECT headers");
            throw new MessageDeliveryException("Authorization token required");
        }

        if (jwtBlacklistService.isBlacklisted(token)) {
            log.warn("Blacklisted token attempt: {}", token);
            throw new MessageDeliveryException("Token revoked");
        }

        if (!jwtTokenUtils.validateToken(token)) {
            log.error("Invalid JWT token: {}", token);
            throw new MessageDeliveryException("Invalid token");
        }

        String username = jwtTokenUtils.getUsernameFromToken(token);
        log.info("Authenticated user: {}", username);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        accessor.setUser(authentication);
    }

    private String extractToken(StompHeaderAccessor accessor) {
        // Проверяем заголовок Authorization
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader != null && !authHeader.isEmpty()) {
            if (authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }
            return authHeader;
        }

        // Если нет в Authorization, проверяем параметр token
        return accessor.getFirstNativeHeader("token");
    }
}