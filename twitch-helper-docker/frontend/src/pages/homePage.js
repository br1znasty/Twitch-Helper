import { getCurrentUser, logoutUser } from "../api/authApi";
import { getTwitchStatistic } from "../api/twitchApi";
import { buildStatsHtml, getSelectedMetrics } from "../components/stats";
import { navigate, render } from "../utils/navigation";

export async function renderHomePage(app) {
    const { response, data } = await getCurrentUser();

    if (!response.ok) {
        navigate("#/login");

        return;
    }

    try {
        render(app, `<div class="dashboard-page">
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
                      <input type="text" id="stats-channel" placeholder="Введите название канала" />
                    </label>

                    <div class="metrics-block">
                      <div class="metrics-title">Какие показатели показать</div>

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
                <button disabled>Создание печенек</button>
                <div class="placeholder-box">Функция пока недоступна</div>
              </div>
            </div>
          </section>
        </main>
      </div>`);

        document.getElementById("settings-button").addEventListener("click", () => { navigate("#/settings"); })
        document.getElementById("logout-button").addEventListener("click", async () => {
            await logoutUser();
            navigate("#/login");
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
                statsMessage.textContent = "Не выбрана ни один показатель";

                return
            }

            try {
                statsOutput.innerHTML = `<div class="placeholder-box">Загрузка...</div>`;

                const { response, data } = await getTwitchStatistic({ channel, metrics });

                if (!response.ok) {
                    statsMessage.className = "error";
                    statsForm.textContent = data.message || "Не удалось получить статистику";
                    statsOutput.innerHTML = `<div class="placeholder-box">Нет данных</div>`;

                    return;
                }

                statsOutput.innerHTML = buildStatsHtml(data.metrics);
            } catch (error) {
                console.log(error);
                statsMessage.className = "error";
                statsMessage.textContent = "Не удалось подключиться к серверу";
                statsOutput.innerHTML = `<div class="placeholder-box">Нет данных</div>`;
            }
        });
    } catch {
        navigate("#/login");
    }
}