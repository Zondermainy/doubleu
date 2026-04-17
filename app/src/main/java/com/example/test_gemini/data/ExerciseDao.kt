package com.example.test_gemini.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    @Query("SELECT * FROM exercises ORDER BY muscleGroup, name")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun getExerciseById(id: Long): ExerciseEntity?

    @Query("SELECT * FROM exercises WHERE muscleGroup = :muscleGroup ORDER BY name")
    fun getExercisesByMuscleGroup(muscleGroup: String): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE isDefault = 1")
    suspend fun getDefaultExercises(): List<ExerciseEntity>

    @Query("SELECT COUNT(*) FROM exercises WHERE isDefault = 1")
    suspend fun getDefaultExercisesCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercise: ExerciseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<ExerciseEntity>)

    @Update
    suspend fun update(exercise: ExerciseEntity)

    @Delete
    suspend fun delete(exercise: ExerciseEntity)

    @Query("DELETE FROM exercises WHERE isDefault = 0")
    suspend fun deleteCustomExercises()
}
