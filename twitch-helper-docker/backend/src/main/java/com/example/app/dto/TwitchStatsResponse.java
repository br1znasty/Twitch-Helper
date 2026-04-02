package com.example.app.dto;

import java.util.Map;

public class TwitchStatsResponse {
    private String channel;
    private Map<String, Object> metrics;

    public TwitchStatsResponse() {
    }

    public TwitchStatsResponse(String channel, Map<String, Object> metrics) {
        this.channel = channel;
        this.metrics = metrics;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Map<String, Object> getMetrics() {
        return metrics;
    }

    public void setMetrics(Map<String, Object> metrics) {
        this.metrics = metrics;
    }
}