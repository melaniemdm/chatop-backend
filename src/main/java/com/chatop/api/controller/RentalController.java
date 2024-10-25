package com.chatop.api.controller;

import com.chatop.api.dto.RentalDTO;
import com.chatop.api.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rentals")
public class RentalController {

@Autowired
    private RentalService rentalService;

@GetMapping
    public List<RentalDTO> getAllRentals(){
    return rentalService.getAllRentals();
}

    @GetMapping("/{id}")
    public ResponseEntity<RentalDTO> getRentalById(@PathVariable Long id) {
        return rentalService.getRentalById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<String> createRental(
            @RequestParam("name") String name,
            @RequestParam("surface") Double surface,
            @RequestParam("price") Double price,
            @RequestParam("description") String description,
            @RequestParam("ownerId") Integer ownerId,
            @RequestParam("picture") MultipartFile file) {

        RentalDTO rentalDTO = new RentalDTO();
        rentalDTO.setName(name);
        rentalDTO.setSurface(surface);
        rentalDTO.setPrice(price);
        rentalDTO.setDescription(description);
        rentalDTO.setOwnerId(ownerId);

        try {
            rentalService.createRental(rentalDTO, file);
            return ResponseEntity.ok("Rental created!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create rental");
        }
    }


@PutMapping("/{id}")
public ResponseEntity<String> updateRental(@PathVariable Long id, @RequestBody RentalDTO rentalDTO) {
    return rentalService.updateRental(id, rentalDTO)
            .map(rental -> ResponseEntity.ok("Rental updated"))
            .orElse(ResponseEntity.notFound().build());
}



    @DeleteMapping("/{id}")
public ResponseEntity<String> deleteRental(@PathVariable Long id){
    rentalService.deleteRental(id);
    return ResponseEntity.ok("rental deleted");
}

}
