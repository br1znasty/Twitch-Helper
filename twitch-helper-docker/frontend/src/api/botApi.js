import { parseResponse } from "../utils/response.js";

const CHAT_BOT_API_URL = "";

export async function sendBotMessage(message) {
  try {
    const response = await fetch(`${CHAT_BOT_API_URL}/api/chat/send`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ message }),
    });
    const data = await parseResponse(response);
    return { response, data };
  } catch (error) {
    console.error("Network error in sendBotMessage:", error);
    return {
      response: { ok: false, status: 0 },
      data: { message: "Ошибка подключения к серверу бота" }
    };
  }
}

export async function getChatMessages(channel, limit = 20) {
  try {
    const response = await fetch(
      `${CHAT_BOT_API_URL}/api/chat/messages?channel=${encodeURIComponent(channel)}&limit=${limit}`,
      { method: "GET", headers: { "Content-Type": "application/json" } }
    );
    const data = await parseResponse(response);
    return { response, data };
  } catch (error) {
    console.error("Network error in getChatMessages:", error);
    return { response: { ok: false, status: 0 }, data: [] };
  }
}

export async function getBotStatus() {
  try {
    const response = await fetch(`${CHAT_BOT_API_URL}/api/chat/status`, {
      method: "GET",
      headers: { "Content-Type": "application/json" },
    });
    const data = await parseResponse(response);
    return { response, data };
  } catch (error) {
    return { response: { ok: false }, data: { connected: false } };
  }
}