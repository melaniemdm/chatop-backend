package com.chatop.api.controller;

import com.chatop.api.model.User;
import com.chatop.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user){
        User savedUser = userService.saveUser(user);
        String token = "jwt";
        return ResponseEntity.ok().body("{ \"token\": \"" + token + "\" }");
    }

    @GetMapping("/me")
   public Optional<User> getMe(){
        Long id = 1L;
        return userService.getUser(id);
    }

}
