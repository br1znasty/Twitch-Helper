package com.example.app.service;

import com.example.app.dto.AuthResponse;
import com.example.app.dto.LoginRequest;
import com.example.app.dto.RegisterRequest;
import com.example.app.dto.UpdateProfileRequest;
import com.example.app.dto.UserResponse;
import com.example.app.entity.User;
import com.example.app.repository.UserRepository;
import com.example.app.twitch.TokenService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public AuthService(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        userRepository.save(user);

        return new AuthResponse("User registered successfully");
    }

    public AuthResponse login(LoginRequest request, HttpSession session) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!user.getPasswordHash().equals(request.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        session.setAttribute("userId", user.getId());

        return new AuthResponse("Login successful");
    }

    public UserResponse getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            throw new RuntimeException("Unauthorized");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

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

    public AuthResponse logout(HttpSession session) {
        session.invalidate();
        return new AuthResponse("Logout successful");
    }

    public AuthResponse updateProfile(UpdateProfileRequest request, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            throw new RuntimeException("Unauthorized");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

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

        if ((user.getClientId() == null || user.getClientId().isBlank())
                && request.getClientId() != null
                && !request.getClientId().isBlank()) {
            user.setClientId(request.getClientId());
        }

        if ((user.getClientSecret() == null || user.getClientSecret().isBlank())
                && request.getClientSecret() != null
                && !request.getClientSecret().isBlank()) {
            user.setClientSecret(request.getClientSecret());
        }

        if ((user.getAccessToken() == null || user.getAccessToken().isBlank())
                && request.getAccessToken() != null
                && !request.getAccessToken().isBlank()) {
            user.setAccessToken(request.getAccessToken());
        }

        if (user.getExpiredAt() == null && request.getExpiredAt() != null) {
            user.setExpiredAt(request.getExpiredAt());
        }

        userRepository.save(user);

        return new AuthResponse("Profile updated successfully");
    }

    public AuthResponse refreshToken(HttpSession session) {
        System.out.println("Refresh token service method called");
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            throw new RuntimeException("Unauthorized");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getClientId() == null || user.getClientId().isBlank()) {
            throw new RuntimeException("Client ID is required. Please add your Twitch Client ID in settings.");
        }
        if (user.getClientSecret() == null || user.getClientSecret().isBlank()) {
            throw new RuntimeException("Client Secret is required. Please add your Twitch Client Secret in settings.");
        }

        System.out.println("Forcing token refresh...");
        String newToken = tokenService.forceRefreshToken(user);
        System.out.println("New token received: " + (newToken != null ? "yes" : "no"));

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getClientId(),
                user.getClientSecret(),
                user.getAccessToken(),
                user.getExpiredAt()
        );

        return new AuthResponse("Token refreshed successfully", userResponse);
    }
}