package com.sobolev.spring.userservice.service;

import com.sobolev.spring.userservice.model.Role;
import com.sobolev.spring.userservice.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role findRoleByName(String roleName) {
        return roleRepository.findByName(roleName).orElse(null);
    }
}
