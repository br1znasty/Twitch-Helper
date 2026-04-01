import "./style.css";

const API_BASE_URL = "http://localhost:8081";

const app = document.getElementById("app");

function navigate(path) {
  window.location.hash = path;
}

function getRoute() {
  return window.location.hash || "#/";
}

function render(content) {
  app.innerHTML = content;
}

async function parseResponse(response) {
  try {
    return await response.json();
  } catch {
    return {};
  }
}

function renderWelcomePage() {
  render(`
    <div class="page">
      <div class="card">
        <h1>Twitch Helper</h1>
        <p>Выберите действие</p>

        <div class="button-group">
          <button id="go-register">Регистрация</button>
          <button id="go-login" class="secondary">Логин</button>
        </div>
      </div>
    </div>
  `);

  document.getElementById("go-register").addEventListener("click", () => {
    navigate("#/register");
  });

  document.getElementById("go-login").addEventListener("click", () => {
    navigate("#/login");
  });
}

function renderRegisterPage() {
  render(`
    <div class="page">
      <div class="card">
        <h2>Регистрация</h2>

        <form id="register-form">
          <label>
            Имя
            <input type="text" id="username" placeholder="Введите имя" />
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
    </div>
  `);

  const form = document.getElementById("register-form");
  const message = document.getElementById("register-message");

  form.addEventListener("submit", async (event) => {
    event.preventDefault();

    message.className = "";
    message.textContent = "";

    const username = document.getElementById("username").value.trim();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();

    try {
      const response = await fetch(`${API_BASE_URL}/api/auth/register`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          username,
          email,
          password
        })
      });

      const data = await parseResponse(response);

      if (!response.ok) {
        message.className = "error";
        message.textContent = data.message || "Ошибка регистрации";
        return;
      }

      navigate("#/home");
    } catch (error) {
      message.className = "error";
      message.textContent = "Не удалось подключиться к серверу";
    }
  });
}

function renderLoginPage() {
  render(`
    <div class="page">
      <div class="card">
        <h2>Логин</h2>

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
    </div>
  `);

  const form = document.getElementById("login-form");
  const message = document.getElementById("login-message");

  form.addEventListener("submit", async (event) => {
    event.preventDefault();

    message.className = "";
    message.textContent = "";

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();

    try {
      const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          email,
          password
        })
      });

      const data = await parseResponse(response);

      if (!response.ok) {
        message.className = "error";
        message.textContent = data.message || "Ошибка входа";
        return;
      }

      navigate("#/home");
    } catch (error) {
      message.className = "error";
      message.textContent = "Не удалось подключиться к серверу";
    }
  });
}

function renderHomePage() {
  render(`
    <div class="page">
      <div class="card">
        <h1>Привет!</h1>
        <p>Доступные функции приложения</p>

        <div class="button-group">
          <button disabled>Настройка виджетов</button>
          <button disabled>Накрутка сладостей</button>
        </div>
      </div>
    </div>
  `);
}

function router() {
  const route = getRoute();

  if (route === "#/register") {
    renderRegisterPage();
    return;
  }

  if (route === "#/login") {
    renderLoginPage();
    return;
  }

  if (route === "#/home") {
    renderHomePage();
    return;
  }

  renderWelcomePage();
}

window.addEventListener("hashchange", router);
window.addEventListener("load", router);

router();