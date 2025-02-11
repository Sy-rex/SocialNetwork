package com.sobolev.spring.userservice.service;

import com.sobolev.spring.userservice.model.Role;
import com.sobolev.spring.userservice.model.User;
import com.sobolev.spring.userservice.repository.RoleRepository;
import com.sobolev.spring.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Autowired
    public RegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                               RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @Transactional
    public void register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());

        Role role = roleService.findRoleByName("ROLE_USER"); // пока все USER потом будем фиксить
        user.setRoles(List.of(role));
        userRepository.save(user);
    }

}
