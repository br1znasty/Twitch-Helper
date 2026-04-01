package com.example.app.API;

import java.net.URI;
import java.net.http.*;
import java.io.*;

import org.json.JSONObject;

public class TokenService {
	private final String clientId;
	private final String clientSecret;
	private String accessToken;
	private long expiresAt;

	private final File accessKeyFile = new File("AccessKey.txt");;

	public TokenService(String clientId, String clientSecret) throws IOException {
		this.clientId = clientId;
		this.clientSecret = clientSecret;

		try {
			accessKeyFile.createNewFile();
		}
		catch (IOException i) {
			throw new IOException("Cannot create accessKeyFile: " + i.getMessage());
		}

		tryGetSavedAccessDate();
	}

	public String getAccessToken() {
		if (accessToken == null || System.currentTimeMillis() > expiresAt)
			refreshToken();
		return accessToken;
	}

	private void tryGetSavedAccessDate() {
		try (BufferedReader reader = new BufferedReader(new FileReader(accessKeyFile))) {
			accessToken = reader.readLine();
			expiresAt = Long.parseLong(reader.readLine());
		}
		catch (IOException i) {
			System.out.println("Cannot read accessKey data: " + i.getMessage());
		}
		catch (NumberFormatException e) {
			System.out.println("Cannot parse accessKey time: " + e.getMessage());
		}
	}

	private void refreshToken() {
		try {
			HttpClient client = HttpClient.newHttpClient();

			String url = "https://id.twitch.tv/oauth2/token"
				+ "?client_id=" + clientId
				+ "&client_secret=" + clientSecret
				+ "&grant_type=client_credentials";

			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.POST(HttpRequest.BodyPublishers.noBody())
				.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			JSONObject accessResponse = new JSONObject(response.body());

			accessToken = accessResponse.getString("access_token");
			int expiresIn = accessResponse.getInt("expires_in");
			expiresAt = System.currentTimeMillis() + (expiresIn - 60) * 1000L;

			try (BufferedWriter writer = new BufferedWriter(new FileWriter(accessKeyFile))) {	
				writer.write(accessToken);
				writer.newLine();
				writer.write(String.valueOf(expiresAt));
				writer.newLine();
			}

			System.out.println("New token received");
		}
		catch (IOException i) {
			System.out.println("Cannot update file access data: " + i.getMessage());
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to get token: " + e.getMessage());
		}
	}
}