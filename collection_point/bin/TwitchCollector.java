import java.net.URI;
import java.net.http.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import org.json.*;

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

				JSONObject json = new JSONObject(body);
				JSONArray data = json.getJSONArray("data");

				//System.out.println(json.toString(4));

				if (data.length() > 0) {
					JSONObject stream = data.getJSONObject(0);

					result.put("status", "online");
					result.put("viewers", stream.getInt("viewer_count"));
					result.put("title", stream.getString("title"));
					result.put("game", stream.getString("game_name"));
					result.put("started_at", stream.getString("started_at"));
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
				JSONObject json = new JSONObject(body);
				//System.out.println(json.toString(4));
				return json.getJSONArray("data")
					.getJSONObject(0)
					.getString("id");
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
					JSONObject json = new JSONObject(body);
					//System.out.println(json.toString(4));
					return json.getInt("total");
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