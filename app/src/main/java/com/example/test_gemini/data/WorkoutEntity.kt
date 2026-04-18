package com.example.test_gemini.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String, // Название тренировки
    val date: String, // "yyyy-MM-dd"
    val isCompleted: Boolean = false // Тренировка завершена
)