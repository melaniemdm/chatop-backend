package com.chatop.api.controller;

import com.chatop.api.dto.UserDTO;
import com.chatop.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

// Generating a response with the token (placeholder)
        String tokenResponse = "{ \"token\": \"jwt_token_placeholder\" }";
        return ResponseEntity.ok(tokenResponse);
    }



    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
           //Validate that the password is present
        if (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Password is required");
        }

        UserDTO savedUser = userService.saveUser(userDTO);
        String token = "jwt";  // Vous pouvez générer un vrai token JWT ici
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
