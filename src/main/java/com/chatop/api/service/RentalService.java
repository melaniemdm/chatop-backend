package com.chatop.api.service;

import com.chatop.api.dto.RentalDTO;
import com.chatop.api.model.Rental;
import com.chatop.api.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class RentalService {
    private final String uploadDirectory = "upload/pictures";


    @Autowired
    private RentalRepository rentalRepository;

    // Retrieve a list of all rentals and convert them to DTOs
    public List<RentalDTO> getAllRentals() {
        // Fetch all rentals from the repository
        List<Rental> rentals = (List<Rental>) rentalRepository.findAll();
        // Initialize a list to store RentalDTO objects
        List<RentalDTO> rentalDTOs = new ArrayList<>();
        // Convert each Rental entity to RentalDTO and add it to the list
        for (Rental rental : rentals) {
            rentalDTOs.add(entityToDto(rental));
        }

        return rentalDTOs;
    }


    // Retrieve a rental by its ID and convert it to a DTO
    public Optional<RentalDTO> getRentalById(Long id) {
        // Fetch the rental entity by its ID from the repository
        Optional<Rental> rental = rentalRepository.findById(id);
        // If the rental exists, convert it to a DTO and return it
        if (rental.isPresent()) {
            RentalDTO rentalDTO = entityToDto(rental.get());
            return Optional.of(rentalDTO);
        } else {
            return Optional.empty();
        }
    }

    // Create a new rental with the given DTO and file
    public void createRental(RentalDTO rentalDTO, MultipartFile file) throws IOException {
        // Convert the DTO to a Rental entity
        Rental rental = dtoToEntity(rentalDTO);

        // Check if a file is provided and save it
        if (file != null && !file.isEmpty()) {
            String fileName = saveFile(file);
            rental.setPicture("http://localhost:3001/api/upload/pictures/" + fileName);
            System.out.println("Fichier reçu et sauvegardé: " + fileName);
        } else {
            System.out.println("Aucun fichier reçu ou le fichier est vide.");
        }

        // Set creation and update timestamps
        rental.setCreatedAt(LocalDateTime.now());
        rental.setUpdatedAt(LocalDateTime.now());

        // Save the rental entity in the repository
        rentalRepository.save(rental);
    }


    // Update an existing rental with the given DTO
    public Optional<RentalDTO> updateRental(Long id, RentalDTO rentalDTO) {
        // Fetch the rental entity by its ID
        Optional<Rental> rentalOptional = rentalRepository.findById(id);

        if (rentalOptional.isPresent()) {
            Rental rental = rentalOptional.get();
            // Update the fields of the rental entity with values from the DTO
            rental.setName(rentalDTO.getName());
            rental.setSurface(rentalDTO.getSurface());
            rental.setPrice(rentalDTO.getPrice());
            rental.setDescription(rentalDTO.getDescription());

            // Save the updated rental entity in the repository
            Rental updatedRental = rentalRepository.save(rental);
            // Convert the updated entity to a DTO and return it
            return Optional.of(entityToDto(updatedRental));
        } else {
            // Return an empty Optional if the rental does not exist
            return Optional.empty();
        }
    }


    // Delete a rental by its ID
    public void deleteRental(Long id) {
        // Remove the rental entity with the given ID from the repository
        rentalRepository.deleteById(id);
    }

    // Convert a Rental entity to a DTO
    private RentalDTO entityToDto(Rental rental) {
        // Create a new RentalDTO instance
        RentalDTO rentalDTO = new RentalDTO();
        // Map the fields from the entity to the DTO
        rentalDTO.setId(rental.getId());
        rentalDTO.setName(rental.getName());
        rentalDTO.setSurface(rental.getSurface());
        rentalDTO.setPrice(rental.getPrice());
        rentalDTO.setPicture(rental.getPicture());
        rentalDTO.setDescription(rental.getDescription());
        rentalDTO.setOwnerId(rental.getOwnerId());
        rentalDTO.setCreatedAt(rental.getCreatedAt());
        rentalDTO.setUpdatedAt(rental.getUpdatedAt());
        return rentalDTO;
    }

    // Convert a DTO to a Rental entity
    private Rental dtoToEntity(RentalDTO rentalDTO) {
        // Create a new Rental entity instance
        Rental rental = new Rental();
        // Map the fields from the DTO to the entity
        rental.setId(rentalDTO.getId());
        rental.setName(rentalDTO.getName());
        rental.setSurface(rentalDTO.getSurface());
        rental.setPrice(rentalDTO.getPrice());
        rental.setPicture(rentalDTO.getPicture());
        rental.setDescription(rentalDTO.getDescription());
        rental.setOwnerId(rentalDTO.getOwnerId());
        rental.setCreatedAt(rentalDTO.getCreatedAt());
        rental.setUpdatedAt(rentalDTO.getUpdatedAt());
        return rental;
    }

    // Save a file to the specified directory and return its name
    public String saveFile(MultipartFile file) throws IOException {
        // Create the directory if it does not exist
        Path uploadPath = Paths.get(uploadDirectory);
        Files.createDirectories(uploadPath);

        // Get the file name and define the path where it will be saved
        String fileName = file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        // Save the file to the specified location, replacing any existing file with the same name
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        System.out.println("Fichier sauvegardé à: " + filePath);
        // Return the file name
        return fileName;
    }

}



