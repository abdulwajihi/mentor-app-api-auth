package com.example.authapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity

@Table(name = "users")  // Rename to "users" to avoid keyword conflict
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String gender;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
//    private boolean isStaff;
//    private boolean isSuperuser;
    private boolean isActive;
    private int tokenVersion;
    private String role; // or use an Enum Role if preferred
    private String name;
    private String bio;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> skills;



    // getters and setters for role
}

