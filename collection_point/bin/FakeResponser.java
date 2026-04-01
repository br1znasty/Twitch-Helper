import org.json.*;

public class FakeResponser {
	public static String getStreamJson() {
		return """
		{
			"pagination": {"cursor": "eyJiIjp7IkN1cnNvciI6ImV5SnpJam94TXpBMk5pNDJOekl5TXpNNU9UWTRNellzSW1RaU9tWmhiSE5sTENKMElqcDBjblZsZlE9PSJ9LCJhIjp7IkN1cnNvciI6IiJ9fQ"},
			"data": [{
				"tag_ids": [],
				"user_name": "Mock",
				"language": "mo",
				"is_mature": false,
				"type": "live",
				"title": "Mocker mocks mockers",
				"thumbnail_url": "https://static-cdn.jtvnw.net/previews-ttv/live_user_mock-{width}x{height}.jpg",
				"tags": ["Русский"],
				"game_name": "MOCK",
				"user_id": "48189727",
				"user_login": "mock",
				"started_at": "2026-03-31T14:00:35Z",
				"id": "315772507732",
				"viewer_count": 13066,
				"game_id": "509658"
			}]
		}
		""";
	}

	public static String getBroadcasterJson() {
        	return """
		{"data": [{
			"broadcaster_type": "partner",
			"offline_image_url": "https://static-cdn.jtvnw.net/jtv_user_pictures/6bb903cb-1726-4818-8994-6abf33eb2ac2-channel_offline_image-1920x1080.png",
			"description": "Mock? MOCK!!!",
			"created_at": "2013-08-27T09:11:03Z",
			"profile_image_url": "https://static-cdn.jtvnw.net/jtv_user_pictures/c3279fd1-6f68-4f40-bb7a-c358a6f7a492-profile_image-300x300.png",
			"id": "48189727",
			"login": "mock",
			"display_name": "Mock",
			"type": "",
			"view_count": 0
		}]}
        	""";
	}

	public static String getFollowersJson() {
		return """
		{
			"total": 133713371337,
			"pagination": {},
			"data": []
		}
		""";
	}
}