# doubleu - Daily Planner & Workout Tracker

Android-приложение для планирования дня: задачи, расписание и тренировки в одном месте.

## Возможности

- **Задачи** — создание, редактирование и отметка выполненных задач на сегодня
- **Расписание** — задачи с привязкой ко времени (точка или интервал)
- **Тренировки** — создание тренировок с упражнениями, подходами и повторениями
- **Планы тренировок** — готовые шаблоны для быстрого создания тренировок
- **Заметки** — база упражнений с группировкой по мышечным группам
- **Календарь** — просмотр событий по датам
- **Профиль** — статистика выполненных задач и тренировок

## Технологии

- Kotlin
- Android Jetpack (Fragment, ViewModel, LiveData/Flow)
- Room Database
- Material Design 3
- ViewPager2 + TabLayout

## Структура

```
app/src/main/java/com/example/test_gemini/
├── data/          # Entity, DAO, Database, Repository
├── fragments/     # UI-логика (Tasks, Schedule, Workouts, Notes, Calendar, Profile)
└── adapters/      # RecyclerView adapters
```