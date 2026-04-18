package com.example.test_gemini.data

data class WorkoutExerciseItem(
    val id: Long,
    val workoutId: Long,
    val exerciseId: Long,
    val exerciseName: String,
    val muscleGroup: String,
    val sets: Int,
    val reps: Int,
    val isCompleted: Boolean
)