package com.sobolev.spring.userservice.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtils {
    private final String secret;
    private final Duration lifetime;

    public JwtTokenUtils(
            @Value("${jwt_secret}") String secret,
            @Value("${jwt_lifetime}") Duration lifetime) {
        this.secret = secret;
        this.lifetime = lifetime;
    }

    public String generateToken(UserDetails userDetails) {
        List<String> rolesList = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Date issuedAt = new Date();
        Date expiresAt = new Date(issuedAt.getTime() + lifetime.toMillis());
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withClaim("roles", rolesList)
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .withIssuer("sobolev")
                .sign(Algorithm.HMAC256(secret));
    }

    public List<String> getRolesFromToken(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getClaim("roles").asList(String.class);
    }

    public String getUsernameFromToken(String token) {
        return JWT.decode(token).getSubject();
    }

    public Long getExpirationDateFromToken(String token) {
        return JWT.decode(token).getClaim("exp").asLong();
    }

    public String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    public String validateTokenAndRetrieveClaim(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withIssuer("sobolev")
                .build();

        DecodedJWT jwt = verifier.verify(token);
        return jwt.getSubject();
    }
}
