package com.example.app.controller;

import com.example.app.dto.AuthResponse;
import com.example.app.dto.UpdateProfileRequest;
import com.example.app.dto.UserIdRequest;
import com.example.app.dto.UserResponse;
import com.example.app.service.UserService;
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
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        userResponse = new UserResponse(
                null, "testuser", "test@example.com",
                "client-id", "client-secret", "token", null
        );
    }

    @Test
    void me_returns200WithUserResponse() {
        UserIdRequest request = new UserIdRequest();
        request.setUserId(1L);

        when(userService.getCurrentUser(1L)).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = userController.me(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("testuser", response.getBody().getUsername());
        assertEquals("test@example.com", response.getBody().getEmail());
        verify(userService).getCurrentUser(1L);
    }

    @Test
    void me_throwsWhenServiceThrows() {
        UserIdRequest request = new UserIdRequest();
        request.setUserId(99L);

        when(userService.getCurrentUser(99L)).thenThrow(new RuntimeException("User not found"));

        assertThrows(RuntimeException.class, () -> userController.me(request));
    }

    @Test
    void updateProfile_returns200OnSuccess() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setUserId(1L);
        request.setUsername("newname");
        request.setEmail("new@example.com");

        when(userService.updateProfile(any())).thenReturn(new AuthResponse("Profile updated successfully"));

        ResponseEntity<AuthResponse> response = userController.updateProfile(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Profile updated successfully", response.getBody().getMessage());
    }

    @Test
    void updateProfile_throwsWhenUsernameAlreadyTaken() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setUserId(1L);
        request.setUsername("takenuser");
        request.setEmail("test@example.com");

        when(userService.updateProfile(any())).thenThrow(new RuntimeException("Username is already taken"));

        assertThrows(RuntimeException.class, () -> userController.updateProfile(request));
    }
}
