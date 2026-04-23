package com.shieldguardav.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_results")
data class ScanResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val threatType: String,
    val threatName: String,
    val severity: String,
    val filePath: String,
    val scanDate: Long,
    val status: String
)

@Entity(tableName = "malware_signatures")
data class MalwareSignatureEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String,
    val signatureHash: String,
    val severity: String,
    val description: String,
    val category: String
)

@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val scanType: String,
    val startTime: Long,
    val endTime: Long = 0,
    val filesScanned: Int = 0,
    val threatsFound: Int = 0,
    val threatsCleaned: Int = 0,
    val status: String
)

@Entity(tableName = "quarantine")
data class QuarantineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val originalPath: String,
    val quarantinedPath: String,
    val packageName: String,
    val appName: String,
    val threatName: String,
    val quarantineDate: Long
)