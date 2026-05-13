package com.fashora.service;

import com.fashora.dto.AuthDto;
import com.fashora.entity.User;
import com.fashora.exception.BadRequestException;
import com.fashora.repository.UserRepository;
import com.fashora.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;

    public AuthDto.AuthResponse register(AuthDto.RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Email already in use: " + req.getEmail());
        }
        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .phone(req.getPhone())
                .role(User.Role.USER)
                .build();
        userRepository.save(user);
        String token = jwtUtils.generateToken(user.getEmail());
        return new AuthDto.AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }

    public AuthDto.AuthResponse login(AuthDto.LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));
        String token = jwtUtils.generateToken(user.getEmail());
        return new AuthDto.AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }
}
