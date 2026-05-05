import { parseResponse } from "../utils/response.js";
import { TWITCH_API_URL } from "../constants/api.js";

function getStoredUserId() {
  return Number(localStorage.getItem("userId"));
}

export async function getTwitchStatistic(payload) {
  try {
    const response = await fetch(`${TWITCH_API_URL}/api/twitch/statistics`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        ...payload,
        userId: getStoredUserId(),
      }),
    });

    const data = await parseResponse(response);
    return { response, data };
  } catch (error) {
    console.error("Network error in getTwitchStatistic:", error);
    return {
      response: { ok: false, status: 0 },
      data: { message: "Сетевая ошибка. Проверьте подключение к серверу." }
    };
  }
}

export async function refreshToken() {
  try {
    const response = await fetch(`${TWITCH_API_URL}/api/twitch/refresh-token`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        userId: getStoredUserId(),
      }),
    });

    const data = await parseResponse(response);
    return { response, data };
  } catch (error) {
    console.error("Network error in refreshToken:", error);
    return {
      response: { ok: false, status: 0 },
      data: { message: "Сетевая ошибка. Проверьте подключение к серверу." }
    };
  }
}