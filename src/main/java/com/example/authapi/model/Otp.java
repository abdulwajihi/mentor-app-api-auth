package com.example.authapi.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    private String otpCode;
    private String purpose;  // "SIGNUP", "SIGNIN", "RESET_PASSWORD"
    private LocalDateTime expirationTime;
    @CreationTimestamp
    private LocalDateTime createdAt;
}