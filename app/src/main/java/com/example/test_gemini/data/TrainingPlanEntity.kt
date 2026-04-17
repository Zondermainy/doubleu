package com.example.test_gemini.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "training_plans")
data class TrainingPlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
