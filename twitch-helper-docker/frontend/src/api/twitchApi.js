import { parseResponse } from "../utils/response";
import { API_BASE_URL } from "../constants/api";

export async function getTwitchStatistic(payload) {
    const response = await fetch(`${API_BASE_URL}/api/twitch/statistics`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify(payload)
    });

    const data = await parseResponse(response);
    return { response, data };
}