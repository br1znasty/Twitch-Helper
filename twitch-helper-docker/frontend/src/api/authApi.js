import { parseResponse } from "../utils/response.js";
import { AUTH_API_URL } from "../constants/api.js";

export async function registerUser(payload) {
  const response = await fetch(`${AUTH_API_URL}/api/auth/register`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  });

  const data = await parseResponse(response);
  return { response, data };
}

export async function loginUser(payload) {
  const response = await fetch(`${AUTH_API_URL}/api/auth/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  });

  const data = await parseResponse(response);
  return { response, data };
}

export function logoutUser() {
  localStorage.removeItem("userId");
}
