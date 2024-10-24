package com.chatop.api.service;

import com.chatop.api.dto.UserDTO;
import com.chatop.api.model.User;
import com.chatop.api.repository.UserRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public UserDTO saveUser(UserDTO userDTO){
        User user = dtoToEntity(userDTO);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        return entityToDto(savedUser);
    }

    // Méthode pour trouver un utilisateur par son ID
    public Optional<UserDTO> getUser(final Long id){
        Optional<User> user  = userRepository.findById(id);
        return user.map(this::entityToDto);
    }
    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(final Long id){
        userRepository.deleteById(id);
    }


    // Méthode pour trouver un utilisateur par son email
    public Optional<User> getUserByLogin(String login) {
        return userRepository.findByEmail(login); // "login" correspond à l'email
    }
    // Méthode pour convertir un User en UserDTO
    public UserDTO entityToDto(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setUpdatedAt(user.getUpdatedAt());
        return userDTO;
    }

    // Méthode pour convertir un UserDTO en User
    public User dtoToEntity(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        return user;
    }

}
