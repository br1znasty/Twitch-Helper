export async function parseResponse(response) {
    try {
        return await response.json();
    } catch {
        return {};
    }
}