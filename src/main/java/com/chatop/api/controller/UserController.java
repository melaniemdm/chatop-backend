package com.chatop.api.controller;

import com.chatop.api.dto.UserDTO;
import com.chatop.api.service.JwtService;
import com.chatop.api.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    public ResponseEntity<Map<String, Object>> getMe(HttpServletRequest request) {
        // Récupération du token depuis l'en-tête Authorization
        String authHeader = request.getHeader("Authorization");
        String token = null;
        System.out.println("BEARER"+ authHeader);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Supprime le préfixe "Bearer "

        } else {
            return ResponseEntity.status(401).body(Map.of("error", "Token not provided"));

        }

        // Validation du token et extraction de l'email (ou autre information utilisateur)
        String username;
        try {
            username = jwtService.getUsernameFromToken(token); // Méthode pour extraire le sujet du token
            System.out.println("usernameController"+ username);
            if (username == null || !jwtService.validateToken(token, username)) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired token"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Token validation failed"));
        }

        // Récupérer les informations de l'utilisateur à partir du service UserService
        Optional<UserDTO> userDTO = userService.getUserByLogin(username);
        if (userDTO.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }

        // Construire la réponse avec les informations utilisateur et le token
        Map<String, Object> response = new HashMap<>();
        response.put("user", userDTO.get());
        response.put("token", token);

        return ResponseEntity.ok(response);
    }


}
