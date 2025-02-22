package com.sobolev.spring.userservice.util;

import com.sobolev.spring.userservice.model.User;
import com.sobolev.spring.userservice.service.UserDetailService;
import com.sobolev.spring.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class UserValidator implements Validator {

    private final UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;

        if (userService.findByUsername(user.getUsername()).isPresent()) {
            errors.rejectValue("username", "", "Username already exists");
        }

        // Проверяем наличие пользователя с таким же email
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            errors.rejectValue("email", "", "Email already exists");
        }
    }
}
