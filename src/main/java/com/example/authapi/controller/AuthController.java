package com.example.authapi.controller;

import com.example.authapi.service.UserService;
import com.example.authapi.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/auth")

public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String gender,
            @RequestParam String role) {
        userService.signUp(email, password, firstName, lastName, gender,role);
        return ResponseEntity.ok("Sign-up successful. Please check your email for OTP.");
    }

    @PostMapping("/verify-signup")
    public ResponseEntity<String> verifySignUp(
            @RequestParam String email,
            @RequestParam String otp) {
        boolean success = userService.verifySignUp(email, otp);
        return success ? ResponseEntity.ok("Account activated.") : ResponseEntity.badRequest().body("Invalid OTP.");
    }

    @PostMapping("/signin")
    public ResponseEntity<String> signIn(
            @RequestParam String email,
            @RequestParam String password) {
        userService.signIn(email, password);
        return ResponseEntity.ok("OTP sent to your email.");
    }

    @PostMapping("/verify-signin")
    public ResponseEntity<String> verifySignIn(
            @RequestParam String email,
            @RequestParam String otp) {
        String token = userService.verifySignIn(email, otp);
        return ResponseEntity.ok("JWT Token: " + token);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String email) {
        userService.resetPassword(email);
        return ResponseEntity.ok("Password reset OTP sent.");
    }

    @PostMapping("/verify-reset-password")
    public ResponseEntity<String> verifyResetPassword(
            @RequestParam String email,
            @RequestParam String otp,
            @RequestParam String newPassword) {
        userService.verifyResetPassword(email, otp, newPassword);
        return ResponseEntity.ok("Password reset successful.");
    }

    @PostMapping("/update-password")
    public ResponseEntity<String> updatePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();

        userService.updatePassword(email, oldPassword, newPassword);
        return ResponseEntity.ok("Password updated.");
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromRequest(request);
        if (token == null) {
            return ResponseEntity.badRequest().body("No token provided");
        }

        userService.logoutCurrentToken(token);
        SecurityContextHolder.clearContext(); // Clear security context

        return ResponseEntity.ok("Logged out from current session");
    }



    @PostMapping("/logout-all")
    public ResponseEntity<String> logoutAll() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();

        userService.logoutAll(email);
        return ResponseEntity.ok("Logged out from all devices.");
    }
    @PostMapping("/resend-signup-otp")
    public ResponseEntity<String> resendSignupOtp(@RequestParam String email) {
        userService.resendOtp(email, "SIGNUP");
        return ResponseEntity.ok("Sign-up OTP resent.");
    }

    @PostMapping("/resend-signin-otp")
    public ResponseEntity<String> resendSigninOtp(@RequestParam String email) {
        userService.resendOtp(email, "SIGNIN");
        return ResponseEntity.ok("Sign-in OTP resent.");
    }

    @PostMapping("/resend-reset-password-otp")
    public ResponseEntity<String> resendResetPasswordOtp(@RequestParam String email) {
        userService.resendOtp(email, "RESET_PASSWORD");
        return ResponseEntity.ok("Reset password OTP resent.");
    }


}