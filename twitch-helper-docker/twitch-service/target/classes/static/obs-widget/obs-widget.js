const WIDGET_CONFIGS = {
    viewers: {
        label: "Зрители",
        icon: "👀",
        metrics: ["status", "viewers"],
        render: renderViewers
    },

    status: {
        label: "Статус стрима",
        icon: "●",
        metrics: ["status"],
        render: renderStatus
    },

    game: {
        label: "Игра",
        icon: "🎮",
        metrics: ["status", "game"],
        render: renderGame
    },

    title: {
        label: "Название стрима",
        icon: "✦",
        metrics: ["status", "title"],
        render: renderTitle
    },

    "stream-time": {
        label: "Время стрима",
        icon: "⏱",
        metrics: ["status", "started_at"],
        render: renderStreamTime
    },

    followers: {
        label: "Фолловеры",
        icon: "★",
        metrics: ["followers"],
        render: renderFollowers
    },

    language: {
        label: "Язык стрима",
        icon: "🌐",
        metrics: ["status", "language"],
        render: renderLanguage
    },

    mature: {
        label: "18+",
        icon: "⚠",
        metrics: ["status", "is_mature"],
        render: renderMature
    },

    "stream-type": {
        label: "Тип трансляции",
        icon: "▣",
        metrics: ["status", "type"],
        render: renderStreamType
    },

    "display-name": {
        label: "Канал",
        icon: "☻",
        metrics: ["display_name"],
        render: renderDisplayName
    },

    description: {
        label: "Описание",
        icon: "☰",
        metrics: ["description"],
        render: renderDescription
    },

    summary: {
        label: "Стрим",
        icon: "▸",
        metrics: ["status", "viewers", "game", "title", "started_at"],
        render: renderSummary
    }
};

function detectWidgetTypeFromPath() {
    const parts = window.location.pathname.split("/");
    const lastPart = parts[parts.length - 1];

    if (!lastPart || lastPart === "widget") {
        return "status";
    }

    return lastPart;
}

const query = new URLSearchParams(window.location.search);

const widgetType = query.get("type") || detectWidgetTypeFromPath();
const userId = Number(query.get("userId"));
const channel = query.get("channel") || "";
const interval = normalizeInterval(Number(query.get("interval")));

const fontSize = query.get("fontSize");
const textColor = query.get("textColor");
const backgroundColor = query.get("backgroundColor");

const widgetElement = document.getElementById("widget");
const labelElement = document.getElementById("label");
const valueElement = document.getElementById("value");
const detailsElement = document.getElementById("details");

let startedAt = null;

const widgetConfig = WIDGET_CONFIGS[widgetType];

applyCustomStyles();

if (!widgetConfig) {
    setValue("Неизвестный виджет", widgetType, "error");
} else {
    labelElement.textContent = widgetConfig.label;
    loadStats();
    setInterval(loadStats, interval * 1000);

    if (widgetType === "stream-time") {
        setInterval(renderCurrentStreamTime, 1000);
    }
}

function applyCustomStyles() {
    if (fontSize) {
        document.documentElement.style.setProperty("--widget-font-size", safeCssValue(fontSize));
    }

    if (textColor) {
        document.documentElement.style.setProperty("--widget-text-color", safeCssValue(textColor));
    }

    if (backgroundColor) {
        document.documentElement.style.setProperty("--widget-background", safeCssValue(backgroundColor));
    }
}

function safeCssValue(value) {
    return value
        .replaceAll("<", "")
        .replaceAll(">", "")
        .replaceAll(";", "")
        .trim();
}

function normalizeInterval(value) {
    if (!value || value < 5) {
        return 30;
    }

    return Math.min(value, 600);
}

function setValue(value, details, className) {
    valueElement.textContent = value;
    detailsElement.textContent = details || "";
    widgetElement.className = "widget " + (className || "");
}

