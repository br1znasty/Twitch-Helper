import { parseResponse } from "../utils/response.js";
import { AUTH_API_URL } from "../constants/api.js";

export async function registerUser(payload) {
  try {
    const response = await fetch(`${AUTH_API_URL}/api/auth/register`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    });

    const data = await parseResponse(response);
    return { response, data };
  } catch (error) {
    console.error("Network error in registerUser:", error);
    return {
      response: { ok: false, status: 0 },
      data: { message: "Сетевая ошибка. Проверьте подключение к серверу." }
    };
  }
}

export async function loginUser(payload) {
  try {
    const response = await fetch(`${AUTH_API_URL}/api/auth/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    });

    const data = await parseResponse(response);
    return { response, data };
  } catch (error) {
    console.error("Network error in loginUser:", error);
    return {
      response: { ok: false, status: 0 },
      data: { message: "Сетевая ошибка. Проверьте подключение к серверу." }
    };
  }
}

export function logoutUser() {
  localStorage.removeItem("userId");
}