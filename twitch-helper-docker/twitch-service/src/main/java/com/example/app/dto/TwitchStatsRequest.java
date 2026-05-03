package com.example.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TwitchStatsRequest {
    private Long userId;

    @NotBlank(message = "Channel is required")
    private String channel;

    private List<String> metrics;
}