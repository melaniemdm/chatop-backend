package com.chatop.api.controller;

import com.chatop.api.dto.UserDTO;
import com.chatop.api.service.JwtService;
import com.chatop.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication", description = "Operations related to user authentication and registration")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Operation(summary = "User login", description = "Authenticate user with email and password to get a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"token\": \"jwt\"}"))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Login request containing email and password",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(example = "{\"email\": \"user@example.com\", \"password\": \"password123\"}")))
                                   @RequestBody Map<String, String> loginRequest) {
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

        System.out.println("Token JWT généré : {}" + token);

        // Response with token
        return ResponseEntity.ok("{ \"token\": \"" + token + "\" }");
    }


    @Operation(summary = "Register a new user", description = "Registers a new user and returns a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registration successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"token\": \"jwt\"}"))),
            @ApiResponse(responseCode = "400", description = "Password is required")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Request body containing user information for registration",
            content = @Content(mediaType = "application/json", schema = @Schema(example = """
                    {
                        "name": "test",
                        "email": "test@test.com",
                        "password": "test"
                    }
                    """))
    )
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User registration data",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDTO.class)))
                                          @RequestBody UserDTO userDTO) {
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

    @Operation(summary = "Get current user details", description = "Retrieve details of the authenticated user using a Bearer token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User data retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = """
                            {"id": 1,
                            "name": "Test TEST",
                            "email": "test@test.com",
                            "created_at": "2022/02/02",
                            "updated_at": "2022/08/02"
                            }"""))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = """
                            {
                                "error": "Invalid or missing token"
                            }
                            """))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = """
                            {
                                "error": "User not found"
                            }
                            """)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMe(@Parameter(hidden = true) HttpServletRequest request) {
        // Récupération du token depuis l'en-tête Authorization
        String authHeader = request.getHeader("Authorization");
        String token = null;

        System.out.println("BEARER" + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Remove the "Bearer" prefix

        } else {
            return ResponseEntity.status(401).body(Map.of("error", "Token not provided"));

        }

        // Validation du token et extraction de l'email (ou autre information utilisateur)
        String username;
        try {
            username = jwtService.getUsernameFromToken(token);

            System.out.println("usernameController" + username);

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
        // response.put("token", token);

        return ResponseEntity.ok(response);
    }

}
