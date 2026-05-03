package com.example.app.twitch;

import com.example.app.entity.User;
import com.example.app.repository.UserRepository;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class TokenService {
    private final UserRepository userRepository;

    public TokenService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getAccessToken(User user) {
        if (user.getAccessToken() == null || user.getAccessToken().isEmpty()
                || user.getExpiredAt() == null
                || System.currentTimeMillis() > user.getExpiredAt()) {
            refreshToken(user);
        }

        return user.getAccessToken();
    }

    public String forceRefreshToken(User user) {
        refreshToken(user);
        return user.getAccessToken();
    }

    private void refreshToken(User user) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            String url = "https://id.twitch.tv/oauth2/token"
                    + "?client_id=" + user.getClientId()
                    + "&client_secret=" + user.getClientSecret()
                    + "&grant_type=client_credentials";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject accessResponse = new JSONObject(response.body());

            String accessToken = accessResponse.getString("access_token");
            int expiresIn = accessResponse.getInt("expires_in");
            long expiresAt = System.currentTimeMillis() + (expiresIn - 60) * 1000L;

            user.setAccessToken(accessToken);
            user.setExpiredAt(expiresAt);

            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get Twitch token: " + e.getMessage());
        }
    }
}