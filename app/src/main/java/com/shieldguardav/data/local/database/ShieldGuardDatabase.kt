package com.shieldguardav.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shieldguardav.data.local.dao.MalwareSignatureDao
import com.shieldguardav.data.local.dao.QuarantineDao
import com.shieldguardav.data.local.dao.ScanHistoryDao
import com.shieldguardav.data.local.dao.ScanResultDao
import com.shieldguardav.data.local.entity.MalwareSignatureEntity
import com.shieldguardav.data.local.entity.QuarantineEntity
import com.shieldguardav.data.local.entity.ScanHistoryEntity
import com.shieldguardav.data.local.entity.ScanResultEntity

@Database(
    entities = [
        ScanResultEntity::class,
        MalwareSignatureEntity::class,
        ScanHistoryEntity::class,
        QuarantineEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ShieldGuardDatabase : RoomDatabase() {
    abstract fun scanResultDao(): ScanResultDao
    abstract fun malwareSignatureDao(): MalwareSignatureDao
    abstract fun scanHistoryDao(): ScanHistoryDao
    abstract fun quarantineDao(): QuarantineDao
}