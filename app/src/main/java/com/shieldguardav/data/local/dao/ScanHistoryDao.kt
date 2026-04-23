package com.shieldguardav.data.local.dao

import androidx.room.*
import com.shieldguardav.data.local.entity.ScanHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanHistoryDao {
    @Query("SELECT * FROM scan_history ORDER BY startTime DESC")
    fun getAllHistory(): Flow<List<ScanHistoryEntity>>

    @Query("SELECT * FROM scan_history ORDER BY startTime DESC LIMIT :limit")
    suspend fun getRecentHistory(limit: Int): List<ScanHistoryEntity>

    @Query("SELECT * FROM scan_history WHERE id = :id")
    suspend fun getHistoryById(id: Long): ScanHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: ScanHistoryEntity): Long

    @Update
    suspend fun updateHistory(history: ScanHistoryEntity)

    @Delete
    suspend fun deleteHistory(history: ScanHistoryEntity)

    @Query("DELETE FROM scan_history WHERE id < :timestamp")
    suspend fun deleteOldHistory(timestamp: Long)
}