package com.chatop.api.controller;

import com.chatop.api.dto.MessageDTO;
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
    public ResponseEntity<Map<String, String>> createMessage(@RequestBody MessageDTO messageDTO) {
        if (messageDTO.getMessage().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Message content cannot be empty"));
        }
// Creation of the message via service
        messageService.createMessage(messageDTO);
        return ResponseEntity.ok(Map.of("message", "Message created successfully"));
    }
}
