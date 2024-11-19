package com.chatop.api.service;

import com.chatop.api.dto.UserDTO;
import com.chatop.api.model.User;
import com.chatop.api.repository.UserRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Data
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Injection du PasswordEncoder

    // Save a new user in the database
    public UserDTO saveUser(UserDTO userDTO) {
        // Convert the UserDTO to a User entity
        User user = dtoToEntity(userDTO);
        // Encode the user's password using a secure hashing algorithm (BCrypt)
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        // Set the creation and update timestamps
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        // Save the User entity to the database and retrieve the saved entity
        User savedUser = userRepository.save(user);
        // Convert the saved entity back to a UserDTO and return it
        return entityToDto(savedUser);
    }

    // Find a user by their ID and return as a UserDTO
    public Optional<UserDTO> getUser(final Long id) {
        // Retrieve the User entity by its ID from the repository
        Optional<User> user = userRepository.findById(id);
        // If the user exists, convert it to a UserDTO and return it
        return user.map(this::entityToDto);
    }

    // Retrieve all users from the database
    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    // Delete a user by their ID
    public void deleteUser(final Long id) {
        userRepository.deleteById(id);
    }


    // Find a user by their email address and return as a UserDTO
    public Optional<UserDTO> getUserByLogin(String login) {
        // Search for a User entity with the given email in the repository
        Optional<User> user = userRepository.findByEmail(login);

        // Log whether the user was found or not
        if (user.isPresent()) {
            System.out.println("Utilisateur trouvé : " + user.get());
        } else {
            System.out.println("Aucun utilisateur trouvé pour l'email : " + login);
        }
        // Convert the User entity to a UserDTO if present and return it
        return user.map(this::entityToDto);

    }

    // Find a user by their ID and return as a UserDTO
    public Optional<UserDTO> getUserById(Long id) {
        // Search for a User entity with the given ID in the repository
        Optional<User> user = userRepository.findById(id);
        // Log whether the user was found or not
        if (user.isPresent()) {
            System.out.println("Utilisateur trouvé  : " + user.get());
        } else {
            System.out.println("Aucun utilisateur trouvé pour l'id : " + id);
        }
        // Convert the User entity to a UserDTO if present and return it
        return user.map(this::entityToDto);
    }


    // Convert a User entity to a UserDTO
    public UserDTO entityToDto(User user) {
        // Create a new UserDTO instance
        UserDTO userDTO = new UserDTO();
        // Map the fields from the User entity to the UserDTO
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setUpdatedAt(user.getUpdatedAt());
        userDTO.setPassword(user.getPassword());// Include the password if needed

        return userDTO;
    }

    // Convert a UserDTO to a User entity
    public User dtoToEntity(UserDTO userDTO) {
        // Create a new User entity instance
        User user = new User();
        // Map the fields from the UserDTO to the User entity
        user.setId(userDTO.getId());
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        return user;
    }

}
