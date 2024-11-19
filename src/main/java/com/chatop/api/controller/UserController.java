package com.chatop.api.controller;

import com.chatop.api.dto.UserDTO;
import com.chatop.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@Tag(name = "User Management", description = "Operations related to managing users")
public class UserController {
    @Autowired
    private UserService userService;

    @Operation(summary = "Get user by ID", description = "Fetches a user by their unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class, example = """
                            {
                              "id": 2,
                              "name": "Owner Name",
                              "email": "test@test.com",
                              "created_at": "2022/02/02",
                              "updated_at": "2022/08/02"
                            }
                            """))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"User not found\"}")))
    })

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUser(
            @Parameter(description = "ID of the user to be retrieved", example = "1") @PathVariable Long id) {
        // Create a map to hold the response data.
        Map<String, Object> response = new HashMap<>();
        // Fetch the user details using the userService.
        Optional<UserDTO> userDTO = userService.getUserById(id);
        // Check if the user was found.
        if (userDTO.isPresent()) {
            // Populate the response map with user details.
            response.put("id", id);
            response.put("name", userDTO.get().getName());
            response.put("email", userDTO.get().getEmail());
            response.put("created_at", userDTO.get().getCreatedAt());
            response.put("updated_at", userDTO.get().getUpdatedAt());
        } else {
            // If the user is not found, return a 404 Not Found response.
            return ResponseEntity.notFound().build();
        }
// Return the response with a 200 OK status.
        return ResponseEntity.ok(response);
    }
}
