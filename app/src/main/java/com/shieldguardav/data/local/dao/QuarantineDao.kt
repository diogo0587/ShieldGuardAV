package com.shieldguardav.data.local.dao

import androidx.room.*
import com.shieldguardav.data.local.entity.QuarantineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuarantineDao {
    @Query("SELECT * FROM quarantine ORDER BY quarantineDate DESC")
    fun getAllQuarantined(): Flow<List<QuarantineEntity>>

    @Query("SELECT * FROM quarantine WHERE id = :id")
    suspend fun getQuarantinedById(id: Long): QuarantineEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuarantined(item: QuarantineEntity): Long

    @Delete
    suspend fun deleteQuarantined(item: QuarantineEntity)

    @Query("DELETE FROM quarantine WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM quarantine")
    fun getQuarantinedCount(): Flow<Int>
}