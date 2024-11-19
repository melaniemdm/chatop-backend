package com.chatop.api.service;

import com.chatop.api.dto.MessageDTO;
import com.chatop.api.model.Message;
import com.chatop.api.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public void createMessage(MessageDTO messageDTO) {
        messageRepository.save(dtoToEntity(messageDTO));
    }

    // Converts an entity to a Data Transfer Object (DTO)
    public MessageDTO entityToDto(Message message) {
        // Create a new instance of MessageDTO
        MessageDTO messageDTO = new MessageDTO();
        // Map the fields from the entity (Message) to the DTO (MessageDTO)
        messageDTO.setId(message.getId());
        messageDTO.setRentalId(message.getRentalId());
        messageDTO.setUserId(message.getUserId());
        messageDTO.setMessage(message.getMessage());
        messageDTO.setCreatedAt(message.getCreatedAt());
        messageDTO.setUpdatedAt(message.getUpdatedAt());
        return messageDTO;
    }

    // Converts a Data Transfer Object (DTO) to an entity
    public Message dtoToEntity(MessageDTO messageDTO) {
        // Create a new instance of Message (the entity)
        Message message = new Message();
        // Map the fields from the DTO (MessageDTO) to the entity (Message)
        message.setId(messageDTO.getId());
        message.setRentalId(messageDTO.getRentalId());
        message.setUserId(messageDTO.getUserId());
        message.setMessage(messageDTO.getMessage());
        message.setCreatedAt(messageDTO.getCreatedAt());
        message.setUpdatedAt(messageDTO.getUpdatedAt());
        return message;
    }

}
