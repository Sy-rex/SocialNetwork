package com.sobolev.spring.userservice.dto;

import jakarta.validation.constraints.Size;

public class ChangePasswordDTO {
    @Size(min = 3, max = 50)
    private String oldPassword;
    @Size(min = 3, max = 50)
    private String newPassword;

    public ChangePasswordDTO(String newPassword, String oldPassword) {
        this.newPassword = newPassword;
        this.oldPassword = oldPassword;
    }

    public ChangePasswordDTO() {
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
