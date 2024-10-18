package com.chatop.api.controller;

import com.chatop.api.model.User;
import com.chatop.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping("/me")
   public Optional<User> getMe(){
        Long id = 1L;
        return userService.getUser(id);
    }
}
