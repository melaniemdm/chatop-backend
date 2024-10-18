package com.chatop.api.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name="RENTALS") // NOM DE LA TABLE ASSOCIÉE
public class Rentals {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surface", nullable = false)
    private Integer surface;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "picture")
    @Lob // Permet de stocker des objets volumineux comme des fichiers binaires
    private byte[] picture; // Stockera l'image en tant que données binaires

    @Column(name = "description")
    private String description;

    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}
