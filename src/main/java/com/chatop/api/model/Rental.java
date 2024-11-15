package com.chatop.api.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "RENTALS") // NOM DE LA TABLE ASSOCIÉE
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "surface", nullable = false)
    private Double surface;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "picture")
    private String picture;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "owner_id", nullable = false)
    private Integer ownerId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime created_at;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updated_at;

    // Méthode appelée avant la persistance de l'entité
    @PrePersist
    protected void onCreate() {
        created_at = LocalDateTime.now();
        updated_at = LocalDateTime.now();
    }

    // Méthode appelée avant la mise à jour de l'entité
    @PreUpdate
    protected void onUpdate() {
        updated_at = LocalDateTime.now();
    }

    // getter and setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getSurface() {
        return surface;
    }

    public void setSurface(Double surface) {
        this.surface = surface;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public LocalDateTime getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.created_at = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updated_at;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updated_at = updatedAt;
    }
}
