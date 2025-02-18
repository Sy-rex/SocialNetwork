package com.sobolev.spring.userservice.service;

import com.sobolev.spring.userservice.dto.ChangePasswordDTO;
import com.sobolev.spring.userservice.dto.ProfileResponseDTO;
import com.sobolev.spring.userservice.model.User;
import com.sobolev.spring.userservice.repository.RoleRepository;
import com.sobolev.spring.userservice.repository.UserRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserDetailService(UserRepository userRepository,
                             RoleService roleService,
                             PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Integer id){
        return userRepository.findById(id.intValue());
    }


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
        );
    }

    public void createNewUser(User user) {
        user.setRoles(List.of(roleService.findRoleByName("ROLE_USER")));
        userRepository.save(user);
    }

    @Transactional
    public void updateUser(String username, ProfileResponseDTO profile) {
        User user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        if(profile.getUsername() != null) {
            user.setUsername(profile.getUsername());
        }
        if(profile.getEmail() != null) {
            user.setEmail(profile.getEmail());
        }
        if(profile.getBio() != null) {
            user.setBio(profile.getBio());
        }

        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(String username) {
        User user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        userRepository.delete(user);
    }

    @Transactional
    public void changePassword(ChangePasswordDTO changePasswordDTO, String username) {
        User user = findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect current password");
        }

        if (passwordEncoder.matches(changePasswordDTO.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password cannot be the same as the old password");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        userRepository.save(user);
    }

}
