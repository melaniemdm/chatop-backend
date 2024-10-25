package com.chatop.api.controller;

import com.chatop.api.dto.UserDTO;
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
        String login = loginRequest.get("login");
        String password = loginRequest.get("password");

        Optional<UserDTO> foundUser = userService.getUserByLogin(login);

        if (!foundUser.isPresent()) {
            return ResponseEntity.status(404).body("User not found");
        }

        UserDTO user = foundUser.get();
        if (!password.equals(user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        String tokenResponse = "{ \"token\": \"jwt_token_placeholder\" }"; // Remplacez par un vrai JWT si nécessaire
        return ResponseEntity.ok().body(tokenResponse);
    }



    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        // Validez que le password est présent
        if (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Password is required");
        }

        UserDTO savedUser = userService.saveUser(userDTO);
        String token = "jwt";  // Vous pouvez générer un vrai token JWT ici
        return ResponseEntity.ok().body("{ \"token\": \"" + token + "\" }");
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMe() {
        Long id = 1L;
        Optional<UserDTO> userDTO = userService.getUser(id);

        if (userDTO.isPresent()) {
            return ResponseEntity.ok(userDTO.get());
        } else {
            return ResponseEntity.status(404).build();
        }
    }


}
