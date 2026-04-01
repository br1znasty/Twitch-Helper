import java.io.*;

public class Main {
	public static void main(String[] args) {
		String clientId = "7id3zvr39mupx516lk2pvodoqe4lr1";
		final String channel = args.length > 1 ? args[0] : "T2x2";
		String clientSecret = "";

		boolean mock = args.length > 1 ? Boolean.parseBoolean(args[1]) : true;

		try {
			clientSecret = getSecret();

			TokenService tokenService = new TokenService(clientId, clientSecret);
			TwitchCollector collector;

			if (mock) {
				MockTwitchServer mockServer = new MockTwitchServer();
				System.out.println("Mock server started at: " + mockServer.getBaseUrl());

				collector = new TwitchCollector(clientId, channel, tokenService) {
					@Override
					protected String getApiBaseUrl() {
						return mockServer.getBaseUrl();
					}
				};


				collector.collectAll().thenAccept(data -> {
					System.out.println(channel + " info:");
					data.forEach((k, v) -> System.out.println(k + ": " + v));
				}).join();

				mockServer.shutdown();
			}
			else {
				collector = new TwitchCollector(clientId, channel, tokenService);
				collector.collectAll().thenAccept(data -> {
					System.out.println(channel + " info:");
					data.forEach((k, v) -> System.out.println(k + ": " + v));
				}).join();
			}
		}
		catch (IOException i) {
			System.out.println(i.getMessage());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getSecret() throws IOException {
		File secret = new File("SECRET.txt");
		try (BufferedReader reader = new BufferedReader(new FileReader(secret))) {
			return reader.readLine();
		}
		catch (IOException i) {
			throw new IOException("Cannot take secret: " + i.getMessage());
		}
	}
}