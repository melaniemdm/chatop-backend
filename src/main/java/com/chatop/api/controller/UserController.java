package com.chatop.api.controller;

import com.chatop.api.dto.UserDTO;
import com.chatop.api.service.JwtService;
import com.chatop.api.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/auth")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String login = loginRequest.get("login");
        String password = loginRequest.get("password");

        Optional<UserDTO> foundUser = userService.getUserByLogin(login);

        if (foundUser.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        UserDTO user = foundUser.get();

// Verifying password using BCryptPasswordEncoder
        boolean isPasswordValid = passwordEncoder.matches(password, user.getPassword());
        if (!isPasswordValid) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        // Génération du token JWT pour l'utilisateur authentifié en utilisant l'email comme sujet
        String token = jwtService.generateToken(user.getEmail());

      // Affiche le token généré dans les logs
        logger.info("Token JWT généré : {}", token);

        // Réponse avec le token
        return ResponseEntity.ok("{ \"token\": \"" + token + "\" }");
    }



    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
           //Validate that the password is present
        if (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Password is required");
        }
        // Enregistrement de l'utilisateur
        UserDTO savedUser = userService.saveUser(userDTO);

        // Génération d'un token JWT pour l'utilisateur nouvellement inscrit en utilisant l'email comme sujet
        String token = jwtService.generateToken(savedUser.getEmail());

        return ResponseEntity.ok().body("{ \"token\": \"" + token + "\" }");
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMe() {
        Long id = 18L;
        Optional<UserDTO> userDTO = userService.getUser(id);

        if (userDTO.isPresent()) {
            return ResponseEntity.ok(userDTO.get());
        } else {
            return ResponseEntity.status(404).build();
        }
    }


}
