package com.chatop.api.controller;

import com.chatop.api.dto.UserDTO;
import com.chatop.api.service.JwtService;
import com.chatop.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
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
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Operation(summary = "login")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String login = loginRequest.get("email");
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

        // Generation of the JWT token for the authenticated user using the email as subject
        String token = jwtService.generateToken(user.getEmail(), user.getId());

        System.out.println("Token JWT généré : {}"+ token);

        // Response with token
        return ResponseEntity.ok("{ \"token\": \"" + token + "\" }");
    }


    @Operation(summary = "register new user")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
           //Validate that the password is present
        if (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Password is required");
        }
        // User registration
        UserDTO savedUser = userService.saveUser(userDTO);

        // Generation of the JWT token for the authenticated user using the email as subject
        String token = jwtService.generateToken(savedUser.getEmail(), savedUser.getId());

        return ResponseEntity.ok().body("{ \"token\": \"" + token + "\" }");
    }

    @Operation(summary = "get me")
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMe(HttpServletRequest request) {
        // Récupération du token depuis l'en-tête Authorization
        String authHeader = request.getHeader("Authorization");
        String token = null;

        System.out.println("BEARER"+ authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Remove the "Bearer" prefix

        } else {
            return ResponseEntity.status(401).body(Map.of("error", "Token not provided"));

        }

        // Validation du token et extraction de l'email (ou autre information utilisateur)
        String username;
        try {
            username = jwtService.getUsernameFromToken(token);

            System.out.println("usernameController"+ username);

            if (username == null || !jwtService.validateToken(token, username)) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired token"));
            }
        } catch (Exception exception) {
            return ResponseEntity.status(401).body(Map.of("error", "Token validation failed"));
        }

        // Retrieve user information from UserService
        Optional<UserDTO> userDTO = userService.getUserByLogin(username);
        if (userDTO.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }

        // Build the response with user information and token
        Map<String, Object> response = new HashMap<>();
        response.put("id", userDTO.get().getId());
        response.put("name", userDTO.get().getName());
        response.put("email", userDTO.get().getEmail());
        response.put("created_at", userDTO.get().getCreatedAt());
        response.put("updated_at", userDTO.get().getUpdatedAt());
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

}
