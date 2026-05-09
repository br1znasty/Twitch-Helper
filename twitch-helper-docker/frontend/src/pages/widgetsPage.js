import { logoutUser } from "../api/authApi.js";
import { getCurrentUser } from "../api/userApi.js";
import { navigate, render } from "../utils/navigation.js";

const WIDGETS = [
  {
    path: "viewers",
    title: "Счётчик зрителей",
    description: "Показывает текущее количество зрителей. Если канал оффлайн, показывает оффлайн-состояние.",
    interval: 30,
  },
  {
    path: "status",
    title: "Статус стрима",
    description: "Показывает ONLINE/OFFLINE.",
    interval: 30,
  },
  {
    path: "game",
    title: "Текущая игра",
    description: "Показывает текущую категорию стрима.",
    interval: 60,
  },
  {
    path: "title",
    title: "Название стрима",
    description: "Показывает текущий заголовок трансляции.",
    interval: 60,
  },
  {
    path: "stream-time",
    title: "Время стрима",
    description: "Показывает, сколько времени идёт стрим. При оффлайне пишет, что стрим не запущен.",
    interval: 30,
  },
  {
    path: "followers",
    title: "Фолловеры",
    description: "Показывает общее количество фолловеров канала.",
    interval: 60,
  },
  {
    path: "language",
    title: "Язык стрима",
    description: "Показывает язык текущей трансляции.",
    interval: 60,
  },
  {
    path: "mature",
    title: "18+ статус",
    description: "Показывает, отмечен ли стрим как mature-content.",
    interval: 60,
  },
  {
    path: "stream-type",
    title: "Тип трансляции",
    description: "Показывает тип видеопотока из Twitch API.",
    interval: 60,
  },
  {
    path: "display-name",
    title: "Имя канала",
    description: "Показывает display name канала.",
    interval: 300,
  },
  {
    path: "description",
    title: "Описание канала",
    description: "Показывает описание канала.",
    interval: 300,
  },
  {
    path: "summary",
    title: "Сводка стрима",
    description: "Показывает статус, название, игру и зрителей в одном виджете.",
    interval: 30,
  },
];

export async function renderWidgetsPage(app) {
  const { response, data } = await getCurrentUser();

  if (!response.ok) {
    navigate("#/login");
    return;
  }

  const userId = localStorage.getItem("userId") || "";

  render(
    app,
    `<div class="dashboard-page">
      <header class="dashboard-header">
        <div class="user-block">
          <div class="user-label">Пользователь</div>
          <div class="user-name">${escapeHtml(data.username)}</div>
        </div>

        <div class="header-actions">
          <button id="back-button" class="secondary">Назад</button>
          <button id="logout-button" class="secondary">Выход</button>
        </div>
      </header>

      <main class="dashboard-main">
        <section class="dashboard-card widgets-card">
          <h1>OBS-виджеты</h1>

          <form id="widget-links-form" class="widget-form simple-widget-form">
            <h2>Параметры ссылок</h2>

            <label>
              Канал Twitch
              <input type="text" id="widget-channel" placeholder="например: shroud" />
            </label>

            <label>
              Интервал обновления, секунд
              <input type="number" id="widget-interval" value="30" min="5" max="600" />
            </label>

            <label>
              Размер шрифта
              <input type="text" id="widget-font-size" value="42px" />
            </label>

            <label>
              Цвет текста
              <input type="text" id="widget-text-color" value="#ffffff" />
            </label>

            <label>
              Фон блока
              <input type="text" id="widget-background-color" value="rgba(15, 23, 42, 0.72)" />
            </label>

            <button type="submit">Сгенерировать ссылки</button>
            <div id="widgets-message"></div>
          </form>

          <div class="widgets-list-block full-width-widget-list">
            <div class="widgets-list-header">
              <h2>Ссылки для OBS Browser Source</h2>
            </div>

            <div id="widgets-list">
              ${buildWidgetCards(userId, "", 30, "42px", "#ffffff", "rgba(15, 23, 42, 0.72)")}
            </div>
          </div>
        </section>
      </main>
    </div>`
  );

  document.getElementById("back-button").addEventListener("click", () => navigate("#/home"));
  document.getElementById("logout-button").addEventListener("click", async () => {
    await logoutUser();
    navigate("#/login");
  });

  document.getElementById("widget-links-form").addEventListener("submit", (event) => {
    event.preventDefault();
    renderLinks();
  });

  bindWidgetButtons();
}

