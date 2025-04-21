package com.example.authapi.controller;

import com.example.authapi.model.UserProfileUpdateRequest;
import com.example.authapi.model.UserProfileResponse;
import com.example.authapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;

    @GetMapping("/get-profile")
    public ResponseEntity<UserProfileResponse> getProfile(@RequestParam Long userId) {
        System.out.println("Fetching profile for userId: " + userId);
        UserProfileResponse profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }


    @PutMapping("/update-profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @RequestParam Long userId,
            @RequestBody UserProfileUpdateRequest request
    ) {
        UserProfileResponse updated = userService.updateUserProfile(userId, request);
        return ResponseEntity.ok(updated);
    }
}
