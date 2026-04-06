import "./style.css";

const API_BASE_URL = "http://localhost:8081";

const app = document.getElementById("app");

const METRIC_LABELS = {
  status: "Статус стрима",
  language: "Язык стрима",
  is_mature: "Откровенный контент",
  type: "Тип видеопотока",
  title: "Название стрима",
  game: "Категория",
  viewers: "Количество зрителей",
  started_at: "Время начала",
  display_name: "Псевдоним стримера",
  description: "Описание канала",
  followers: "Количество подписчиков"
};

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
          <button id="go-login" class="secondary">Вход</button>
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

function formatMetricValue(key, value) {
  if (key === "is_mature") {
    return value ? "Да" : "Нет";
  }

  if (key === "status") {
    if (value === "online") return "Онлайн";
    if (value === "offline") return "Оффлайн";
  }

  if (value === null || value === undefined || value === "") {
    return "Нет данных";
  }

  return value;
}

function buildStatsHtml(metrics) {
  const entries = Object.entries(metrics || {});

  if (entries.length === 0) {
    return `<div class="placeholder-box">Статистика пока не загружена</div>`;
  }

  return `
    <div class="stats-results">
      ${entries
        .map(([key, value]) => {
          const label = METRIC_LABELS[key] || key;
          const formattedValue = formatMetricValue(key, value);

          return `
            <div class="stats-result-row">
              <span class="stats-result-key">${label}</span>
              <span class="stats-result-value">${formattedValue}</span>
            </div>
          `;
        })
        .join("")}
    </div>
  `;
}

