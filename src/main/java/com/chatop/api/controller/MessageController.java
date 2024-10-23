package com.chatop.api.controller;

import com.chatop.api.model.Message;
import com.chatop.api.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/messages")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @PostMapping
    public ResponseEntity<Map<String, String>> createMessage(@RequestBody Message message) {
        if (message.getMessage() == null || message.getMessage().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Message content cannot be null or empty"));
        }

        messageService.createMessage(message);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Message created successfully");
        return ResponseEntity.ok(response);
    }
}
