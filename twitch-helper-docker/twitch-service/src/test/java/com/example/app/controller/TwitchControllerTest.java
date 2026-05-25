package com.example.app.controller;

import com.example.app.dto.AuthResponse;
import com.example.app.dto.TwitchStatsRequest;
import com.example.app.dto.TwitchStatsResponse;
import com.example.app.dto.UserIdRequest;
import com.example.app.service.TwitchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TwitchControllerTest {

    @Mock
    private TwitchService twitchService;

    @InjectMocks
    private TwitchController twitchController;

    private TwitchStatsRequest statsRequest;

    @BeforeEach
    void setUp() {
        statsRequest = new TwitchStatsRequest();
        statsRequest.setUserId(1L);
        statsRequest.setChannel("somechannel");
        statsRequest.setMetrics(List.of("followers", "viewers"));
    }

    @Test
    void getStatistics_returns200OnSuccess() {
        TwitchStatsResponse fakeResponse = new TwitchStatsResponse(
                "somechannel", Map.of("followers", 1000, "viewers", 50)
        );
        when(twitchService.getStatistics(any())).thenReturn(fakeResponse);

        ResponseEntity<TwitchStatsResponse> response = twitchController.getStatistics(statsRequest);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("somechannel", response.getBody().getChannel());
        assertEquals(1000, response.getBody().getMetrics().get("followers"));
    }

    @Test
    void getStatistics_throwsWhenServiceThrows() {
        when(twitchService.getStatistics(any()))
                .thenThrow(new RuntimeException("Twitch client credentials are not configured"));

        assertThrows(RuntimeException.class, () -> twitchController.getStatistics(statsRequest));
    }

    @Test
    void refreshToken_returns200OnSuccess() {
        UserIdRequest request = new UserIdRequest();
        request.setUserId(1L);

        when(twitchService.refreshToken(1L)).thenReturn(new AuthResponse("Token refreshed successfully"));

        ResponseEntity<AuthResponse> response = twitchController.refreshToken(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Token refreshed successfully", response.getBody().getMessage());
        verify(twitchService).refreshToken(1L);
    }

    @Test
    void refreshToken_throwsWhenUserNotFound() {
        UserIdRequest request = new UserIdRequest();
        request.setUserId(99L);

        when(twitchService.refreshToken(99L)).thenThrow(new RuntimeException("User not found"));

        assertThrows(RuntimeException.class, () -> twitchController.refreshToken(request));
    }
}
