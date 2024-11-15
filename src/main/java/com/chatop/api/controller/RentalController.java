package com.chatop.api.controller;

import com.chatop.api.dto.RentalDTO;
import com.chatop.api.service.JwtService;
import com.chatop.api.service.RentalService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/rentals")
@Tag(name = "Rental Management", description = "Operations related to managing rentals")
public class RentalController {

    @Autowired
    private RentalService rentalService;

    @Autowired
    private JwtService jwtService;

    @Operation(summary = "Get all rentals", description = "Fetches a list of all rentals")
    @ApiResponse(responseCode = "200", description = "List of rentals retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RentalDTO.class, example = """
                    {
                      "rentals": [
                        {
                          "id": 1,
                          "name": "test house 1",
                          "surface": 432,
                          "price": 300,
                          "picture": "https://blog.technavio.org/wp-content/uploads/2018/12/Online-House-Rental-Sites.jpg",
                          "description": "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                          "owner_id": 1,
                          "created_at": "2012/12/02",
                          "updated_at": "2014/12/02"
                        },
                        {
                          "id": 2,
                          "name": "test house 2",
                          "surface": 154,
                          "price": 200,
                          "picture": "https://blog.technavio.org/wp-content/uploads/2018/12/Online-House-Rental-Sites.jpg",
                          "description": "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                          "owner_id": 2,
                          "created_at": "2012/12/02",
                          "updated_at": "2014/12/02"
                        },
                        {
                          "id": 3,
                          "name": "test house 3",
                          "surface": 234,
                          "price": 100,
                          "picture": "https://blog.technavio.org/wp-content/uploads/2018/12/Online-House-Rental-Sites.jpg",
                          "description": "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                          "owner_id": 1,
                          "created_at": "2012/12/02",
                          "updated_at": "2014/12/02"
                        }
                      ]
                    }
                    """)))
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public Map<String, List<RentalDTO>> getAllRentals() {
        List<RentalDTO> rentalDTOs = rentalService.getAllRentals();
        // Encapsulate the list in a Map with the "rentals" key
        return Map.of("rentals", rentalDTOs);
    }

    @Operation(summary = "Get rental by ID", description = "Fetches a rental by its unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rental found",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = """
                            {
                              "id": 1,
                              "name": "dream house",
                              "surface": 24,
                              "price": 30,
                              "picture": ["https://blog.technavio.org/wp-content/uploads/2018/12/Online-House-Rental-Sites.jpg"],
                              "description": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam a lectus eleifend, varius massa ac, mollis tortor. Quisque ipsum nulla, faucibus ac metus a, eleifend efficitur augue. Integer vel pulvinar ipsum. Praesent mollis neque sed sagittis ultricies. Suspendisse congue ligula at justo molestie, eget cursus nulla tincidunt. Pellentesque elementum rhoncus arcu, viverra gravida turpis mattis in. Maecenas tempor elementum lorem vel ultricies. Nam tempus laoreet eros, et viverra libero tincidunt a. Nunc vel nisi vulputate, sodales massa eu, varius erat.",
                              "owner_id": 1,
                              "created_at": "2012/12/02",
                              "updated_at": "2014/12/02"
                            }
                            """))),
            @ApiResponse(responseCode = "404", description = "Rental not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Rental not found\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<RentalDTO> getRentalById(@Parameter(description = "ID of the rental to be retrieved", example = "1") @PathVariable Long id) {
        Optional<RentalDTO> rental = rentalService.getRentalById(id);
        if (rental.isPresent()) {
            return ResponseEntity.ok(rental.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @Operation(summary = "Create a new rental", description = "Creates a new rental with the provided details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rental created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = """
                            {
                              "message": "Rental created !"
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "Invalid data format",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = """
                            {
                              "message": "Invalid ID format in token."
                            }
                            """))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = """
                            {
                              "message": "Invalid or missing token."
                            }
                            """))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = """
                            {
                              "message": "Failed to create rental."
                            }
                            """)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> createRental(
            @Parameter(description = "Name of the rental") @RequestParam("name") String name,
            @Parameter(description = "Surface of the rental") @RequestParam("surface") Double surface,
            @Parameter(description = "Price of the rental") @RequestParam("price") Double price,
            @Parameter(description = "Description of the rental") @RequestParam("description") String description,
            @Parameter(description = "Picture of the rental", content = @Content(mediaType = "multipart/form-data"))
            @RequestParam("picture") MultipartFile file,
            HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Remove the "Bearer" prefix
        }
        // Use jwtService to extract owner ID
        String ownerIdStr = null;
        if (token != null) {
            ownerIdStr = jwtService.getIDFromToken(token); // Utilise getIDFromToken pour obtenir l'ID sous forme de chaîne
        }
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

        // Creation of the rental DTO
        RentalDTO rentalDTO = new RentalDTO();
        rentalDTO.setName(name);
        rentalDTO.setSurface(surface);
        rentalDTO.setPrice(price);
        rentalDTO.setDescription(description);
        rentalDTO.setOwnerId(owner_id);
        try {
            rentalService.createRental(rentalDTO, file);
            // Creating a Map object for the response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Rental created !");
            return ResponseEntity.ok(response);
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to create rental."));
        }
    }

    @Operation(summary = "Update a rental", description = "Updates the details of an existing rental.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rental updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = """
                            {
                              "message": "Rental updated !"
                            }
                            """))),
            @ApiResponse(responseCode = "404", description = "Rental not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = """
                            {
                              "error": "Rental not found"
                            }
                            """)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateRental(@PathVariable Long id,
                                                            @Parameter(description = "Name of the rental") @RequestParam("name") String name,
                                                            @Parameter(description = "Surface of the rental") @RequestParam("surface") Double surface,
                                                            @Parameter(description = "Price of the rental") @RequestParam("price") Double price,
                                                            @Parameter(description = "Description of the rental") @RequestParam("description") String description,
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


    @Operation(summary = "Delete a rental", description = "Deletes a rental by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rental delete successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = """
                            {
                              "message": "Rental delete successfully !"
                            }
                            """))),
            @ApiResponse(responseCode = "404", description = "Rental not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = """
                            {
                              "error": "delete rental not found"
                            }
                            """)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRental(@Parameter(description = "ID of the rental to delete") @PathVariable Long id) {
        rentalService.deleteRental(id);
        return ResponseEntity.ok("rental deleted");
    }

}
