package com.example.app.service;

import com.example.app.dto.AuthResponse;
import com.example.app.dto.LoginRequest;
import com.example.app.dto.RegisterRequest;
import com.example.app.entity.User;
import com.example.app.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Username is already taken");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Email is already taken");
        }

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        userRepository.save(user);

        return new AuthResponse("User registered successfully");
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!user.getPasswordHash().equals(request.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return new AuthResponse("Login successfully");
    }
}
