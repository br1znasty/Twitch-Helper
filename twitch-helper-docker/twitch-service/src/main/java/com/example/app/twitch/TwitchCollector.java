package com.example.app.twitch;

import com.example.app.entity.User;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Component
public class TwitchCollector {

    private static final Set<String> STREAM_METRICS = Set.of(
            "status",
            "language",
            "is_mature",
            "type",
            "title",
            "game",
            "viewers",
            "started_at"
    );

    private static final Set<String> USER_METRICS = Set.of(
            "display_name",
            "description"
    );

    private static final Set<String> FOLLOWER_METRICS = Set.of(
            "followers"
    );

    private final TokenService tokenService;
    private final HttpClient httpClient;

    public TwitchCollector(TokenService tokenService) {
        this.tokenService = tokenService;
        this.httpClient = HttpClient.newHttpClient();
    }

    protected String getApiBaseUrl() {
        return "https://api.twitch.tv/helix";
    }

    public CompletableFuture<Map<String, Object>> collectSelected(User user, String channel, List<String> metrics) {
        Set<String> requested = new HashSet<>(metrics);

        return checkChannelExists(user, channel)
                .thenCompose(channelExists -> {
                    if (!channelExists) {
                        throw new RuntimeException("Channel '" + channel + "' does not exist on Twitch");
                    }

                    CompletableFuture<Map<String, Object>> streamFuture =
                            needsStreamMetrics(requested)
                                    ? getStreamMetrics(user, channel, requested)
                                    : CompletableFuture.completedFuture(new HashMap<>());

                    CompletableFuture<Map<String, Object>> userFuture =
                            needsUserMetrics(requested)
                                    ? getUserMetrics(user, channel, requested)
                                    : CompletableFuture.completedFuture(new HashMap<>());

                    CompletableFuture<Map<String, Object>> followersFuture =
                            requested.contains("followers")
                                    ? getFollowersMetric(user, channel)
                                    : CompletableFuture.completedFuture(new HashMap<>());

                    return streamFuture
                            .thenCombine(userFuture, (streamMap, userMap) -> {
                                Map<String, Object> result = new HashMap<>();
                                result.putAll(streamMap);
                                result.putAll(userMap);
                                return result;
                            })
                            .thenCombine(followersFuture, (result, followersMap) -> {
                                result.putAll(followersMap);
                                return result;
                            });
                });
    }

    private CompletableFuture<Boolean> checkChannelExists(User user, String channel) {
        String token = tokenService.getAccessToken(user);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getApiBaseUrl() + "/users?login=" + channel))
                .header("Client-ID", user.getClientId())
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(body -> {
                    try {
                        JSONObject json = new JSONObject(body);
                        JSONArray data = json.getJSONArray("data");
                        return data.length() > 0;
                    } catch (Exception e) {
                        return false;
                    }
                });
    }

    private boolean needsStreamMetrics(Set<String> requested) {
        return requested.stream().anyMatch(STREAM_METRICS::contains);
    }

    private boolean needsUserMetrics(Set<String> requested) {
        return requested.stream().anyMatch(USER_METRICS::contains);
    }

    private CompletableFuture<Map<String, Object>> getStreamMetrics(User user, String channel, Set<String> requested) {
        String token = tokenService.getAccessToken(user);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getApiBaseUrl() + "/streams?user_login=" + channel))
                .header("Client-ID", user.getClientId())
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(body -> {
                    try {
                        Map<String, Object> result = new HashMap<>();

                        JSONObject json = new JSONObject(body);
                        JSONArray data = json.getJSONArray("data");

                        if (data.length() == 0) {
                            if (requested.contains("status")) {
                                result.put("status", "offline");
                            }

                            return result;
                        }

                        JSONObject stream = data.getJSONObject(0);

                        if (requested.contains("status")) {
                            result.put("status", "online");
                        }
                        if (requested.contains("language")) {
                            result.put("language", stream.optString("language", ""));
                        }
                        if (requested.contains("is_mature")) {
                            result.put("is_mature", stream.optBoolean("is_mature", false));
                        }
                        if (requested.contains("type")) {
                            result.put("type", stream.optString("type", ""));
                        }
                        if (requested.contains("title")) {
                            result.put("title", stream.optString("title", ""));
                        }
                        if (requested.contains("game")) {
                            result.put("game", stream.optString("game_name", ""));
                        }
                        if (requested.contains("viewers")) {
                            result.put("viewers", stream.optInt("viewer_count", 0));
                        }
                        if (requested.contains("started_at")) {
                            result.put("started_at", stream.optString("started_at", ""));
                        }

                        return result;
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to parse stream metrics: " + e.getMessage(), e);
                    }
                });
    }

    private CompletableFuture<Map<String, Object>> getUserMetrics(User user, String channel, Set<String> requested) {
        String token = tokenService.getAccessToken(user);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getApiBaseUrl() + "/users?login=" + channel))
                .header("Client-ID", user.getClientId())
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(body -> {
                    try {
                        Map<String, Object> result = new HashMap<>();

                        JSONObject json = new JSONObject(body);
                        JSONArray data = json.getJSONArray("data");

                        if (data.length() == 0) {
                            return result;
                        }

                        JSONObject userJson = data.getJSONObject(0);

                        if (requested.contains("display_name")) {
                            result.put("display_name", userJson.optString("display_name", ""));
                        }
                        if (requested.contains("description")) {
                            result.put("description", userJson.optString("description", ""));
                        }

                        return result;
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to parse user metrics: " + e.getMessage(), e);
                    }
                });
    }

    private CompletableFuture<Map<String, Object>> getFollowersMetric(User user, String channel) {
        return getBroadcasterId(user, channel).thenCompose(id -> {
            if (id == null) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("followers", 0);
                errorResult.put("error", "Channel not found");
                return CompletableFuture.completedFuture(errorResult);
            }

            String token = tokenService.getAccessToken(user);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getApiBaseUrl() + "/channels/followers?broadcaster_id=" + id))
                    .header("Client-ID", user.getClientId())
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenApply(body -> {
                        try {
                            JSONObject json = new JSONObject(body);
                            Map<String, Object> result = new HashMap<>();
                            result.put("followers", json.getInt("total"));
                            return result;
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to parse followers: " + e.getMessage(), e);
                        }
                    });
        });
    }

    private CompletableFuture<String> getBroadcasterId(User user, String channel) {
        String token = tokenService.getAccessToken(user);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getApiBaseUrl() + "/users?login=" + channel))
                .header("Client-ID", user.getClientId())
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(body -> {
                    try {
                        JSONObject json = new JSONObject(body);
                        JSONArray data = json.getJSONArray("data");
                        if (data.length() == 0) {
                            return null;
                        }
                        return data.getJSONObject(0).getString("id");
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to parse broadcaster id: " + e.getMessage(), e);
                    }
                });
    }
}