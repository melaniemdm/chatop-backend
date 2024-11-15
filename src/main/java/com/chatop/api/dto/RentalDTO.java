package com.chatop.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Details about the rental property")
public class RentalDTO {
    @Schema(description = "Unique identifier of the rental", example = "1")
    private Long id;
    @Schema(description = "Name of the rental property", example = "Beautiful Beach House")
    private String name;
    @Schema(description = "Surface area of the rental property in square meters", example = "120.5")
    private Double surface;
    @Schema(description = "Rental price per night in USD", example = "150.0")
    private Double price;
    @Schema(description = "URL of the rental property's picture", example = "https://example.com/images/rental1.jpg")
    private String picture;
    @Schema(description = "Description of the rental property", example = "A beautiful house near the beach with stunning views.")
    private String description;
    @Schema(description = "ID of the owner of the rental property", example = "10")
    @JsonProperty("owner_id")
    private Integer ownerId;
    @Schema(description = "Timestamp when the rental was created", example = "2023-11-01T12:45:30")
    private LocalDateTime created_at;
    @Schema(description = "Timestamp when the rental was last updated", example = "2023-11-05T15:22:10")
    private LocalDateTime updated_at;

    // Getters and Setters
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

    @JsonProperty("owner_id")
    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    @JsonProperty("created_at")
    public LocalDateTime getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.created_at = createdAt;
    }

    @JsonProperty("updated_at")
    public LocalDateTime getUpdatedAt() {
        return updated_at;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updated_at = updatedAt;
    }
}
