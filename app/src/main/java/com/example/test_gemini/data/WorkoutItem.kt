package com.example.test_gemini.data

data class WorkoutItem(
    val workout: WorkoutEntity,
    val exercises: List<WorkoutExerciseItem>,
    val completedCount: Int,
    val totalCount: Int
)