function renderLinks() {
  const userId = localStorage.getItem("userId") || "";
  const channel = document.getElementById("widget-channel").value.trim();
  const interval = document.getElementById("widget-interval").value.trim() || "30";
  const fontSize = document.getElementById("widget-font-size").value.trim() || "42px";
  const textColor = document.getElementById("widget-text-color").value.trim() || "#ffffff";
  const backgroundColor = document.getElementById("widget-background-color").value.trim() || "rgba(15, 23, 42, 0.72)";
  const message = document.getElementById("widgets-message");

  if (!channel) {
    message.className = "error";
    message.textContent = "Введите канал Twitch";
    return;
  }

  message.className = "success";
  message.textContent = "Ссылки обновлены";

  document.getElementById("widgets-list").innerHTML = buildWidgetCards(
    userId,
    channel,
    interval,
    fontSize,
    textColor,
    backgroundColor
  );

  bindWidgetButtons();
}

function buildWidgetCards(userId, channel, interval, fontSize, textColor, backgroundColor) {
  return WIDGETS.map((widget) => {
    const url = buildWidgetUrl(widget.path, userId, channel, interval || widget.interval, fontSize, textColor, backgroundColor);

    return `<div class="widget-card polling-widget-card">
      <div class="widget-card-header">
        <div>
          <div class="widget-type">${escapeHtml(widget.title)}</div>
          <div class="widget-token">/widget/${escapeHtml(widget.path)}</div>
        </div>
      </div>

      <p class="widget-description">${escapeHtml(widget.description)}</p>

      <label>
        Ссылка для OBS
        <input id="url-${escapeHtml(widget.path)}" value="${escapeHtml(url)}" readonly />
      </label>

      <div class="widget-actions">
        <button class="secondary copy-widget-url" data-path="${escapeHtml(widget.path)}">Скопировать ссылку</button>
        <button class="secondary open-widget-url" data-path="${escapeHtml(widget.path)}">Открыть</button>
      </div>
    </div>`;
  }).join("");
}

function buildWidgetUrl(path, userId, channel, interval, fontSize, textColor, backgroundColor) {
  const params = new URLSearchParams();
  params.set("userId", userId || "");
  params.set("channel", channel || "channel_name");
  params.set("interval", interval || "30");
  params.set("fontSize", fontSize || "42px");
  params.set("textColor", textColor || "#ffffff");
  params.set("backgroundColor", backgroundColor || "rgba(15, 23, 42, 0.72)");

  return `${window.location.origin}/widget/${path}?${params.toString()}`;
}

function bindWidgetButtons() {
  document.querySelectorAll(".copy-widget-url").forEach((button) => {
    button.addEventListener("click", async () => {
      const path = button.dataset.path;
      const input = document.getElementById(`url-${path}`);
      await navigator.clipboard.writeText(input.value);
      showMessage("success", "Ссылка скопирована");
    });
  });

  document.querySelectorAll(".open-widget-url").forEach((button) => {
    button.addEventListener("click", () => {
      const path = button.dataset.path;
      const input = document.getElementById(`url-${path}`);
      window.open(input.value, "_blank");
    });
  });
}

function showMessage(type, text) {
  const message = document.getElementById("widgets-message");
  if (!message) {
    return;
  }
  message.className = type;
  message.textContent = text;
}

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}
