import { METRIC_LABELS, formatMetricValue } from "../constants/metrics";

export function buildStatsHtml(metrics) {
    const entries = Object.entries(metrics || {});

    if (entries.length === 0) {
        return `<div class="placeholder-box">Статистика пока не загружена</div>`;
    }

    return `<div class="stats-results">
        ${entries.map(([key, value]) => `<div class="stats-result-row">
        <span class="stats-result-key">${METRIC_LABELS[key] || key}</span>
        <span class="stats-result-value">${formatMetricValue(key, value)}</span>
        </div>
        `).join("")}
    </div>`;
}

export function getSelectedMetrics() {
    const checked = document.querySelectorAll('input[name="metric"]:checked');
    return Array.from(checked).map((item) => item.value);
}
