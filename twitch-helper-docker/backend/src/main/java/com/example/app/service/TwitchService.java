package com.example.app.service;

import com.example.app.dto.TwitchStatsRequest;
import com.example.app.dto.TwitchStatsResponse;
import com.example.app.entity.User;
import com.example.app.repository.UserRepository;
import com.example.app.twitch.TwitchCollector;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TwitchService {
    private final UserRepository userRepository;
    private final TwitchCollector twitchCollector;

    public TwitchService(UserRepository userRepository, TwitchCollector twitchCollector) {
        this.userRepository = userRepository;
        this.twitchCollector = twitchCollector;
    }

    public TwitchStatsResponse getStatistics(TwitchStatsRequest request, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            throw new RuntimeException("Unauthorized");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getClientId() == null || user.getClientId().isBlank()
                || user.getClientSecret() == null || user.getClientSecret().isBlank()) {
            throw new RuntimeException("Twitch client credentials are not configured");
        }

        List<String> metrics = request.getMetrics();

        if (metrics == null || metrics.isEmpty()) {
            throw new RuntimeException("At least one metric must be selected");
        }

        Map<String, Object> selectedMetrics = twitchCollector
                .collectSelected(user, request.getChannel(), metrics)
                .join();

        return new TwitchStatsResponse(request.getChannel(), selectedMetrics);
    }
}