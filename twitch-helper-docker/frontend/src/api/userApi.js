import { parseResponse } from "../utils/response.js";
import { USER_API_URL } from "../constants/api.js";

function getStoredUserId() {
  return Number(localStorage.getItem("userId"));
}

export async function getCurrentUser() {
  try {
    const response = await fetch(`${USER_API_URL}/api/users/me`, {
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
    console.error("Network error in getCurrentUser:", error);
    return {
      response: { ok: false, status: 0 },
      data: { message: "Сетевая ошибка. Проверьте подключение к серверу." }
    };
  }
}

export async function updateProfile(payload) {
  try {
    const response = await fetch(`${USER_API_URL}/api/users/profile`, {
      method: "PUT",
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
    console.error("Network error in updateProfile:", error);
    return {
      response: { ok: false, status: 0 },
      data: { message: "Сетевая ошибка. Проверьте подключение к серверу." }
    };
  }
}