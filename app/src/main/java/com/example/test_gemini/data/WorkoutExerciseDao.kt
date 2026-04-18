package com.example.test_gemini.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutExerciseDao {

    @Query("SELECT * FROM workout_exercises WHERE workoutId = :workoutId ORDER BY orderIndex")
    fun getExercisesForWorkout(workoutId: Long): Flow<List<WorkoutExerciseEntity>>

    @Query("SELECT * FROM workout_exercises WHERE workoutId = :workoutId ORDER BY orderIndex")
    suspend fun getExercisesForWorkoutList(workoutId: Long): List<WorkoutExerciseEntity>

    @Query("SELECT * FROM workout_exercises WHERE id = :id")
    suspend fun getById(id: Long): WorkoutExerciseEntity?

    @Query("SELECT COUNT(*) FROM workout_exercises WHERE workoutId = :workoutId")
    suspend fun getTotalExercisesCount(workoutId: Long): Int

    @Query("SELECT COUNT(*) FROM workout_exercises WHERE workoutId = :workoutId AND isCompleted = 1")
    suspend fun getCompletedExercisesCount(workoutId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workoutExercise: WorkoutExerciseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(workoutExercises: List<WorkoutExerciseEntity>)

    @Update
    suspend fun update(workoutExercise: WorkoutExerciseEntity)

    @Delete
    suspend fun delete(workoutExercise: WorkoutExerciseEntity)

    @Query("UPDATE workout_exercises SET isCompleted = :completed WHERE id = :id")
    suspend fun updateCompletion(id: Long, completed: Boolean)

    @Query("DELETE FROM workout_exercises WHERE workoutId = :workoutId")
    suspend fun deleteAllForWorkout(workoutId: Long)
}