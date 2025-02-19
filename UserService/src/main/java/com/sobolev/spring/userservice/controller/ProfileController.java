package com.sobolev.spring.userservice.controller;

import com.sobolev.spring.userservice.dto.ChangePasswordDTO;
import com.sobolev.spring.userservice.dto.ProfileResponseDTO;
import com.sobolev.spring.userservice.model.User;
import com.sobolev.spring.userservice.security.JwtTokenUtils;
import com.sobolev.spring.userservice.service.UserDetailService;
import com.sobolev.spring.userservice.service.UserService;
import com.sobolev.spring.userservice.util.UserValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final UserService userService;
    private final UserValidator userValidator;
    private final JwtTokenUtils jwtTokenUtils;
    private final ModelMapper modelMapper;

    @Autowired
    public ProfileController(UserService userService,
                             UserValidator userValidator,
                             JwtTokenUtils jwtTokenUtils, ModelMapper modelMapper) {
        this.userService = userService;
        this.userValidator = userValidator;
        this.jwtTokenUtils = jwtTokenUtils;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        String token = jwtTokenUtils.extractToken(request);
        String user = jwtTokenUtils.getUsernameFromToken(token);
        if (user != null) {
            Optional<User> userDetail = userService.findByUsername(user);

            if (userDetail.isPresent())
                return ResponseEntity.ok(convertFromUser(userDetail.get()));

            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.badRequest().body("Problem with token");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProfileById(@PathVariable("id") Integer id) {
        Optional<User> userDetail = userService.findById(id);
        if (userDetail.isPresent()) {
            return ResponseEntity.ok(convertFromUser(userDetail.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/me")
    public ResponseEntity<String> changeProfile(HttpServletRequest request,
                                                @RequestBody @Valid ProfileResponseDTO profileResponseDTO) {
        String username = jwtTokenUtils.getUsernameFromToken(jwtTokenUtils.extractToken(request));
        if (username != null) {
            userService.updateUser(username, profileResponseDTO);
            return ResponseEntity.ok("Profile updated successfully");
        }
        return ResponseEntity.badRequest().body("Problem with token");
    }

    @DeleteMapping("/me")
    public ResponseEntity<String> deleteProfile(HttpServletRequest request){
        String username = jwtTokenUtils.getUsernameFromToken(jwtTokenUtils.extractToken(request));
        if (username != null) {
            userService.deleteUser(username);
            return ResponseEntity.ok("Profile deleted successfully");
        }
        return ResponseEntity.badRequest().body("Problem with token");
    }

    @PostMapping("/me/password")
    public ResponseEntity<?> changeUsername(HttpServletRequest request,
                                            @RequestBody ChangePasswordDTO changePasswordDTO,
                                            BindingResult bindingResult) {
        String username = jwtTokenUtils.getUsernameFromToken(jwtTokenUtils.extractToken(request));
        if (username != null) {
//            сделать валидатор
            userService.changePassword(changePasswordDTO, username);
            return ResponseEntity.ok("Password changed successfully");
        }
        return ResponseEntity.badRequest().body("Problem with token");
    }

    private ProfileResponseDTO convertFromUser(User user) {
        return modelMapper.map(user, ProfileResponseDTO.class);
    }

    private User convertFromProfileDTO(ProfileResponseDTO profileResponseDTO) {
        return modelMapper.map(profileResponseDTO, User.class);
    }
}
