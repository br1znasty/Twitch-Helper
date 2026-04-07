import { registerUser } from "../api/authApi";
import { navigate, render } from "../utils/navigation";

export function renderRegisterPage(app) {
    render(app, `<div class="page auth-page">
        <div class="card auth-card">
            <h2>Регистрация</h2>

            <form id="register-form">
                <label>
                    Имя пользователя
                    <input type="text" id="username" placeholder="Введите имя пользователя" />
                </label>

                <label>
                    Email
                    <input type="email" id="email" placeholder="Введите email" />
                </label>

                <label>
                    Пароль
                    <input type="password" id="password" placeholder="Введите пароль" />
                </label>

                <button type="submit">Зарегистрироваться</button>
            </form>

            <div id="register-message"></div>

            <div class="link-row">
                <a href="#/">Назад</a>
            </div>
        </div>
    </div>`);

    const form = document.getElementById("register-form");
    const message = document.getElementById("register-message");

    form.addEventListener("submit", async (event) => {
        event.preventDefault();

        message.className = "";
        message.textContent = "";

        const payload = {
            username: document.getElementById("username").value.trim(),
            email: document.getElementById("email").value.trim(),
            password: document.getElementById("password").value.trim()
        };

        try {
            const { response, data } = await registerUser(payload);

            if (!response.ok) {
                message.className = "error";
                message.textContent = data.message || "Ошибка регистрации";

                return;
            }

            navigate("#/login");
        } catch {
            message.className = "error";
            message.textContent = "Не удалось подключиться к серверу";
        }
    });
}