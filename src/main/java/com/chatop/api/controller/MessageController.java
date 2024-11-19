package com.chatop.api.controller;

import com.chatop.api.dto.MessageDTO;
import com.chatop.api.service.JwtService;
import com.chatop.api.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/messages")
@Tag(name = "Message Management", description = "Operations related to managing messages")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @Autowired
    private JwtService jwtService;

    @Operation(
            summary = "Create a new message",
            description = "Creates a new message associated with a specific rental.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = """
                            {
                              "message": "Message send with success"
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid input data or format",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = """
                            {
                              "error": "Invalid ID format in token."
                            }
                            """))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = """
                            {
                              "error": "Invalid or missing token."
                            }
                            """)))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Request body containing the message content and rental ID",
            content = @Content(mediaType = "application/json", schema = @Schema(example = """
                    {
                    "message" : "ceci est un message",
                     "rental_id": 1
                    }"""))
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<Map<String, String>> createMessage(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request body containing the message content and rental ID",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "")))
            @RequestBody Map<String, Object> requestBody,
            @Parameter(hidden = true) HttpServletRequest request) {

        // Retrieve the JWT token from the Authorization header
        String authHeader = request.getHeader("Authorization");
        String token = null;
        // Check if the Authorization header contains a Bearer token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Extract the token by removing the "Bearer " prefix
            System.out.println(token);
        }
        // Extract the user ID from the token using the JwtService
        String userIdStr = null;
        if (token != null) {
            userIdStr = jwtService.getIDFromToken(token); // Decode the token to retrieve the user ID as a string
        }
        // If the user ID is null, return a 401 Unauthorized response
        if (userIdStr == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or missing token."));
        }
        Integer userId;
        try {
            // Convert the user ID from String to Integer
            userId = Integer.parseInt(userIdStr); // Converted to Integer if userIdStr is not null
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid ID format in token."));
        }

        // Get the message and rentalId from the request body
        String message = (String) requestBody.get("message");
        Integer rentalId = (Integer) requestBody.get("rental_id");
        // Validate that the message content is not null or empty
        if (message == null || message.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Message content cannot be empty"));
        }

        // Validate that the rental ID is provided in the request body
        if (rentalId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Rental ID cannot be null"));
        }

        // Create a new MessageDTO object and populate it with the extracted data
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessage(message);
        messageDTO.setRentalId(rentalId);
        messageDTO.setUserId(userId); // Assigns user ID

        // Save the message via the MessageService
        messageService.createMessage(messageDTO);
        // Return a 200 OK response indicating that the message was successfully created
        return ResponseEntity.ok().body(Map.of("message", "Message send with success"));
    }
}



