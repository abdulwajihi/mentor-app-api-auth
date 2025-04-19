package com.example.authapi.repository;

import com.example.authapi.model.Otp;
import com.example.authapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OtpRepository extends JpaRepository<Otp, Long> {
    List<Otp> findByUserAndPurpose(User user, String purpose);
}