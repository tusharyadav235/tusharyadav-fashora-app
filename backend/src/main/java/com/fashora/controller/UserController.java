package com.fashora.controller;

import com.fashora.dto.ApiResponse;
import com.fashora.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserService.UserProfileDto>> getProfile(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getProfile(user.getUsername())));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserService.UserProfileDto>> updateProfile(
            @AuthenticationPrincipal UserDetails user,
            @RequestBody UserService.UpdateProfileRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Profile updated", userService.updateProfile(user.getUsername(), req)));
    }
}