async function loadStats() {
    if (!userId || !channel) {
        setValue("Ошибка настроек", "Передай userId и channel", "error");
        return;
    }

    try {
        const response = await fetch("/api/twitch/statistics", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                userId: userId,
                channel: channel,
                metrics: widgetConfig.metrics
            })
        });

        const data = await response.json();

        if (!response.ok) {
            setValue("Ошибка", data.message || "Не удалось получить данные", "error");
            return;
        }

        widgetConfig.render(data.metrics || data.selectedMetrics || data.statistics || {});
    } catch (error) {
        setValue("Ошибка сети", "backend недоступен", "error");
    }
}

function isOffline(metrics) {
    return (metrics.status || "offline") === "offline";
}

function renderViewers(metrics) {
    if (isOffline(metrics)) {
        setValue("⚫ Оффлайн", "Зрителей: 0", "offline");
        return;
    }

    setValue("👀 " + (metrics.viewers ?? 0), "зрителей сейчас", "");
}

function renderStatus(metrics) {
    const status = metrics.status || "offline";

    if (status === "online") {
        setValue("🔴 ONLINE", channel, "");
    } else {
        setValue("⚫ OFFLINE", channel, "offline");
    }
}

function renderGame(metrics) {
    if (isOffline(metrics)) {
        setValue("⚫ Оффлайн", "категория недоступна", "offline");
        return;
    }

    setValue("🎮 " + (metrics.game || "Без категории"), "", "");
}

function renderTitle(metrics) {
    if (isOffline(metrics)) {
        setValue("⚫ Оффлайн", "название стрима недоступно", "offline");
        return;
    }

    setValue(metrics.title || "Без названия", "", "");
}

function renderStreamTime(metrics) {
    if (isOffline(metrics) || !metrics.started_at) {
        startedAt = null;
        setValue("⚫ Стрим не запущен", "", "offline");
        return;
    }

    startedAt = new Date(metrics.started_at);
    renderCurrentStreamTime();
}

function renderCurrentStreamTime() {
    if (!startedAt) {
        return;
    }

    const elapsed = (Date.now() - startedAt.getTime()) / 1000;
    setValue("⏱ " + formatSeconds(elapsed), "", "");
}

function renderFollowers(metrics) {
    setValue("★ " + (metrics.followers ?? 0), "фолловеров", "");
}

function renderLanguage(metrics) {
    if (isOffline(metrics)) {
        setValue("⚫ Оффлайн", "язык стрима недоступен", "offline");
        return;
    }

    setValue("🌐 " + (metrics.language || "-"), "", "");
}

function renderMature(metrics) {
    if (isOffline(metrics)) {
        setValue("⚫ Оффлайн", "", "offline");
        return;
    }

    if (metrics.is_mature) {
        setValue("18+", "контент для взрослых", "");
    } else {
        setValue("Без 18+", "обычный контент", "");
    }
}

function renderStreamType(metrics) {
    if (isOffline(metrics)) {
        setValue("⚫ Оффлайн", "", "offline");
        return;
    }

    setValue("▣ " + (metrics.type || "live"), "", "");
}

function renderDisplayName(metrics) {
    setValue("☻ " + (metrics.display_name || channel), "", "");
}

function renderDescription(metrics) {
    setValue(metrics.description || "Описание отсутствует", "", "");
}

function renderSummary(metrics) {
    if (isOffline(metrics)) {
        setValue("⚫ OFFLINE", channel, "offline");
        return;
    }

    const details = (metrics.game || "Без категории") + " · 👀 " + (metrics.viewers ?? 0);
    setValue("🔴 " + (metrics.title || "Стрим онлайн"), details, "");
}

function formatSeconds(totalSeconds) {
    const seconds = Math.max(0, Math.floor(totalSeconds));

    const h = String(Math.floor(seconds / 3600)).padStart(2, "0");
    const m = String(Math.floor((seconds % 3600) / 60)).padStart(2, "0");
    const s = String(seconds % 60).padStart(2, "0");

    return h + ":" + m + ":" + s;
}