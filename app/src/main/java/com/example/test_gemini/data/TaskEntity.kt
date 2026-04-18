package com.example.test_gemini.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val isCompleted: Boolean = false,
    val date: String, // формат "yyyy-MM-dd" – дата, на которую назначена задача
    val time: String? = null, // формат "HH:mm" – время начала
    val endTime: String? = null // формат "HH:mm" – время окончания (для интервала)
)