package com.chatop.api.service;

import com.chatop.api.model.User;
import com.chatop.api.repository.UserRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return userRepository.save(user);
}


}
