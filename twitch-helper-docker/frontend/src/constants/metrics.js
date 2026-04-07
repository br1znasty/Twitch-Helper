export const METRIC_LABELS = {
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

export function formatMetricValue(key, value) {
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