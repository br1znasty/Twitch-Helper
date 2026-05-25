package com.example.app.service;

import com.example.app.dto.AuthResponse;
import com.example.app.dto.UpdateProfileRequest;
import com.example.app.dto.UserResponse;
import com.example.app.entity.User;
import com.example.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User("testuser", "test@example.com", "password123");
        existingUser.setClientId("client-id");
        existingUser.setClientSecret("client-secret");
    }

    // --- getCurrentUser ---

    @Test
    void getCurrentUser_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        UserResponse response = userService.getCurrentUser(1L);

        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
    }

    @Test
    void getCurrentUser_throwsWhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.getCurrentUser(99L));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void getCurrentUser_throwsWhenUserIdIsNull() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.getCurrentUser(null));
        assertEquals("User id is required", ex.getMessage());
    }

    // --- updateProfile ---

    @Test
    void updateProfile_success() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setUserId(1L);
        request.setUsername("testuser");       // same username
        request.setEmail("test@example.com");  // same email
        request.setClientId("new-client-id");
        request.setClientSecret("new-secret");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        AuthResponse response = userService.updateProfile(request);

        assertNotNull(response);
        assertEquals("Profile updated successfully", response.getMessage());
        verify(userRepository).save(existingUser);
    }

    @Test
    void updateProfile_updatesPassword() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setUserId(1L);
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("newpassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        userService.updateProfile(request);

        assertEquals("newpassword", existingUser.getPasswordHash());
    }

    @Test
    void updateProfile_throwsWhenNewUsernameAlreadyTaken() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setUserId(1L);
        request.setUsername("occupieduser");
        request.setEmail("test@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsername("occupieduser")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.updateProfile(request));
        assertEquals("Username is already taken", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateProfile_throwsWhenNewEmailAlreadyInUse() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setUserId(1L);
        request.setUsername("testuser");
        request.setEmail("other@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("other@example.com")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.updateProfile(request));
        assertEquals("Email is already in use", ex.getMessage());
        verify(userRepository, never()).save(any());
    }
}
