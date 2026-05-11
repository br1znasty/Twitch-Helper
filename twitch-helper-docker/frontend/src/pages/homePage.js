import { logoutUser } from "../api/authApi.js";
import { getCurrentUser } from "../api/userApi.js";
import { getTwitchStatistic } from "../api/twitchApi.js";
import { sendBotMessage, getChatMessages, getBotStatus } from "../api/botApi.js";
import { buildStatsHtml, getSelectedMetrics } from "../components/stats";
import { navigate, render } from "../utils/navigation";

export async function renderHomePage(app) {
  const { response, data } = await getCurrentUser();

  if (!response.ok) {
    navigate("#/login");
    return;
  }

  try {
    render(
      app,
      `<div class="dashboard-page">
        <header class="dashboard-header">
          <div class="user-block">
            <div class="user-label">Пользователь</div>
            <div class="user-name">${data.username}</div>
          </div>

          <div class="header-actions">
            <button id="widgets-button" class="secondary">Виджеты</button>
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
                      <input type="text" id="stats-channel" placeholder="Введите название канала" />
                    </label>

                    <div class="metrics-block">
                      <div class="metrics-title">Какие метрики показать</div>

                      <label class="checkbox-row"><input type="checkbox" name="metric" value="status" checked /><span>Статус стрима</span></label>
                      <label class="checkbox-row"><input type="checkbox" name="metric" value="language" /><span>Язык стрима</span></label>
                      <label class="checkbox-row"><input type="checkbox" name="metric" value="is_mature" /><span>Содержит ли что-то откровенное</span></label>
                      <label class="checkbox-row"><input type="checkbox" name="metric" value="type" /><span>Тип видеопотока</span></label>
                      <label class="checkbox-row"><input type="checkbox" name="metric" value="title" /><span>Название стрима</span></label>
                      <label class="checkbox-row"><input type="checkbox" name="metric" value="game" /><span>Категория</span></label>
                      <label class="checkbox-row"><input type="checkbox" name="metric" value="viewers" checked /><span>Количество зрителей</span></label>
                      <label class="checkbox-row"><input type="checkbox" name="metric" value="started_at" /><span>Время начала</span></label>
                      <label class="checkbox-row"><input type="checkbox" name="metric" value="display_name" checked /><span>Псевдоним стримера</span></label>
                      <label class="checkbox-row"><input type="checkbox" name="metric" value="description" checked /><span>Описание канала</span></label>
                      <label class="checkbox-row"><input type="checkbox" name="metric" value="followers" checked /><span>Количество подписчиков</span></label>
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
              <button id="bot-toggle-button">🤖 Управление ботом</button>
              <div id="bot-panel" class="bot-panel hidden">
                <div class="bot-status-bar">
                  <span class="bot-indicator offline" id="bot-indicator"></span>
                  <span id="bot-status-text">Проверка подключения...</span>
                  <button id="start-bot-button" class="small-btn" style="display:none;">Запустить</button>
                  <button id="stop-bot-button" class="small-btn danger" style="display:none;">Остановить</button>
                </div>
                
                <div class="bot-section">
                  <h4>📝 Отправить сообщение</h4>
                  <div class="send-message-row">
                    <input type="text" id="bot-message-input" placeholder="Текст сообщения..." />
                    <button id="send-message-button" class="small-btn">Отправить</button>
                  </div>
                  <div id="send-message-result" class="bot-message-result"></div>
                </div>
                
                <div class="bot-section">
                  <h4>💬 Последние сообщения чата</h4>
                  <div id="chat-messages" class="chat-messages-preview">
                    <div class="command-placeholder">Введите канал в блоке статистики и нажмите обновить</div>
                  </div>
                  <button id="refresh-chat-button" class="small-btn secondary" style="margin-top: 10px;">🔄 Обновить</button>
                </div>
              </div>
            </div>
          </div>
        </section>
      </main>
    </div>`
  );

    document.getElementById("widgets-button").addEventListener("click", () => {
      navigate("#/widgets");
    });

    document.getElementById("settings-button").addEventListener("click", () => {
      navigate("#/settings");
    });
    document.getElementById("logout-button").addEventListener("click", async () => {
        await logoutUser();
        navigate("#/login");
      });

    const statsToggleButton = document.getElementById("stats-toggle-button");
    const statsForm = document.getElementById("stats-form");
    const statsOutput = document.getElementById("stats-output");
    const statsMessage = document.getElementById("stats-message");
    const channelInput = document.getElementById("stats-channel");

    statsToggleButton.addEventListener("click", () => {
      statsForm.classList.toggle("hidden");
    });

    statsForm.addEventListener("submit", async (event) => {
      event.preventDefault();

      statsMessage.className = "";
      statsMessage.textContent = "";

      statsForm.style.border = "";
      channelInput.style.border = "";

      const channel = channelInput.value.trim();
      const metrics = getSelectedMetrics();

      if (!channel) {
        showError(statsMessage, statsOutput, "Введите название канала");
        channelInput.style.border = "1px solid #dc2626";
        return;
      }

      if (metrics.length === 0) {
        showError(statsMessage, statsOutput, "Ни одна метрика не выбрана");
        return;
      }

      try {
        statsOutput.innerHTML = `<div class="placeholder-box">Загрузка...</div>`;
        statsMessage.className = "info";
        statsMessage.textContent = "Получение данных...";

        const { response, data } = await getTwitchStatistic({
          channel,
          metrics,
        });

        if (!response.ok) {
          const errorMessage = data.message || "Не удалось получить статистику";
          showError(statsMessage, statsOutput, errorMessage);
          return;
        }

        statsOutput.innerHTML = buildStatsHtml(data.metrics);
        statsMessage.className = "success";
        statsMessage.textContent = "Статистика успешно загружена";

        channelInput.style.border = "";

        setTimeout(() => {
          if (statsMessage.className === "success") {
            statsMessage.className = "";
            statsMessage.textContent = "";
          }
        }, 3000);
        
      } catch (error) {
        console.error("Error fetching statistics:", error);
        showError(statsMessage, statsOutput, "Не удалось подключиться к серверу");
      }
    });

    const botToggleButton = document.getElementById("bot-toggle-button");
    const botPanel = document.getElementById("bot-panel");
    const sendMessageButton = document.getElementById("send-message-button");
    const botMessageInput = document.getElementById("bot-message-input");
    const sendMessageResult = document.getElementById("send-message-result");
    const refreshChatButton = document.getElementById("refresh-chat-button");
    const botIndicator = document.getElementById("bot-indicator");
    const botStatusText = document.getElementById("bot-status-text");

    async function updateBotStatus() {
      try {
        const { response, data } = await getBotStatus();
        if (response.ok && data) {
          botIndicator.className = `bot-indicator ${data.connected ? 'online' : 'offline'}`;
          botStatusText.textContent = data.connected ? 'Бот подключён' : 'Бот отключён';
        } else {
          botIndicator.className = 'bot-indicator offline';
          botStatusText.textContent = 'Статус недоступен';
        }
      } catch (error) {
        botIndicator.className = 'bot-indicator offline';
        botStatusText.textContent = 'Ошибка подключения';
      }
    }

    botToggleButton.addEventListener("click", () => {
      botPanel.classList.toggle("hidden");
      if (!botPanel.classList.contains("hidden")) {
        updateBotStatus();
        loadChatMessages();
      }
    });

    sendMessageButton.addEventListener("click", async () => {
      const message = botMessageInput.value.trim();
      if (!message) {
        sendMessageResult.textContent = "Введите сообщение";
        sendMessageResult.className = "bot-message-result error";
        return;
      }
    
      sendMessageResult.textContent = "Отправка...";
      sendMessageResult.className = "bot-message-result info";
    
      const { response, data } = await sendBotMessage(message);
      if (response.ok) {
        sendMessageResult.textContent = "✅ Сообщение отправлено!";
        sendMessageResult.className = "bot-message-result success";
        botMessageInput.value = "";
        setTimeout(() => loadChatMessages(), 500);
      } else {
         sendMessageResult.textContent = data.message || "❌ Ошибка отправки";
        sendMessageResult.className = "bot-message-result error";
      }
    
      setTimeout(() => {
        if (sendMessageResult.textContent !== "Отправка...") {
          setTimeout(() => { sendMessageResult.textContent = ""; }, 3000);
        }
      }, 2000);
    });

    async function loadChatMessages() {
      const channel = document.getElementById("stats-channel")?.value.trim() || "";
      if (!channel) {
        document.getElementById("chat-messages").innerHTML = `<div class="command-placeholder">Введите название канала в блоке статистики</div>`;
        return;
      }
    
      document.getElementById("chat-messages").innerHTML = `<div class="command-placeholder">Загрузка сообщений...</div>`;
      const { response, data } = await getChatMessages(channel, 20);
    
      if (response.ok && Array.isArray(data)) {
        if (data.length === 0) {
          document.getElementById("chat-messages").innerHTML = `<div class="command-placeholder">Пока нет сообщений в чате</div>`;
        } else {
          document.getElementById("chat-messages").innerHTML = data.map(msg => `
            <div class="chat-message-item">
              <strong>${escapeHtml(msg.username)}:</strong> ${escapeHtml(msg.message)}
              <span class="chat-time">${formatTime(msg.timestamp)}</span>
            </div>
          `).join("");
        }
      } else {
        document.getElementById("chat-messages").innerHTML = `<div class="command-placeholder">❌ Не удалось загрузить сообщения. Бот запущен?</div>`;
      }
    }

    if (refreshChatButton) {
      refreshChatButton.addEventListener("click", loadChatMessages);
    }

    let statusInterval = null;
    const observer = new MutationObserver(() => {
      if (!botPanel.classList.contains("hidden")) {
        if (!statusInterval) {
          updateBotStatus();
          statusInterval = setInterval(updateBotStatus, 30000);
        }
      } else {
        if (statusInterval) {
          clearInterval(statusInterval);
          statusInterval = null;
        }
      }
    });
    observer.observe(botPanel, { attributes: true, attributeFilter: ['class'] });
  } catch (error) {
    console.error("Error rendering home page:", error);
    navigate("#/login");
  }
}

function showError(messageElement, outputElement, errorText) {
  messageElement.className = "error";
  messageElement.textContent = errorText;
  outputElement.innerHTML = `<div class="placeholder-box">Ошибка загрузки данных</div>`;
}

function formatTime(timestamp) {
  if (!timestamp) return "";
  const date = new Date(timestamp);
  return date.toLocaleTimeString();
}

function escapeHtml(str) {
  if (!str) return "";
  return str.replace(/[&<>]/g, function(m) {
    if (m === "&") return "&amp;";
    if (m === "<") return "&lt;";
    if (m === ">") return "&gt;";
    return m;
  });
}