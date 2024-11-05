package com.chatop.api.controller;

import com.chatop.api.dto.RentalDTO;
import com.chatop.api.service.JwtService;
import com.chatop.api.service.RentalService;
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

@GetMapping
public Map<String, List<RentalDTO>> getAllRentals() {
    List<RentalDTO> rentalDTOs = rentalService.getAllRentals();

    // Encapsule la liste dans un Map avec la clé "rentals"
    return Map.of("rentals", rentalDTOs);
}

    @GetMapping("/{id}")
    public ResponseEntity<RentalDTO> getRentalById(@PathVariable Long id) {
        Optional<RentalDTO> rental = rentalService.getRentalById(id);

        if (rental.isPresent()) {
            return ResponseEntity.ok(rental.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }



    @PostMapping
    public ResponseEntity<Map<String, Object>> createRental(
            @RequestParam("name") String name,
            @RequestParam("surface") Double surface,
            @RequestParam("price") Double price,
            @RequestParam("description") String description,
            @RequestParam("picture") MultipartFile file,
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

        Integer ownerId;
        try {
            ownerId = Integer.parseInt(ownerIdStr); // Convertir en Integer si ownerIdStr n'est pas null
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
        rentalDTO.setOwnerId(ownerId);

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


    @PutMapping("/{id}")
public ResponseEntity<String> updateRental(@PathVariable Long id, @RequestBody RentalDTO rentalDTO) {
    Optional<RentalDTO> updatedRental = rentalService.updateRental(id, rentalDTO);

    if (updatedRental.isPresent()) {
        return ResponseEntity.ok("Rental updated");
    } else {
        return ResponseEntity.notFound().build();
    }
}




    @DeleteMapping("/{id}")
public ResponseEntity<String> deleteRental(@PathVariable Long id){
    rentalService.deleteRental(id);
    return ResponseEntity.ok("rental deleted");
}

}
