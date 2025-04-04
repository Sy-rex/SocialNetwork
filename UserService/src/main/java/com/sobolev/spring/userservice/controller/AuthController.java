package com.sobolev.spring.userservice.controller;

import com.sobolev.spring.userservice.dto.JwtRequest;
import com.sobolev.spring.userservice.dto.JwtResponse;
import com.sobolev.spring.userservice.dto.RegistrationUserDTO;
import com.sobolev.spring.userservice.exception.AppError;
import com.sobolev.spring.userservice.model.User;
import com.sobolev.spring.userservice.security.JwtTokenUtils;
import com.sobolev.spring.userservice.config.*;
import com.sobolev.spring.userservice.service.JwtBlacklistService;
import com.sobolev.spring.userservice.service.RegistrationService;
import com.sobolev.spring.userservice.service.UserDetailService;
import com.sobolev.spring.userservice.util.UserValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserDetailService userDetailService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private final RegistrationService registrationService;
    private final UserValidator userValidator;
    private final JwtBlacklistService jwtBlacklistService;

    @PostMapping("/registration")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationUserDTO userDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", bindingResult.getFieldError().getDefaultMessage()));
        }

        User user = convertToUser(userDTO);
        userValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(Map.of("message", bindingResult.getFieldError().getDefaultMessage()));
        }

        registrationService.register(user);

        UserDetails userDetails = userDetailService.loadUserByUsername(user.getUsername());
        String token = jwtTokenUtils.generateToken(userDetails);

        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Неправильный логин или пароль"));
        }

        UserDetails userDetails = userDetailService.loadUserByUsername(authRequest.getUsername());
        String token = jwtTokenUtils.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    private User convertToUser(RegistrationUserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = jwtTokenUtils.extractToken(request);

        if (token != null) {

            long expiration = jwtTokenUtils.getExpirationDateFromToken(token);
            long currentTimeMillis = System.currentTimeMillis() / 1000;
            long remainingTime = (expiration - currentTimeMillis) * 1000;

            jwtBlacklistService.addToBlacklist(token, remainingTime);
        }

        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {
        String oldToken = jwtTokenUtils.extractToken(request);
        if (oldToken == null || jwtBlacklistService.isBlacklisted(oldToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid token"));
        }
        if (!jwtTokenUtils.validateToken(oldToken)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Token expired"));
        }

        return ResponseEntity.ok(new JwtResponse(jwtTokenUtils.refreshToken(oldToken)));
    }
}
