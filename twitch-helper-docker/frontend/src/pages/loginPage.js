import { loginUser } from "../api/authApi";
import { navigate, render } from "../utils/navigation";

export function renderLoginPage(app) {
    render(app, `
    <div class="page auth-page">
      <div class="card auth-card">
        <h2>Вход</h2>

        <form id="login-form">
          <label>
            Email
            <input type="email" id="email" placeholder="Введите email" />
          </label>

          <label>
            Пароль
            <input type="password" id="password" placeholder="Введите пароль" />
          </label>

          <button type="submit">Войти</button>
        </form>

        <div id="login-message"></div>

        <div class="link-row">
          <a href="#/">Назад</a>
        </div>
      </div>
    </div>`);

    const form = document.getElementById("login-form");
    const message = document.getElementById("login-message");

    form.addEventListener("submit", async (event) => {
        event.preventDefault();

        message.className = "";
        message.textContent = "";

        const payload = {
            email: document.getElementById("email").value.trim(),
            password: document.getElementById("password").value.trim()
        };

        try {
            const { response, data } = await loginUser(payload);

            if (!response.ok) {
                message.className = "error";
                message.textContent = data.message || "Ошибка входа";

                return;
            }

            navigate("#/home");
        } catch {
            message.className = "error";
            message.textContent = "Не удалось подключиться к серверу";
        }
    });
}