package com.chatop.api.controller;

import com.chatop.api.model.User;
import com.chatop.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        // Utilisation de "login" comme alias pour "email"
        String login = loginRequest.get("login");
        String password = loginRequest.get("password");

        // Recherche de l'utilisateur par email
        Optional<User> foundUser = userService.getUserByLogin(login);

        if (foundUser.isPresent()) {
            // Vérification du mot de passe
            if (foundUser.get().getPassword().equals(password)) {
                // Création objet de réponse JSON directement
                Map<String, String> response = new HashMap<>();
                response.put("token", "jwt");
                return ResponseEntity.ok(response);
            } else {
                // Mauvais mot de passe
                return ResponseEntity.status(401).body("Invalid credentials");
            }
        } else {
            // Utilisateur non trouvé
            return ResponseEntity.status(404).body("User not found");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user){
        User savedUser = userService.saveUser(user);
        String token = "jwt";
        return ResponseEntity.ok().body("{ \"token\": \"" + token + "\" }");
    }

    @GetMapping("/me")
   public Optional<User> getMe(){
        Long id = 1L;
        return userService.getUser(id);
    }

}
