package com.chatop.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Details about the user")
public class UserDTO {
    @Schema(description = "Unique identifier of the user", example = "1")
    private Long id;
    @Schema(description = "Name of the user", example = "John Doe")
    private String name;
    @Schema(description = "Email address of the user", example = "johndoe@example.com")
    private String email;
    @Schema(description = "Password of the user", example = "password123",
            accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;
    @JsonProperty("created_at")
    @Schema(description = "Timestamp when the user was created", example = "2023-11-01T12:45:30")
    private LocalDateTime created_at;

    @JsonProperty("updated_at")
    @Schema(description = "Timestamp when the user was last updated", example = "2023-11-05T15:22:10")
    private LocalDateTime updated_at;

    // Getters and setters
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
