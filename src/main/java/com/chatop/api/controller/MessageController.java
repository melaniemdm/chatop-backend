package com.chatop.api.controller;

import com.chatop.api.dto.MessageDTO;
import com.chatop.api.service.JwtService;
import com.chatop.api.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/messages")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @Autowired
    private JwtService jwtService;

    @PostMapping
    public ResponseEntity<Map<String, String>> createMessage(
            @RequestBody Map<String, Object> requestBody,
            HttpServletRequest request) {

        // Récupérer le token JWT depuis l'en-tête Authorization
        String authHeader = request.getHeader("Authorization");
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Supprime le préfixe "Bearer "
        }

        // Utiliser jwtService pour extraire l'ID de l'utilisateur
        String userIdStr = null;
        if (token != null) {
            userIdStr = jwtService.getIDFromToken(token); // Utilise getIDFromToken pour obtenir l'ID sous forme de chaîne
        }

        // Vérifiez que l'ID n'est pas null avant la conversion
        if (userIdStr == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or missing token."));
        }

        Integer userId;
        try {
            userId = Integer.parseInt(userIdStr); // Convertir en Integer si userIdStr n'est pas null
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid ID format in token."));
        }

        // Récupérer le message et le rentalId du corps de la requête
        String message = (String) requestBody.get("message");
        Integer rentalId = (Integer) requestBody.get("rental_id");

        // Vérifier que le contenu du message n'est pas vide
        if (message == null || message.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Message content cannot be empty"));
        }

        // Vérifier que rentalId est présent
        if (rentalId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Rental ID cannot be null"));
        }

        // Créer un nouvel objet MessageDTO (ou tout autre objet) pour passer au service
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessage(message);
        messageDTO.setRentalId(rentalId);
        messageDTO.setUserId(userId); // Assigner l'ID de l'utilisateur

        // Création du message via le service
        messageService.createMessage(messageDTO);

        return ResponseEntity.ok().body(Map.of("message", "Message created successfully"));
    }}


