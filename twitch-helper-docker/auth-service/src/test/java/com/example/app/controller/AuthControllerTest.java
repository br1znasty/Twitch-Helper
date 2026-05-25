package com.example.app.controller;

import com.example.app.dto.AuthResponse;
import com.example.app.dto.LoginRequest;
import com.example.app.dto.RegisterRequest;
import com.example.app.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
    }

    @Test
    void register_returns200OnSuccess() {
        when(authService.register(any())).thenReturn(new AuthResponse("User registered successfully", 1L));

        ResponseEntity<AuthResponse> response = authController.register(registerRequest);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("User registered successfully", response.getBody().getMessage());
        assertEquals(1L, response.getBody().getUserId());
    }

    @Test
    void register_throwsWhenServiceThrows() {
        when(authService.register(any())).thenThrow(new RuntimeException("Username is already taken"));

        assertThrows(RuntimeException.class, () -> authController.register(registerRequest));
    }

    @Test
    void login_returns200OnSuccess() {
        when(authService.login(any())).thenReturn(new AuthResponse("Login successful", 1L));

        ResponseEntity<AuthResponse> response = authController.login(loginRequest);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Login successful", response.getBody().getMessage());
    }

    @Test
    void login_throwsWhenInvalidCredentials() {
        when(authService.login(any())).thenThrow(new RuntimeException("Invalid email or password"));

        assertThrows(RuntimeException.class, () -> authController.login(loginRequest));
    }
}