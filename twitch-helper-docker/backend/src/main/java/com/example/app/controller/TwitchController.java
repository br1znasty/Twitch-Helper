package com.example.app.controller;

import com.example.app.dto.TwitchStatsRequest;
import com.example.app.dto.TwitchStatsResponse;
import com.example.app.service.TwitchService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/twitch")
public class TwitchController {
    private final TwitchService twitchService;

    public TwitchController(TwitchService twitchService) {
        this.twitchService = twitchService;
    }

    @PostMapping("/statistics")
    public ResponseEntity<TwitchStatsResponse> getStatistics(
            @Valid @RequestBody TwitchStatsRequest request,
            HttpSession session
    ) {
        TwitchStatsResponse response = twitchService.getStatistics(request, session);
        return ResponseEntity.ok(response);
    }
}