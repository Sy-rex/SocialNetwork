package com.sobolev.spring.chatservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat/test")
public class ChatController {
    @GetMapping
    public String test() {
        System.out.println("test");
        return "test ok";
    }
}
