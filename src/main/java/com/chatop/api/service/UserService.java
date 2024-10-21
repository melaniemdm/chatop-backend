package com.chatop.api.service;

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


    // Méthode pour trouver un utilisateur par son ID
    public Optional<User> getUser(final Long id){
        return userRepository.findById(id);
    }
    public Iterable<User> getUsers(){
        return userRepository.findAll();
    }
    public void deleteUser(final Long id){
        userRepository.deleteById(id);
    }
    public User saveUser(User user){
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        // Enregistre l'utilisateur dans la base de données
        return userRepository.save(user);
}

    // Méthode pour trouver un utilisateur par son email
    public Optional<User> getUserByLogin(String login) {
        return userRepository.findByEmail(login); // "login" correspond à l'email
    }


}
