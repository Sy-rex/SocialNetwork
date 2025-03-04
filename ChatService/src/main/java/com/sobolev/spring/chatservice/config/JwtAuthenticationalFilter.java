package com.sobolev.spring.chatservice.config;

import com.sobolev.spring.chatservice.security.JwtTokenUtils;
import com.sobolev.spring.chatservice.service.JwtBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class JwtAuthenticationalFilter extends OncePerRequestFilter {

    private final JwtTokenUtils jwtTokenUtils;
    private final JwtBlacklistService jwtBlacklistService;

    @Autowired
    public JwtAuthenticationalFilter(JwtTokenUtils jwtTokenUtils, JwtBlacklistService jwtBlacklistService) {
        this.jwtTokenUtils = jwtTokenUtils;
        this.jwtBlacklistService = jwtBlacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = jwtTokenUtils.extractToken(request);

        if (token != null && jwtTokenUtils.validateToken(token)) {
            if (jwtBlacklistService.isBlacklisted(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is blacklisted");
                return;
            }

            String username = jwtTokenUtils.getUsernameFromToken(token);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, null, null);
            authenticationToken.setDetails(new WebAuthenticationDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }else{
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Forbidden: Invalid or missing token");
            response.getWriter().flush();
        }

        filterChain.doFilter(request, response);
    }
}