function getSelectedMetrics() {
  const checked = document.querySelectorAll('input[name="metric"]:checked');
  return Array.from(checked).map((item) => item.value);
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
            <h1>Панель управления</h1>
            <p class="dashboard-subtitle">Основные функции приложения</p>

            <div class="dashboard-grid">
              <div class="dashboard-row top-align">
                <div class="stats-action-column">
                  <button id="stats-toggle-button">Получение статистики</button>

                  <form id="stats-form" class="stats-form hidden">
                    <label>
                      Канал
                      <input
                        type="text"
                        id="stats-channel"
                        placeholder="Введите название канала"
                      />
                    </label>

                    <div class="metrics-block">
                      <div class="metrics-title">Какие показатели показать</div>

                      <label class="checkbox-row">
                        <input type="checkbox" name="metric" value="status" checked />
                        <span>Статус стрима</span>
                      </label>

                      <label class="checkbox-row">
                        <input type="checkbox" name="metric" value="language" />
                        <span>Язык стрима</span>
                      </label>

                      <label class="checkbox-row">
                        <input type="checkbox" name="metric" value="is_mature" />
                        <span>Содержит ли что-то откровенное</span>
                      </label>

                      <label class="checkbox-row">
                        <input type="checkbox" name="metric" value="type" />
                        <span>Тип видеопотока</span>
                      </label>

                      <label class="checkbox-row">
                        <input type="checkbox" name="metric" value="title" />
                        <span>Название стрима</span>
                      </label>

                      <label class="checkbox-row">
                        <input type="checkbox" name="metric" value="game" />
                        <span>Категория</span>
                      </label>

                      <label class="checkbox-row">
                        <input type="checkbox" name="metric" value="viewers" checked />
                        <span>Количество зрителей</span>
                      </label>

                      <label class="checkbox-row">
                        <input type="checkbox" name="metric" value="started_at" />
                        <span>Время начала</span>
                      </label>

                      <label class="checkbox-row">
                        <input type="checkbox" name="metric" value="display_name" checked />
                        <span>Псевдоним стримера</span>
                      </label>

                      <label class="checkbox-row">
                        <input type="checkbox" name="metric" value="description" checked />
                        <span>Описание канала</span>
                      </label>

                      <label class="checkbox-row">
                        <input type="checkbox" name="metric" value="followers" checked />
                        <span>Количество подписчиков</span>
                      </label>
                    </div>

                    <button type="submit">Получить статистику</button>
                  </form>
                </div>

                <div class="stats-output-card">
                  <div class="stats-output-title">Статистика канала</div>
                  <div id="stats-output">
                    <div class="placeholder-box">Статистика пока не загружена</div>
                  </div>
                  <div id="stats-message"></div>
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

    const statsToggleButton = document.getElementById("stats-toggle-button");
    const statsForm = document.getElementById("stats-form");
    const statsOutput = document.getElementById("stats-output");
    const statsMessage = document.getElementById("stats-message");

    statsToggleButton.addEventListener("click", () => {
      statsForm.classList.toggle("hidden");
    });

    statsForm.addEventListener("submit", async (event) => {
      event.preventDefault();

      statsMessage.className = "";
      statsMessage.textContent = "";

      const channel = document.getElementById("stats-channel").value.trim();
      const metrics = getSelectedMetrics();

      if (!channel) {
        statsMessage.className = "error";
        statsMessage.textContent = "Введите название канала";
        return;
      }

      if (metrics.length === 0) {
        statsMessage.className = "error";
        statsMessage.textContent = "Выберите хотя бы один показатель";
        return;
      }

      try {
        statsOutput.innerHTML = `<div class="placeholder-box">Загрузка...</div>`;

        const statsResponse = await fetch(`${API_BASE_URL}/api/twitch/statistics`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json"
          },
          credentials: "include",
          body: JSON.stringify({
            channel,
            metrics
          })
        });

        const statsData = await parseResponse(statsResponse);

        if (!statsResponse.ok) {
          statsMessage.className = "error";
          statsMessage.textContent = statsData.message || "Не удалось получить статистику";
          statsOutput.innerHTML = `<div class="placeholder-box">Нет данных</div>`;
          return;
        }

        statsOutput.innerHTML = buildStatsHtml(statsData.metrics);
      } catch (error) {
        statsMessage.className = "error";
        statsMessage.textContent = "Не удалось подключиться к серверу";
        statsOutput.innerHTML = `<div class="placeholder-box">Нет данных</div>`;
      }
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
            <p class="dashboard-subtitle">Настройки пользователя</p>

            <form id="settings-form" class="settings-form">
              <label>
                Имя пользователя
                <input
                  type="text"
                  id="settings-username"
                  value="${data.username ?? ""}"
                />
              </label>

              <label>
                Email
                <input
                  type="email"
                  id="settings-email"
                  value="${data.email ?? ""}"
                />
              </label>

              <label>
                Пароль
                <input
                  type="password"
                  id="settings-password"
                  placeholder="********"
                />
              </label>

              <label>
                Client ID
                <input
                  type="text"
                  id="settings-client-id"
                  value="${data.clientId ?? ""}"
                  placeholder="Введите client_id"
                />
              </label>

              <label>
                Client Secret
                <input
                  type="text"
                  id="settings-client-secret"
                  value="${data.clientSecret ?? ""}"
                  placeholder="Введите client_secret"
                />
              </label>

              <label>
                Access Token
                <input
                  type="text"
                  id="settings-access-token"
                  value="${data.accessToken ?? ""}"
                  placeholder="Введите access_token"
                />
              </label>

              <label>
                Expired At
                <input
                  type="number"
                  id="settings-expired-at"
                  value="${data.expiredAt ?? ""}"
                  placeholder="Введите expired_at"
                />
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
      const clientId = document.getElementById("settings-client-id").value.trim();
      const clientSecret = document.getElementById("settings-client-secret").value.trim();
      const accessToken = document.getElementById("settings-access-token").value.trim();
      const expiredAtRaw = document.getElementById("settings-expired-at").value.trim();

      const expiredAt = expiredAtRaw === "" ? null : Number(expiredAtRaw);

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
            password,
            clientId,
            clientSecret,
            accessToken,
            expiredAt
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

        setTimeout(() => {
          renderSettingsPage();
        }, 400);
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