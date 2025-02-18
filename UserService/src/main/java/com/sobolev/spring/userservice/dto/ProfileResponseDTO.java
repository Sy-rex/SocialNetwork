package com.sobolev.spring.userservice.dto;

public class ProfileResponseDTO {
    private String username;
    private String email;
    private String bio;

    public ProfileResponseDTO(String username, String email, String bio) {
        this.username = username;
        this.email = email;
        this.bio = bio;
    }

    public ProfileResponseDTO() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
