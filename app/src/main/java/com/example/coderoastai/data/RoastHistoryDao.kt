package com.example.coderoastai.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RoastHistoryDao {
    @Query("SELECT * FROM roast_history ORDER BY timestamp DESC LIMIT 10")
    fun getRecentHistory(): Flow<List<RoastHistoryEntity>>

    @Query("SELECT * FROM roast_history ORDER BY timestamp DESC LIMIT :limit")
    fun getHistory(limit: Int = 50): Flow<List<RoastHistoryEntity>>

    @Query("SELECT * FROM roast_history WHERE id = :id")
    suspend fun getHistoryById(id: Long): RoastHistoryEntity?

    @Query("SELECT * FROM roast_history WHERE language = :language ORDER BY timestamp DESC")
    fun getHistoryByLanguage(language: String): Flow<List<RoastHistoryEntity>>

    @Query("SELECT * FROM roast_history WHERE code LIKE '%' || :searchQuery || '%' ORDER BY timestamp DESC")
    fun searchHistory(searchQuery: String): Flow<List<RoastHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: RoastHistoryEntity): Long

    @Delete
    suspend fun delete(history: RoastHistoryEntity)

    @Query("DELETE FROM roast_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM roast_history")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM roast_history")
    suspend fun getCount(): Int

    @Query("DELETE FROM roast_history WHERE id IN (SELECT id FROM roast_history ORDER BY timestamp ASC LIMIT :count)")
    suspend fun deleteOldest(count: Int)
}
