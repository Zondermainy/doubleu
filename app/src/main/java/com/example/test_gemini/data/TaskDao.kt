package com.example.test_gemini.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    // ===== Flow-запросы для наблюдения =====
    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE date = :date ORDER BY id DESC")
    fun getTasksByDate(date: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE date = :date AND time IS NULL ORDER BY id DESC")
    fun getTasksWithoutTimeByDate(date: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE date = :date AND time IS NOT NULL ORDER BY time ASC, endTime ASC")
    fun getTasksWithTimeByDate(date: String): Flow<List<TaskEntity>>

    // ===== Suspend-запросы для разовых операций =====
    @Query("SELECT * FROM tasks WHERE date = :date")
    suspend fun getTasksByDateSuspend(date: String): List<TaskEntity>

    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun getTotalTasksCount(): Int?

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 1")
    suspend fun getCompletedTasksCount(): Int?

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 1 AND date >= :weekStart AND date <= :weekEnd")
    suspend fun getCompletedTasksCountForWeek(weekStart: String, weekEnd: String): Int?

    // ===== Операции вставки/обновления/удаления =====
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("UPDATE tasks SET isCompleted = :completed WHERE id = :taskId")
    suspend fun updateCompletion(taskId: Long, completed: Boolean)

    @Query("DELETE FROM tasks")
    suspend fun deleteAll()
}