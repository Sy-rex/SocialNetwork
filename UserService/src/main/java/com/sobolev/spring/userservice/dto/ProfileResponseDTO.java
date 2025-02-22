package com.sobolev.spring.userservice.dto;

import lombok.Data;

@Data
public class ProfileResponseDTO {
    private String username;
    private String email;
    private String bio;
}
