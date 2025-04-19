package com.example.authapi.service;

import com.example.authapi.model.Otp;
import com.example.authapi.model.User;
import com.example.authapi.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OtpService {
    @Autowired
    private OtpRepository otpRepository;

    public Otp generateOtp(User user, String purpose, int expirationMinutes) {
        List<Otp> existingOtps = otpRepository.findByUserAndPurpose(user, purpose);
        otpRepository.deleteAll(existingOtps);

        String otpCode = generateOtpCode();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(expirationMinutes);
        Otp otp = new Otp();
        otp.setUser(user);
        otp.setOtpCode(otpCode);
        otp.setPurpose(purpose);
        otp.setExpirationTime(expirationTime);
        return otpRepository.save(otp);
    }

    public boolean verifyOtp(User user, String purpose, String otpCode) {
        List<Otp> otps = otpRepository.findByUserAndPurpose(user, purpose);
        for (Otp otp : otps) {
            if (otp.getOtpCode().equals(otpCode) && LocalDateTime.now().isBefore(otp.getExpirationTime())) {
                otpRepository.delete(otp);
                return true;
            }
        }
        return false;
    }

    private String generateOtpCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}