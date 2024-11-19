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
        // Extract email and password from the request body
        String login = loginRequest.get("email");
        String password = loginRequest.get("password");
        // Check if a user with the given email exists in the database
        Optional<UserDTO> foundUser = userService.getUserByLogin(login);

        if (foundUser.isEmpty()) {
            // If no user is found, return a 404 Not Found response
            return ResponseEntity.status(404).body("User not found");
        }
        // Retrieve the found user's details
        UserDTO user = foundUser.get();

        // Verify the provided password using BCryptPasswordEncoder.
        // Compare the plain text password from the request with the hashed password stored in the database.
        boolean isPasswordValid = passwordEncoder.matches(password, user.getPassword());
        if (!isPasswordValid) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        // Generate a JWT token for the authenticated user.
        // The token includes the user's email and ID as claims for identification purposes.
        String token = jwtService.generateToken(user.getEmail(), user.getId());


        // Return a 200 OK response with the generated JWT token
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
        // Validate that the password is not null or empty
        if (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
            // If the password is missing, return a 400 Bad Request response
            return ResponseEntity.badRequest().body("Password is required");
        }
        // Save the new user's data using the userService
        // This includes encrypting the password and storing the user in the database
        UserDTO savedUser = userService.saveUser(userDTO);

        // Generate a JWT token for the newly registered user
        // The token contains the user's email and ID for future authentication and identification
        String token = jwtService.generateToken(savedUser.getEmail(), savedUser.getId());
        // Return a 200 OK response with the generated JWT token
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
        // Retrieve the token from the Authorization header
        String authHeader = request.getHeader("Authorization");
        String token = null;

        System.out.println("BEARER" + authHeader);
        // Check if the Authorization header exists and contains a Bearer token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Remove the "Bearer " prefix to get the token
        } else {
            // If the token is not provided, return a 401 Unauthorized response with an appropriate error message
            return ResponseEntity.status(401).body(Map.of("error", "Token not provided"));

        }

        // Validate the token and extract the username (or other user-specific information)
        String username;
        try {
            username = jwtService.getUsernameFromToken(token);// Extract the username from the token

            System.out.println("usernameController" + username);
            // Check if the username is valid and the token has not expired
            if (username == null || !jwtService.validateToken(token, username)) {
                // If the token is invalid or expired, return a 401 Unauthorized response
                return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired token"));
            }
        } catch (Exception exception) {
            // Handle any exceptions that occur during token validation and return a 401 Unauthorized response
            return ResponseEntity.status(401).body(Map.of("error", "Token validation failed"));
        }

        // Retrieve user information from UserService
        Optional<UserDTO> userDTO = userService.getUserByLogin(username);
        if (userDTO.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }

        // Build the response with user details
        Map<String, Object> response = new HashMap<>();
        response.put("id", userDTO.get().getId());
        response.put("name", userDTO.get().getName());
        response.put("email", userDTO.get().getEmail());
        response.put("created_at", userDTO.get().getCreatedAt());
        response.put("updated_at", userDTO.get().getUpdatedAt());
        // response.put("token", token);// (Optional) Add the token if needed

        return ResponseEntity.ok(response);
    }

}
