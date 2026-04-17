package com.example.test_gemini.data

data class ExerciseWithDetails(
    val id: Long,
    val name: String,
    val description: String,
    val muscleGroup: String,
    val isDefault: Boolean,
    val sets: Int = 0,
    val reps: Int = 0
)
