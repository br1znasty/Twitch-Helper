package com.example.app.service;

import com.example.app.dto.AuthResponse;
import com.example.app.dto.TwitchStatsRequest;
import com.example.app.dto.TwitchStatsResponse;
import com.example.app.entity.User;
import com.example.app.repository.UserRepository;
import com.example.app.twitch.TokenService;
import com.example.app.twitch.TwitchCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TwitchServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TwitchCollector twitchCollector;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private TwitchService twitchService;

    private User userWithCredentials;
    private User userWithoutCredentials;

    @BeforeEach
    void setUp() {
        userWithCredentials = new User("testuser", "test@example.com", "password");
        userWithCredentials.setClientId("client-id");
        userWithCredentials.setClientSecret("client-secret");

        userWithoutCredentials = new User("testuser", "test@example.com", "password");
    }

    // --- getStatistics ---

    @Test
    void getStatistics_success() {
        TwitchStatsRequest request = new TwitchStatsRequest();
        request.setUserId(1L);
        request.setChannel("somechannel");
        request.setMetrics(List.of("followers", "viewers"));

        Map<String, Object> fakeMetrics = Map.of("followers", 1000, "viewers", 50);

        when(userRepository.findById(1L)).thenReturn(Optional.of(userWithCredentials));
        when(twitchCollector.collectSelected(eq(userWithCredentials), eq("somechannel"), any()))
                .thenReturn(CompletableFuture.completedFuture(fakeMetrics));

        TwitchStatsResponse response = twitchService.getStatistics(request);

        assertNotNull(response);
        assertEquals("somechannel", response.getChannel());
        assertEquals(1000, response.getMetrics().get("followers"));
    }

    @Test
    void getStatistics_throwsWhenUserIdIsNull() {
        TwitchStatsRequest request = new TwitchStatsRequest();
        request.setUserId(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> twitchService.getStatistics(request));
        assertEquals("User id is required", ex.getMessage());
    }

    @Test
    void getStatistics_throwsWhenUserNotFound() {
        TwitchStatsRequest request = new TwitchStatsRequest();
        request.setUserId(99L);
        request.setMetrics(List.of("followers"));

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> twitchService.getStatistics(request));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void getStatistics_throwsWhenCredentialsNotConfigured() {
        TwitchStatsRequest request = new TwitchStatsRequest();
        request.setUserId(1L);
        request.setMetrics(List.of("followers"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(userWithoutCredentials));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> twitchService.getStatistics(request));
        assertTrue(ex.getMessage().contains("Twitch client credentials are not configured"));
    }

    @Test
    void getStatistics_throwsWhenMetricsIsEmpty() {
        TwitchStatsRequest request = new TwitchStatsRequest();
        request.setUserId(1L);
        request.setMetrics(List.of());

        when(userRepository.findById(1L)).thenReturn(Optional.of(userWithCredentials));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> twitchService.getStatistics(request));
        assertEquals("At least one metric must be selected", ex.getMessage());
    }

    // --- refreshToken ---

    @Test
    void refreshToken_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userWithCredentials));

        AuthResponse response = twitchService.refreshToken(1L);

        assertNotNull(response);
        assertEquals("Token refreshed successfully", response.getMessage());
        verify(tokenService, times(1)).forceRefreshToken(userWithCredentials);
    }

    @Test
    void refreshToken_throwsWhenUserIdIsNull() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> twitchService.refreshToken(null));
        assertEquals("User id is required", ex.getMessage());
    }

    @Test
    void refreshToken_throwsWhenClientIdMissing() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userWithoutCredentials));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> twitchService.refreshToken(1L));
        assertTrue(ex.getMessage().contains("Client ID is required"));
    }
}