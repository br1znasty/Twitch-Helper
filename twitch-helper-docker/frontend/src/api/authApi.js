import { parseResponse } from "../utils/response";
import { API_BASE_URL } from "../constants/api";

export async function registerUser(payload) {
    const response = await fetch(`${API_BASE_URL}/api/auth/register`, {
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

export async function loginUser(payload) {
    const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
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

export async function logoutUser() {
    const response = await fetch(`${API_BASE_URL}/api/auth/logout`, {
        method: "POST",
        credentials: "include"
    });

    const data = await parseResponse(response);
    return { response, data };
}

export async function getCurrentUser() {
    const response = await fetch(`${API_BASE_URL}/api/auth/me`, {
        method: "GET",
        credentials: "include"
    });

    const data = await parseResponse(response);
    return { response, data };
}

export async function updateProfile(payload) {
    const response = await fetch(`${API_BASE_URL}/api/auth/profile`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify(payload)
    });

    const data = await parseResponse(response);
    return { response, data };
}

export async function refreshToken() {
    console.log("Calling refresh token API at:", `${API_BASE_URL}/api/auth/refresh-token`);
    
    const response = await fetch(`${API_BASE_URL}/api/auth/refresh-token`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    });

    console.log("Refresh token response status:", response.status);
    
    const data = await parseResponse(response);
    console.log("Refresh token response data:", data);
    
    return { response, data };
}