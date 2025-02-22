package com.sobolev.spring.userservice.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordDTO {
    @Size(min = 3, max = 50)
    private String oldPassword;
    @Size(min = 3, max = 50)
    private String newPassword;
}
