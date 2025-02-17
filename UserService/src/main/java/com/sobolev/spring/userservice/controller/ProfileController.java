package com.sobolev.spring.userservice.controller;

import com.sobolev.spring.userservice.dto.ChangeUsernameDTO;
import com.sobolev.spring.userservice.model.User;
import com.sobolev.spring.userservice.service.UserDetailService;
import com.sobolev.spring.userservice.util.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final UserDetailService userDetailService;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;

    @Autowired
    public ProfileController(UserDetailService userDetailService,
                             PasswordEncoder passwordEncoder,
                             UserValidator userValidator) {
        this.userDetailService = userDetailService;
        this.passwordEncoder = passwordEncoder;
        this.userValidator = userValidator;
    }

    @PostMapping("/change/username")
    public ResponseEntity<?> changeUsername(@RequestBody ChangeUsernameDTO changeUsernameDTO, BindingResult bindingResult) {
        Optional<User> userOptional = userDetailService.findByUsername(changeUsernameDTO.getUsername());

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Username not found");
        }

        User user = userOptional.get();

        if(!passwordEncoder.matches(changeUsernameDTO.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Wrong password");
        }

        userValidator.validate(changeUsernameDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        user.setUsername(changeUsernameDTO.getNewUsername());
//        userDetailService.save(User);  надо написать

        return ResponseEntity.ok("Имя пользователя успешно изменено");
    }
}
