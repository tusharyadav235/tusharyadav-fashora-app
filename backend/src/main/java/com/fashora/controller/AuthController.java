package com.fashora.controller;

import com.fashora.dto.ApiResponse;
import com.fashora.dto.AuthDto;
import com.fashora.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthDto.AuthResponse>> register(
            @Valid @RequestBody AuthDto.RegisterRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Registered successfully", authService.register(req)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDto.AuthResponse>> login(
            @Valid @RequestBody AuthDto.LoginRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Login successful", authService.login(req)));
    }
}
