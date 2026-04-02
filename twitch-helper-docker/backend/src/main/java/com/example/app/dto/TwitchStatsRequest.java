package com.example.app.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class TwitchStatsRequest {
    @NotBlank(message = "Channel is required")
    private String channel;

    private List<String> metrics;

    public TwitchStatsRequest() {
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public List<String> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<String> metrics) {
        this.metrics = metrics;
    }
}