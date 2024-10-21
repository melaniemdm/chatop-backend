package com.chatop.api.service;

import com.chatop.api.model.Rental;
import com.chatop.api.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import java.util.stream.Collectors;

@Service
public class RentalService {
@Autowired
    private RentalRepository rentalRepository;

//Obtenir liste de tous les rentals
    public List<Rental>getAllRentals(){
        //convertion de l'iterable en List
        Iterable<Rental> rentalIterable = rentalRepository.findAll();
        return StreamSupport.stream(rentalIterable.spliterator(),false).collect(Collectors.toList());
    }

//Obtenir un rental par son id
public Optional<Rental> getRentalById(Long id){
        return rentalRepository.findById(id);
}

// Création d'un rental
public void createRental(Rental rental) {
    rental.setCreatedAt(LocalDateTime.now());  // Initialiser createdAt
    rental.setUpdatedAt(LocalDateTime.now());  // Initialiser updatedAt
    rentalRepository.save(rental);
}


    // Mettre à jour une location existante
    public Optional<Rental> updateRental(Long id, Rental rentalDetails) {
        return rentalRepository.findById(id).map(rental -> {
            rental.setName(rentalDetails.getName());
            rental.setSurface(rentalDetails.getSurface());
            rental.setPrice(rentalDetails.getPrice());
            rental.setDescription(rentalDetails.getDescription());
            rental.setPicture(rentalDetails.getPicture());
            rental.setUpdatedAt(rentalDetails.getUpdatedAt());
            return rentalRepository.save(rental);
        });
    }

//Supprimer un rental
public void deleteRental(Long id){
    rentalRepository.deleteById(id);}
}
