package com.example.authapi.service;

import com.example.authapi.model.*;
import com.example.authapi.repository.BlacklistedTokenRepository;
import com.example.authapi.repository.UserRepository;
import com.example.authapi.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    public User signUp(String email, String password, String firstName, String lastName, String gender,String role) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        if (!role.equalsIgnoreCase("USER") && !role.equalsIgnoreCase("MENTOR")) {
            throw new RuntimeException("Invalid role. Must be USER or MENTOR");
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setGender(gender);
        user.setRole(role.toUpperCase()); // Save as uppercase for consistency
        user.setActive(false);
        user.setTokenVersion(0);
        user = userRepository.save(user);

        Otp otp = otpService.generateOtp(user, "SIGNUP", 5);
        emailService.sendOtpEmail(email, otp.getOtpCode());
        return user;
    }

    public boolean verifySignUp(String email, String otp) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.isActive()) {
            throw new RuntimeException("User already active");
        }
        boolean valid = otpService.verifyOtp(user, "SIGNUP", otp);
        if (valid) {
            user.setActive(true);
            userRepository.save(user);
        }
        return valid;
    }

    public void signIn(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.isActive()) {
            throw new RuntimeException("User not active");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        Otp otp = otpService.generateOtp(user, "SIGNIN", 5);
        emailService.sendOtpEmail(email, otp.getOtpCode());
    }

    public String verifySignIn(String email, String otp) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        boolean valid = otpService.verifyOtp(user, "SIGNIN", otp);
        if (valid) {
            return jwtUtil.generateToken(user);
        }
        throw new RuntimeException("Invalid OTP");
    }

    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Otp otp = otpService.generateOtp(user, "RESET_PASSWORD", 5);
        emailService.sendOtpEmail(email, otp.getOtpCode());
    }

    public void verifyResetPassword(String email, String otp, String newPassword) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        boolean valid = otpService.verifyOtp(user, "RESET_PASSWORD", otp);
        if (valid) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } else {
            throw new RuntimeException("Invalid OTP");
        }
    }

    public void updatePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Invalid old password");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }


    public void logoutCurrentToken(String token) {
        if (blacklistedTokenRepository.existsByToken(token)) {
            throw new RuntimeException("Token already blacklisted.");
        }

        BlacklistedToken blacklisted = new BlacklistedToken();
        blacklisted.setToken(token);
        blacklisted.setExpiryDate(jwtUtil.getExpiryFromToken(token));

        blacklistedTokenRepository.save(blacklisted);
    }


    public void logoutAll(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);
    }
    public void resendOtp(String email, String purpose) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        // Check account activation based on purpose
        if (purpose.equals("SIGNUP") && user.isActive()) {
            throw new RuntimeException("User already active.");
        }

        if ((purpose.equals("SIGNIN") || purpose.equals("RESET_PASSWORD")) && !user.isActive()) {
            throw new RuntimeException("User is not active.");
        }

        Otp otp = otpService.generateOtp(user, purpose, 5);
        emailService.sendOtpEmail(email, otp.getOtpCode());
    }
    public UserProfileResponse updateUserProfile(Long userId, UserProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"+userId));

        user.setName(request.getName());
        user.setBio(request.getBio());
        user.setSkills(request.getSkills());

        userRepository.save(user);

        return UserProfileResponse.builder()
                .name(user.getName())
                .bio(user.getBio())
                .skills(user.getSkills())
                .build();
    }
    public UserProfileResponse getUserProfile(Long userId) {
        System.out.println("Trying to fetch user with ID: " + userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));

        System.out.println("User found: " + user.getEmail());

        return UserProfileResponse.builder()
                .name(user.getName())
                .bio(user.getBio())
                .skills(user.getSkills())
                .build();
    }


    public Long findIdByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return user.getId();
    }


}