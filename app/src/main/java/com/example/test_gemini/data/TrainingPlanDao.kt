package com.example.test_gemini.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingPlanDao {

    @Query("SELECT * FROM training_plans ORDER BY createdAt ASC")
    fun getAllPlans(): Flow<List<TrainingPlanEntity>>

    @Query("SELECT * FROM training_plans ORDER BY createdAt ASC")
    suspend fun getAllPlansList(): List<TrainingPlanEntity>

    @Query("SELECT * FROM training_plans ORDER BY createdAt ASC")
    fun getAllPlansSync(): List<TrainingPlanEntity>

    @Query("SELECT * FROM training_plans WHERE id = :id")
    suspend fun getPlanById(id: Long): TrainingPlanEntity?

    @Query("""
        SELECT e.id, e.name, e.description, e.muscleGroup, e.isDefault,
               tpe.sets, tpe.reps
        FROM training_plan_exercises tpe
        INNER JOIN exercises e ON tpe.exerciseId = e.id
        WHERE tpe.planId = :planId
        ORDER BY tpe.orderIndex
    """)
    fun getExercisesForPlan(planId: Long): Flow<List<ExerciseWithDetails>>

    @Query("""
        SELECT e.id, e.name, e.description, e.muscleGroup, e.isDefault,
               tpe.sets, tpe.reps
        FROM training_plan_exercises tpe
        INNER JOIN exercises e ON tpe.exerciseId = e.id
        WHERE tpe.planId = :planId
        ORDER BY tpe.orderIndex
    """)
    suspend fun getExercisesForPlanSuspend(planId: Long): List<ExerciseWithDetails>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plan: TrainingPlanEntity): Long

    @Update
    suspend fun update(plan: TrainingPlanEntity)

    @Delete
    suspend fun delete(plan: TrainingPlanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlanExercise(planExercise: TrainingPlanExerciseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlanExercises(planExercises: List<TrainingPlanExerciseEntity>)

    @Query("DELETE FROM training_plan_exercises WHERE planId = :planId")
    suspend fun deleteExercisesFromPlan(planId: Long)

    @Query("DELETE FROM training_plan_exercises WHERE planId = :planId AND exerciseId = :exerciseId")
    suspend fun removeExerciseFromPlan(planId: Long, exerciseId: Long)

    @Query("UPDATE training_plan_exercises SET sets = :sets, reps = :reps WHERE planId = :planId AND exerciseId = :exerciseId")
    suspend fun updateExerciseSetsReps(planId: Long, exerciseId: Long, sets: Int, reps: Int)
}
