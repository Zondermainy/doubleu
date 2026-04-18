package com.example.test_gemini.data

data class WorkoutWithExercises(
    val workout: WorkoutEntity,
    val exercises: List<WorkoutExerciseWithDetails>,
    val completedCount: Int,
    val totalCount: Int
)

data class WorkoutExerciseWithDetails(
    val id: Long,
    val exerciseId: Long,
    val exerciseName: String,
    val muscleGroup: String,
    val sets: Int,
    val reps: Int,
    val isCompleted: Boolean
)