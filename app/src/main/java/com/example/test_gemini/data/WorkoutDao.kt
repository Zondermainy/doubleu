package com.example.test_gemini.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    // ===== Flow-запросы для наблюдения =====
    @Query("SELECT * FROM workouts ORDER BY id DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE date = :date")
    fun getWorkoutsByDate(date: String): Flow<List<WorkoutEntity>>

    // ===== Suspend-запросы для разовых операций =====
    @Query("SELECT * FROM workouts WHERE date = :date")
    suspend fun getWorkoutsByDateSuspend(date: String): List<WorkoutEntity>

    @Query("SELECT COUNT(*) FROM workouts")
    suspend fun getTotalWorkoutsCount(): Int?

    @Query("SELECT COUNT(*) FROM workouts WHERE isCompleted = 1")
    suspend fun getCompletedWorkoutsCount(): Int?

    @Query("SELECT SUM(caloriesBurned) FROM workouts WHERE isCompleted = 1")
    suspend fun getTotalCaloriesBurned(): Int?

    // ===== Операции вставки/обновления/удаления =====
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workout: WorkoutEntity): Long

    @Update
    suspend fun update(workout: WorkoutEntity)

    @Delete
    suspend fun delete(workout: WorkoutEntity)

    @Query("UPDATE workouts SET isCompleted = :completed WHERE id = :workoutId")
    suspend fun updateCompletion(workoutId: Long, completed: Boolean)

    @Query("DELETE FROM workouts")
    suspend fun deleteAll()
}