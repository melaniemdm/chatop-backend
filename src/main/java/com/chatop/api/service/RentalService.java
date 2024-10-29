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

//Obtenir liste de tous les rentals en DTO
public List<RentalDTO> getAllRentals() {
    List<Rental> rentals = (List<Rental>) rentalRepository.findAll();
    List<RentalDTO> rentalDTOs = new ArrayList<>();

    for (Rental rental : rentals) {
        rentalDTOs.add(entityToDto(rental));
    }

    return rentalDTOs;
}


    //Obtenir un rental par son id et le convertir en DTO
    public Optional<RentalDTO> getRentalById(Long id) {
        Optional<Rental> rental = rentalRepository.findById(id);

        if (rental.isPresent()) {
            RentalDTO rentalDTO = entityToDto(rental.get());
            return Optional.of(rentalDTO);
        } else {
            return Optional.empty();
        }
    }


public void createRental(RentalDTO rentalDTO, MultipartFile file) throws IOException {
        Rental rental = dtoToEntity(rentalDTO);

        // Vérifie et sauvegarde l'image si elle est présente
        if (file != null && !file.isEmpty()) {
            String fileName = saveFile(file);
            rental.setPicture("http://localhost:3001/api/upload/pictures/" + fileName);
            System.out.println("Fichier reçu et sauvegardé: " + fileName);
        } else {
            System.out.println("Aucun fichier reçu ou le fichier est vide.");
        }

        // Définir les dates de création et mise à jour
        rental.setCreatedAt(LocalDateTime.now());
        rental.setUpdatedAt(LocalDateTime.now());

        // Sauvegarde le rental dans le repository
        rentalRepository.save(rental);
    }


    // Mettre à jour une location existante
    public Optional<RentalDTO> updateRental(Long id, RentalDTO rentalDTO) {
        Optional<Rental> rentalOptional = rentalRepository.findById(id);

        if (rentalOptional.isPresent()) {
            Rental rental = rentalOptional.get();
            rental.setName(rentalDTO.getName());
            rental.setSurface(rentalDTO.getSurface());
            rental.setPrice(rentalDTO.getPrice());
            rental.setDescription(rentalDTO.getDescription());
            rental.setPicture(rentalDTO.getPicture());
            rental.setUpdatedAt(rentalDTO.getUpdatedAt());

            Rental updatedRental = rentalRepository.save(rental);
            return Optional.of(entityToDto(updatedRental));
        } else {
            return Optional.empty();
        }
    }


    //Supprimer un rental
public void deleteRental(Long id){
    rentalRepository.deleteById(id);}

    // Conversion d'une entité Rental en DTO
    private RentalDTO entityToDto(Rental rental) {
        RentalDTO rentalDTO = new RentalDTO();
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

    // Conversion d'un DTO en entité Rental
    private Rental dtoToEntity(RentalDTO rentalDTO) {
        Rental rental = new Rental();
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

    public String saveFile(MultipartFile file) throws IOException {

        Path uploadPath = Paths.get(uploadDirectory);
        Files.createDirectories(uploadPath);

        // Sets the path and saves the file
        String fileName = file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        System.out.println("Fichier sauvegardé à: " + filePath);

        return fileName; // Retourne le nom du fichier
    }

}



