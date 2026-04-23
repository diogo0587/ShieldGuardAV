package com.shieldguardav.data.local.dao

import androidx.room.*
import com.shieldguardav.data.local.entity.ScanResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanResultDao {
    @Query("SELECT * FROM scan_results ORDER BY scanDate DESC")
    fun getAllScanResults(): Flow<List<ScanResultEntity>>

    @Query("SELECT * FROM scan_results WHERE status = :status ORDER BY scanDate DESC")
    fun getScanResultsByStatus(status: String): Flow<List<ScanResultEntity>>

    @Query("SELECT * FROM scan_results WHERE packageName = :packageName")
    suspend fun getScanResultByPackage(packageName: String): ScanResultEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScanResult(scanResult: ScanResultEntity): Long

    @Update
    suspend fun updateScanResult(scanResult: ScanResultEntity)

    @Delete
    suspend fun deleteScanResult(scanResult: ScanResultEntity)

    @Query("DELETE FROM scan_results")
    suspend fun deleteAllScanResults()

    @Query("SELECT COUNT(*) FROM scan_results WHERE status = 'FOUND'")
    fun getThreatsCount(): Flow<Int>
}