package com.chatop.api.controller;

import com.chatop.api.dto.RentalDTO;
import com.chatop.api.service.JwtService;
import com.chatop.api.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/rentals")
public class RentalController {

    @Autowired
    private RentalService rentalService;
    @Autowired
    private  JwtService jwtService;

    @Operation(summary = "get all rentals")
    @GetMapping
    public Map<String, List<RentalDTO>> getAllRentals() {
    List<RentalDTO> rentalDTOs = rentalService.getAllRentals();

    // Encapsule la liste dans un Map avec la clé "rentals"
    return Map.of("rentals", rentalDTOs);
}

    @Operation(summary = "rental id")
    @GetMapping("/{id}")
    public ResponseEntity<RentalDTO> getRentalById(@PathVariable Long id) {
        Optional<RentalDTO> rental = rentalService.getRentalById(id);

        if (rental.isPresent()) {
            return ResponseEntity.ok(rental.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @Operation(summary = "create rental")
    @PostMapping
    public ResponseEntity<Map<String, Object>> createRental(
            @Parameter(description = "Nom de la location")@RequestParam("name") String name,
            @Parameter(description = "surface de la location")@RequestParam("surface") Double surface,
            @Parameter(description = "Prix de la location")@RequestParam("price") Double price,
            @Parameter(description = "description de la location")@RequestParam("description") String description,
            @Parameter(description = "Photo de la location")@RequestParam("picture") MultipartFile file,
            HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Supprime le préfixe "Bearer "
        }

        // Utiliser jwtService pour extraire l'ID du propriétaire
        String ownerIdStr = null;
        if (token != null) {
            ownerIdStr = jwtService.getIDFromToken(token); // Utilise getIDFromToken pour obtenir l'ID sous forme de chaîne
        }

        // Vérifiez que l'ID n'est pas null avant la conversion
        if (ownerIdStr == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid or missing token."));
        }

        Integer owner_id;
        try {
            owner_id = Integer.parseInt(ownerIdStr); // Convertir en Integer si ownerIdStr n'est pas null
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Invalid ID format in token."));
        }

        // Créer le DTO de location
        RentalDTO rentalDTO = new RentalDTO();
        rentalDTO.setName(name);
        rentalDTO.setSurface(surface);
        rentalDTO.setPrice(price);
        rentalDTO.setDescription(description);
        rentalDTO.setOwnerId(owner_id);

        try {
            rentalService.createRental(rentalDTO, file);

            // Créer un objet Map pour la réponse
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Rental created successfully!");


            return ResponseEntity.ok(response);
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to create rental."));
        }
    }

    @Operation(summary = "update rental")
    @PutMapping("/{id}")
public ResponseEntity <Map<String, Object>> updateRental(@PathVariable Long id,
                                           @Parameter(description = "Nom de la location")@RequestParam("name") String name,
                                           @Parameter(description = "surface de la location")@RequestParam("surface") Double surface,
                                           @Parameter(description = "Prix de la location")@RequestParam("price") Double price,
                                           @Parameter(description = "description de la location")@RequestParam("description") String description,
                                           HttpServletRequest request) {
        RentalDTO rentalDTO = new RentalDTO();
        rentalDTO.setId(id);
        rentalDTO.setName(name);
        rentalDTO.setSurface(surface);
        rentalDTO.setPrice(price);
        rentalDTO.setDescription(description);
    Optional<RentalDTO> updatedRental = rentalService.updateRental(id, rentalDTO);

    if (updatedRental.isPresent()) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Rental updated !");
        return ResponseEntity.ok(response);
    } else {
        return ResponseEntity.notFound().build();
    }
}



    @Operation(summary = "delete rental")
    @DeleteMapping("/{id}")
public ResponseEntity<String> deleteRental(@PathVariable Long id){
    rentalService.deleteRental(id);
    return ResponseEntity.ok("rental deleted");
}

}
