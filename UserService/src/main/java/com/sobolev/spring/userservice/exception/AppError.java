package com.sobolev.spring.userservice.exception;

import lombok.Data;

import java.util.Date;

@Data
public class AppError extends RuntimeException {
    private int status;
    private String message;
    private Date timestamp;
}
