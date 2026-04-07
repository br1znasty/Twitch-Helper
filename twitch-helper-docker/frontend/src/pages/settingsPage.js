import { getCurrentUser } from "../api/authApi";
import { navigate, render } from "../utils/navigation";
import { updateProfile } from "../api/authApi";
import { timestamptoDatetimeLocal } from "../utils/date";

export async function renderSettingsPage(app) {
    try {
        const { response, data } = await getCurrentUser();

        if (!response.ok) {
            navigate("#/login");

            return;
        }

        render(app, `<div class="dashboard-page">
        <header class="dashboard-header">
          <div class="user-block">
            <div class="user-label">Пользователь</div>
            <div class="user-name">${data.username}</div>
          </div>

          <div class="header-actions">
            <button id="back-button" class="secondary">Назад</button>
            <button id="logout-button" class="secondary">Выход</button>
          </div>
        </header>

        <main class="dashboard-main">
          <section class="dashboard-card settings-card">
            <h1>Настройки</h1>
            <p class="dashboard-subtitle">Настройки пользователя</p>

            <form id="settings-form" class="settings-form">
              <label>
                Имя пользователя
                <input type="text" id="settings-username" value="${data.username ?? ""}" />
              </label>

              <label>
                Email
                <input type="email" id="settings-email" value="${data.email ?? ""}" />
              </label>

              <label>
                Пароль
                <input type="password" id="settings-password" placeholder="********" />
              </label>

              <label>
                Client ID
                <input type="text" id="settings-client-id" value="${data.clientId ?? ""}" placeholder="Введите client id" />
              </label>

              <label>
                Client Secret
                <input type="text" id="settings-client-secret" value="${data.clientSecret ?? ""}" placeholder="Введите client secret" />
              </label>

              <label>
                Access Token
                <input type="text" id="settings-access-token" value="${data.accessToken ?? ""}" placeholder="Получите access token" />
              </label>

              <label>
                Дата истечения токена
                <input type="datetime-local" id="settings-expired-at" value="${timestamptoDatetimeLocal(data.expiredAt)}" placeholder="Дата истечения" />
              </label>

              <button type="submit">Обновить</button>
            </form>

            <div id="settings-message"></div>
          </section>
        </main>
      </div>`);

        document.getElementById("back-button").addEventListener("click", () => { navigate("#/home"); })
        document.getElementById("logout-button").addEventListener("click", async () => {
            await logoutUser();
            navigate("#/login");
        });

        const form = document.getElementById("settings-form");
        const message = document.getElementById("settings-message");

        form.addEventListener("submit", async (event) => {
            event.preventDefault();

            message.className = "";
            message.textContent = "";

            const expiredAtRaw = document.getElementById("settings-expired-at").value.trim();

            const payload = {
                username: document.getElementById("settings-username").value.trim(),
                email: document.getElementById("settings-email").value.trim(),
                password: document.getElementById("settings-password").value.trim(),
                clientId: document.getElementById("settings-client-id").value.trim(),
                clientSecret: document.getElementById("settings-client-secret").value.trim(),
                accessToken: document.getElementById("settings-access-token").value.trim(),
                expiredAt: expiredAtRaw === "" ? null : Number(expiredAtRaw)
            };

            try {
                const { response: updateResponse, data: updateData } = await updateProfile(payload);

                if (!updateResponse.ok) {
                    message.className = "error";
                    message.textContent = updateData.message || "Ошибка обновления профиля";
                    return;
                }

                message.className = "success";
                message.textContent = updateData.message || "Профиль обновлён";

                setTimeout(() => {
                    renderSettingsPage(app);
                }, 400);
            } catch (error) {
                console.log(error);
                message.className = "error";
                message.textContent = "Не удалось подключиться к серверу";
            }
        });
    } catch {
        navigate("#/login");
    }
}