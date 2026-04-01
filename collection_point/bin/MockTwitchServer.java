import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.RecordedRequest;

public class MockTwitchServer {
	private final MockWebServer server;

	public MockTwitchServer() throws Exception {
		server = new MockWebServer();

		server.setDispatcher(new Dispatcher() {
			@Override
			public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
				String path = request.getPath();
				System.out.println("Mock server received request: " + path);

				if (path.contains("streams")) {
					return new MockResponse()
						.setBody(FakeResponser.getStreamJson())
						.setHeader("Content-Type", "application/json");
				}
				else if (path.contains("users")) {
					return new MockResponse()
						.setBody(FakeResponser.getBroadcasterJson())
						.setHeader("Content-Type", "application/json");
				}
				else if (path.contains("followers")) {
					return new MockResponse()
						.setBody(FakeResponser.getFollowersJson())
						.setHeader("Content-Type", "application/json");
				}

				return new MockResponse().setResponseCode(404);
			}
		});

		server.start();
	}

	public String getBaseUrl() {
			return server.url("/").toString();
	}

	public void shutdown() throws Exception {
		server.shutdown();
	}
}