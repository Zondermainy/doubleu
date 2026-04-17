package com.example.test_gemini.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test_gemini.data.AppRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ProfileViewModel(private val repository: AppRepository) : ViewModel() {

    private val _stats = MutableLiveData<Stats>()
    val stats: LiveData<Stats> = _stats

    data class Stats(
        val completedTasksThisWeek: Int = 0,
        val totalTasksCreated: Int = 0,
        val currentStreak: Int = 0,
        val completedWorkouts: Int = 0,
        val totalCalories: Int = 0,
        val completedTasksTotal: Int = 0
    )

    fun loadStats() {
        viewModelScope.launch {
            // Получаем базовые числа
            val totalTasks = repository.getTotalTasksCreated()
            val completedTasks = repository.getCompletedTasksCount()
            val completedWorkouts = repository.getCompletedWorkoutsCount()
            val totalCalories = repository.getTotalCaloriesBurned()

            // Недельная статистика
            val weekStart = getDateDaysAgo(6)
            val weekEnd = getTodayString()
            val completedThisWeek = repository.getCompletedTasksCountForWeek(weekStart, weekEnd)

            // Стрик
            val streak = calculateStreak()

            _stats.value = Stats(
                completedTasksThisWeek = completedThisWeek,
                totalTasksCreated = totalTasks,
                currentStreak = streak,
                completedWorkouts = completedWorkouts,
                totalCalories = totalCalories,
                completedTasksTotal = completedTasks
            )
        }
    }

    private suspend fun calculateStreak(): Int {
        val history = repository.getAllHistory()
        if (history.isEmpty()) return 0

        // Сортируем даты по убыванию (сначала новые)
        val sortedDates = history.map { it.date }
            .sortedDescending()
            .map { SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(it)!! }

        val calendar = Calendar.getInstance()
        val today = calendar.apply {
            time = Date()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        // Проверяем, была ли активность сегодня
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(today)
        val todayActive = history.any { it.date == todayStr && (it.completedTaskIds != "[]" || it.completedWorkoutIds != "[]") }

        if (!todayActive) return 0 // если сегодня нет активности, стрик = 0

        var streak = 1
        var currentDate = today

        while (true) {
            // Вычитаем 1 день
            calendar.time = currentDate
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            currentDate = calendar.time
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(currentDate)

            val dayHistory = history.find { it.date == dateStr }
            val wasActive = dayHistory != null && (dayHistory.completedTaskIds != "[]" || dayHistory.completedWorkoutIds != "[]")

            if (wasActive) {
                streak++
            } else {
                break
            }
        }
        return streak
    }

    private fun getTodayString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    private fun getDateDaysAgo(days: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)
    }
}