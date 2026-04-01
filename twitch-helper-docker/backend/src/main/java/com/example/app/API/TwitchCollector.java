package com.example.app.API;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class TwitchCollector {
	private final String clientId;
	private final String channel;
	private final TokenService tokenService;
	private final HttpClient httpClient;

	public TwitchCollector(String clientId, String channel, TokenService tokenService) {
		this.clientId = clientId;
		this.channel = channel;
		this.tokenService = tokenService;
		this.httpClient = HttpClient.newHttpClient();
	}

	protected String getApiBaseUrl() {
		return "https://api.twitch.tv/helix";
	}

	public CompletableFuture<Map<String, Object>> getStreamInfo() {
		String token = tokenService.getAccessToken();

		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create(getApiBaseUrl() + "/streams?user_login=" + channel))
			.header("Client-ID", clientId)
			.header("Authorization", "Bearer " + token)
			.GET()
			.build();

		return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
			.thenApply(HttpResponse::body)
			.thenApply(body -> {
				Map<String, Object> result = new HashMap<>();

                JSONObject json = null;
                try {
                    json = new JSONObject(body);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                JSONArray data = null;
                try {
                    data = json.getJSONArray("data");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                //System.out.println(json.toString(4));

				if (data.length() > 0) {
                    JSONObject stream = null;
                    try {
                        stream = data.getJSONObject(0);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    result.put("status", "online");
                    try {
                        result.put("viewers", stream.getInt("viewer_count"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        result.put("title", stream.getString("title"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        result.put("game", stream.getString("game_name"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        result.put("started_at", stream.getString("started_at"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
				else {
					result.put("status", "offline");
					result.put("viewers", 0);
				}

				return result;
			});
	}

	public CompletableFuture<String> getBroadcasterId() {
		String token = tokenService.getAccessToken();

		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create(getApiBaseUrl() + "/users?login=" + channel))
			.header("Client-ID", clientId)
			.header("Authorization", "Bearer " + token)
			.GET()
			.build();

		return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
			.thenApply(HttpResponse::body)
			.thenApply(body -> {
                JSONObject json = null;
                try {
                    json = new JSONObject(body);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                //System.out.println(json.toString(4));
                try {
                    return json.getJSONArray("data")
                        .getJSONObject(0)
                        .getString("id");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
	}

	public CompletableFuture<Integer> getFollowers() {
		return getBroadcasterId().thenCompose(id -> {
			String token = tokenService.getAccessToken();

			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(getApiBaseUrl() + "/channels/followers?broadcaster_id=" + id))
				.header("Client-ID", clientId)
				.header("Authorization", "Bearer " + token)
				.GET()
				.build();

			return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
				.thenApply(HttpResponse::body)
				.thenApply(body -> {
                    JSONObject json = null;
                    try {
                        json = new JSONObject(body);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    //System.out.println(json.toString(4));
                    try {
                        return json.getInt("total");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });
			});
	}

	public CompletableFuture<Map<String, Object>> collectAll() {
		CompletableFuture<Map<String, Object>> streamFuture = getStreamInfo();
		CompletableFuture<Integer> followersFuture = getFollowers();

		return streamFuture.thenCombine(followersFuture, (stream, followers) -> {
			stream.put("followers", followers);
			return stream;
		});
	}
}