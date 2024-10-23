package com.chatop.api.service;

import com.chatop.api.model.Message;
import com.chatop.api.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public void createMessage(Message message) {
        messageRepository.save(message);
    }
}
