package com.example.test_gemini.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_history")
data class DailyHistoryEntity(
    @PrimaryKey
    val date: String, // "yyyy-MM-dd"
    val completedTaskIds: String = "", // JSON массив ID задач
    val completedWorkoutIds: String = "" // JSON массив ID тренировок
)