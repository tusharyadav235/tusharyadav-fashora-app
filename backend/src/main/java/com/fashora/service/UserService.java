package com.fashora.service;

import com.fashora.entity.User;
import com.fashora.exception.ResourceNotFoundException;
import com.fashora.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;

    public UserProfileDto getProfile(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toDto(user);
    }

    public UserProfileDto updateProfile(String email, UpdateProfileRequest req) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (req.getName() != null) user.setName(req.getName());
        if (req.getPhone() != null) user.setPhone(req.getPhone());
        userRepo.save(user);
        return toDto(user);
    }

    private UserProfileDto toDto(User u) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(u.getId());
        dto.setName(u.getName());
        dto.setEmail(u.getEmail());
        dto.setPhone(u.getPhone());
        dto.setRole(u.getRole().name());
        dto.setCreatedAt(u.getCreatedAt());
        return dto;
    }

    @Data
    public static class UserProfileDto {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private String role;
        private LocalDateTime createdAt;
    }

    @Data
    public static class UpdateProfileRequest {
        private String name;
        private String phone;
    }
}
