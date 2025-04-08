package com.sobolev.spring.chatservice.config;

import com.sobolev.spring.chatservice.security.JwtTokenUtils;
import com.sobolev.spring.chatservice.service.JwtBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenUtils jwtTokenUtils;
    private final JwtBlacklistService jwtBlacklistService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        System.out.println("Handshake started");
        String token = extractToken(request);
        System.out.println("Extracted token: " + token);

        if (token == null || !isTokenValid(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        System.out.println("token is valid");
        String username = jwtTokenUtils.getUsernameFromToken(token);
        System.out.println("username is " + username);
        attributes.put("username", username);

        Authentication authentication = jwtTokenUtils.getAuthentication(token);
        System.out.println("authentication is " + authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }

    private String extractToken(ServerHttpRequest request) {
        System.out.println("Request URI: " + request.getURI());

        // Check Authorization header
        List<String> authHeaders = request.getHeaders().get("Authorization");
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String authHeader = authHeaders.get(0);
            if (authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }
        }

        // Check query parameter
        URI uri = request.getURI();
        String query = uri.getQuery();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (idx > 0) {
                    String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                    if ("token".equals(key)) {
                        return URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                    }
                }
            }
        }
        return null;
    }

    private boolean isTokenValid(String token) {
        return jwtTokenUtils.validateToken(token) && !jwtBlacklistService.isBlacklisted(token);
    }


}
