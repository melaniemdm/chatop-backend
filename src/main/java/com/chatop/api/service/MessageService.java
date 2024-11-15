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

    //Convertit une entité en DTO
    public MessageDTO entityToDto(Message message) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(message.getId());
        messageDTO.setRentalId(message.getRentalId());
        messageDTO.setUserId(message.getUserId());
        messageDTO.setMessage(message.getMessage());
        messageDTO.setCreatedAt(message.getCreatedAt());
        messageDTO.setUpdatedAt(message.getUpdatedAt());
        return messageDTO;
    }

    //Convertit un DTO en entité
    public Message dtoToEntity(MessageDTO messageDTO) {
        Message message = new Message();
        message.setId(messageDTO.getId());
        message.setRentalId(messageDTO.getRentalId());
        message.setUserId(messageDTO.getUserId());
        message.setMessage(messageDTO.getMessage());
        message.setCreatedAt(messageDTO.getCreatedAt());
        message.setUpdatedAt(messageDTO.getUpdatedAt());
        return message;
    }

}
