import { navigate, render } from "../utils/navigation";

export function renderWelcomePage(app) {
    render(app, `<div class="page auth-page">
        <div class="card auth-card">
            <h1>Twitch Helper</h1>
            <p>Выберите действие</p>

            <div class="button-group">
                <button id="go-register">Регистрация</button>
                <button id="go-login" class="secondary">Вход</button>
            </div>
        </div>
    </div>`);

    document.getElementById("go-register").addEventListener("click", () => { navigate("#/register"); })
    document.getElementById("go-login").addEventListener("click", () => { navigate("#/login"); })
}