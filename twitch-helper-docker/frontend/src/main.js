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

async function logout() {
  try {
    await fetch(`${API_BASE_URL}/api/auth/logout`, {
      method: "POST",
      credentials: "include"
    });
  } catch (error) {
  }

  navigate("#/login");
}

function renderWelcomePage() {
  render(`
    <div class="page auth-page">
      <div class="card auth-card">
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
    <div class="page auth-page">
      <div class="card auth-card">
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
        credentials: "include",
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

      navigate("#/login");
    } catch (error) {
      message.className = "error";
      message.textContent = "Не удалось подключиться к серверу";
    }
  });
}

function renderLoginPage() {
  render(`
    <div class="page auth-page">
      <div class="card auth-card">
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
        credentials: "include",
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

async function renderHomePage() {
  try {
    const response = await fetch(`${API_BASE_URL}/api/auth/me`, {
      method: "GET",
      credentials: "include"
    });

    const data = await parseResponse(response);

    if (!response.ok) {
      navigate("#/login");
      return;
    }

    render(`
      <div class="dashboard-page">
        <header class="dashboard-header">
          <div class="user-block">
            <div class="user-label">Пользователь</div>
            <div class="user-name">${data.username}</div>
          </div>

          <div class="header-actions">
            <button id="settings-button" class="secondary">Настройки</button>
            <button id="logout-button" class="secondary">Выход</button>
          </div>
        </header>

        <main class="dashboard-main">
          <section class="dashboard-card">
            <h1>Dashboard</h1>
            <p class="dashboard-subtitle">Основные функции приложения</p>

            <div class="dashboard-grid">
              <div class="dashboard-row">
                <button disabled>Получение статистики</button>
                <div class="stats-box">
                  <span class="stats-label">Количество зрителей:</span>
                  <span class="stats-value">0</span>
                </div>
              </div>

              <div class="dashboard-row">
                <button disabled>Создание печенек</button>
                <div class="placeholder-box">
                  Функция пока недоступна
                </div>
              </div>
            </div>
          </section>
        </main>
      </div>
    `);

    document.getElementById("logout-button").addEventListener("click", logout);
    document.getElementById("settings-button").addEventListener("click", () => {
      navigate("#/settings");
    });
  } catch (error) {
    navigate("#/login");
  }
}

async function renderSettingsPage() {
  try {
    const response = await fetch(`${API_BASE_URL}/api/auth/me`, {
      method: "GET",
      credentials: "include"
    });

    const data = await parseResponse(response);

    if (!response.ok) {
      navigate("#/login");
      return;
    }

    render(`
      <div class="dashboard-page">
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
            <p class="dashboard-subtitle">Данные пользователя</p>

            <form id="settings-form" class="settings-form">
              <label>
                Имя
                <input type="text" id="settings-username" value="${data.username}" />
              </label>

              <label>
                Email
                <input type="email" id="settings-email" value="${data.email}" />
              </label>

              <label>
                Пароль
                <input type="password" id="settings-password" placeholder="********" />
              </label>

              <label>
                Twitch token
                <input type="text" id="settings-twitch-token" value="" placeholder="Пока недоступно" disabled />
              </label>

              <label>
                OAuth token
                <input type="text" id="settings-oauth-token" value="" placeholder="Пока недоступно" disabled />
              </label>

              <button type="submit">Обновить</button>
            </form>

            <div id="settings-message"></div>
          </section>
        </main>
      </div>
    `);

    document.getElementById("back-button").addEventListener("click", () => {
      navigate("#/home");
    });

    document.getElementById("logout-button").addEventListener("click", logout);

    const form = document.getElementById("settings-form");
    const message = document.getElementById("settings-message");

    form.addEventListener("submit", async (event) => {
      event.preventDefault();

      message.className = "";
      message.textContent = "";

      const username = document.getElementById("settings-username").value.trim();
      const email = document.getElementById("settings-email").value.trim();
      const password = document.getElementById("settings-password").value.trim();

      try {
        const updateResponse = await fetch(`${API_BASE_URL}/api/auth/profile`, {
          method: "PUT",
          headers: {
            "Content-Type": "application/json"
          },
          credentials: "include",
          body: JSON.stringify({
            username,
            email,
            password
          })
        });

        const updateData = await parseResponse(updateResponse);

        if (!updateResponse.ok) {
          message.className = "error";
          message.textContent = updateData.message || "Ошибка обновления профиля";
          return;
        }

        message.className = "success";
        message.textContent = updateData.message || "Профиль обновлён";
      } catch (error) {
        message.className = "error";
        message.textContent = "Не удалось подключиться к серверу";
      }
    });
  } catch (error) {
    navigate("#/login");
  }
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

  if (route === "#/settings") {
    renderSettingsPage();
    return;
  }

  renderWelcomePage();
}

window.addEventListener("hashchange", router);
window.addEventListener("load", router);

router();