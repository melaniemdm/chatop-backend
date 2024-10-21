package com.chatop.api.controller;

import com.chatop.api.model.Rental;
import com.chatop.api.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rentals")
public class RentalController {
@Autowired
    private RentalService rentalService;

@GetMapping
    public List<Rental> getAllRentals(){
    return rentalService.getAllRentals();
}

@GetMapping("/{id}")
public ResponseEntity<Rental> getRentalById(@PathVariable Long id){
    Optional<Rental> rental = rentalService.getRentalById(id);
    return rental.map(ResponseEntity::ok).orElse( ResponseEntity.notFound().build());
}

@PostMapping
public ResponseEntity<String> createRental(@RequestBody Rental rental) {
    rentalService.createRental(rental);
    return ResponseEntity.ok("Rental created!");
}


@PutMapping("/{id}")
    public ResponseEntity<String>updateRental(@PathVariable Long id, @RequestBody Rental rentalDetails){
    Optional<Rental> updatedRental = rentalService.updateRental(id, rentalDetails);
    if(updatedRental.isPresent()){
        return ResponseEntity.ok("rental update");
    }else {
        return ResponseEntity.notFound().build();
    }
}


@DeleteMapping("/{id}")
public ResponseEntity<String> deleteRental(@PathVariable Long id){
    rentalService.deleteRental(id);
    return ResponseEntity.ok("rental deleted");
}

}
