package com.chatop.api.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name="MESSAGES") // NOM DE LA TABLE ASSOCIÉE
public class Messages {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "rental_id", nullable = false)
    private Integer rentalId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
