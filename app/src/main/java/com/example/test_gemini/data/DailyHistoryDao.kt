package com.example.test_gemini.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DailyHistoryDao {
    @Query("SELECT * FROM daily_history WHERE date = :date")
    suspend fun getHistoryForDate(date: String): DailyHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertOrUpdate(history: DailyHistoryEntity)

    @Query("SELECT * FROM daily_history")
    suspend fun getAllHistory(): List<DailyHistoryEntity>

    @Query("DELETE FROM daily_history")
    suspend fun deleteAll()
}