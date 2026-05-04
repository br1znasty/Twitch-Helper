package com.example.app.service;

import com.example.app.dto.AuthResponse;
import com.example.app.dto.TwitchStatsRequest;
import com.example.app.dto.TwitchStatsResponse;
import com.example.app.dto.UserResponse;
import com.example.app.entity.User;
import com.example.app.repository.UserRepository;
import com.example.app.twitch.TokenService;
import com.example.app.twitch.TwitchCollector;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;

@Service
public class TwitchService {
    private final UserRepository userRepository;
    private final TwitchCollector twitchCollector;
    private final TokenService tokenService;

    public TwitchService(
            UserRepository userRepository,
            TwitchCollector twitchCollector,
            TokenService tokenService
    ) {
        this.userRepository = userRepository;
        this.twitchCollector = twitchCollector;
        this.tokenService = tokenService;
    }

    public TwitchStatsResponse getStatistics(TwitchStatsRequest request) {
        if (request.getUserId() == null) {
            throw new RuntimeException("User id is required");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getClientId() == null || user.getClientId().isBlank()
                || user.getClientSecret() == null || user.getClientSecret().isBlank()) {
            throw new RuntimeException("Twitch client credentials are not configured. Please add your Client ID and Client Secret in profile settings.");
        }

        List<String> metrics = request.getMetrics();

        if (metrics == null || metrics.isEmpty()) {
            throw new RuntimeException("At least one metric must be selected");
        }

        try {
            Map<String, Object> selectedMetrics = twitchCollector
                    .collectSelected(user, request.getChannel(), metrics)
                    .join();

            return new TwitchStatsResponse(request.getChannel(), selectedMetrics);
        } catch (CompletionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new RuntimeException("Failed to fetch Twitch statistics: " + cause.getMessage());
        }
    }

    public AuthResponse refreshToken(Long userId) {
        if (userId == null) {
            throw new RuntimeException("User id is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getClientId() == null || user.getClientId().isBlank()) {
            throw new RuntimeException("Client ID is required. Please add your Twitch Client ID in settings.");
        }

        if (user.getClientSecret() == null || user.getClientSecret().isBlank()) {
            throw new RuntimeException("Client Secret is required. Please add your Twitch Client Secret in settings.");
        }

        try {
            tokenService.forceRefreshToken(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to refresh token: " + e.getMessage());
        }

        UserResponse userResponse = toUserResponse(user);

        return new AuthResponse("Token refreshed successfully", userResponse);
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