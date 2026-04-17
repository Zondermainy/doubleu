package com.example.test_gemini.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "training_plan_exercises",
    primaryKeys = ["planId", "exerciseId"],
    foreignKeys = [
        ForeignKey(
            entity = TrainingPlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["planId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("planId"), Index("exerciseId")]
)
data class TrainingPlanExerciseEntity(
    val planId: Long,
    val exerciseId: Long,
    val sets: Int = 3,
    val reps: Int = 10,
    val orderIndex: Int = 0
)
