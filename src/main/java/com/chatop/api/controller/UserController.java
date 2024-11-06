package com.chatop.api.controller;

import com.chatop.api.dto.UserDTO;
import com.chatop.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable Long id){
        Map<String, Object> response = new HashMap<>();
        Optional<UserDTO> userDTO = userService.getUserById(id);
        if(userDTO.isPresent()){
            response.put("id", id);
            response.put("name", userDTO.get().getName());
            response.put("email", userDTO.get().getEmail());
            response.put("created_at", userDTO.get().getCreatedAt());
            response.put("updated_at", userDTO.get().getUpdatedAt());
        }else {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response);
    }
}
