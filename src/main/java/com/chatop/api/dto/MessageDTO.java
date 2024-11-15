package com.chatop.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Details about the message sent by a user related to a rental")
public class MessageDTO {
    @Schema(description = "Unique identifier of the message", example = "1")
    private Long id;
    @JsonProperty("rental_id")
    @Schema(description = "ID of the rental the message is related to", example = "10")
    private Integer rentalId;
    @Schema(description = "ID of the user who sent the message", example = "5")
    private Integer userId;
    @Schema(description = "Content of the message", example = "Is the property still available?")
    private String message;
    @JsonProperty("created_at")
    @Schema(description = "Timestamp when the message was created", example = "2023-11-01T12:45:30")
    private LocalDateTime created_at;
    @JsonProperty("updated_at")
    @Schema(description = "Timestamp when the message was last updated", example = "2023-11-05T15:22:10")
    private LocalDateTime updated_at;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("rental_id")
    public Integer getRentalId() {
        return rentalId;
    }

    public void setRentalId(Integer rentalId) {
        this.rentalId = rentalId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
