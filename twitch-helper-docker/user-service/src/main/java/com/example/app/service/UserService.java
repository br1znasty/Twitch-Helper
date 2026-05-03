package com.example.app.service;

import com.example.app.dto.AuthResponse;
import com.example.app.dto.UpdateProfileRequest;
import com.example.app.dto.UserResponse;
import com.example.app.entity.User;
import com.example.app.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse getCurrentUser(Long userId) {
        User user = getUserById(userId);
        return toUserResponse(user);
    }

    public AuthResponse updateProfile(UpdateProfileRequest request) {
        User user = getUserById(request.getUserId());

        if (!user.getUsername().equals(request.getUsername())
                && userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }

        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPasswordHash(request.getPassword());
        }

        user.setClientId(request.getClientId());
        user.setClientSecret(request.getClientSecret());

        if (request.getAccessToken() != null && !request.getAccessToken().isBlank()) {
            user.setAccessToken(request.getAccessToken());
        }

        if (request.getExpiredAt() != null) {
            user.setExpiredAt(request.getExpiredAt());
        }

        userRepository.save(user);

        return new AuthResponse("Profile updated successfully", toUserResponse(user));
    }

    private User getUserById(Long userId) {
        if (userId == null) {
            throw new RuntimeException("User id is required");
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getClientId(),
                user.getClientSecret(),
                user.getAccessToken(),
                user.getExpiredAt()
        );
    }
}