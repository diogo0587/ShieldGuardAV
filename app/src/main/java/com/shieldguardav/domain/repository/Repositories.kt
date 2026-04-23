package com.shieldguardav.domain.repository

import com.shieldguardav.domain.model.*
import kotlinx.coroutines.flow.Flow

interface AntivirusRepository {
    suspend fun scanFile(filePath: String): ScanResult?
    suspend fun scanPackage(packageName: String): ScanResult?
    suspend fun quickScan(): Flow<ScanProgress>
    suspend fun fullScan(): Flow<ScanProgress>
    suspend fun scanCustom(paths: List<String>): Flow<ScanProgress>
    fun getScanProgress(): Flow<ScanProgress>
    suspend fun cancelScan()
    suspend fun quarantineThreat(scanResult: ScanResult)
    suspend fun deleteThreat(scanResult: ScanResult)
    suspend fun restoreFromQuarantine(id: Long)
    fun getQuarantinedItems(): Flow<List<ScanResult>>
    fun getScanResults(): Flow<List<ScanResult>>
    fun getThreatsCount(): Flow<Int>
}

interface MalwareDatabaseRepository {
    suspend fun loadLocalSignatures()
    suspend fun updateSignaturesFromCloud(): Boolean
    fun getSignatures(): Flow<List<MalwareSignature>>
    suspend fun checkSignature(hash: String): MalwareSignature?
    fun getSignatureCount(): Flow<Int>
}

interface AppRepository {
    suspend fun getInstalledApps(): List<AppInfo>
    suspend fun getAppInfo(packageName: String): AppInfo?
    suspend fun analyzeAppRisks(appInfo: AppInfo): AppInfo
    suspend fun uninstallApp(packageName: String)
    fun getAppsFlow(): Flow<List<AppInfo>>
}

interface SecurityRepository {
    suspend fun getSecurityScore(): SecurityScore
    suspend fun checkWifiSecurity(): List<WifiSecurity>
    suspend fun getActiveConnections(): List<NetworkConnection>
    suspend fun scanJunkFiles(): List<JunkFile>
    suspend fun cleanJunkFiles(files: List<JunkFile>): Long
}

interface SettingsRepository {
    fun isAutoProtectionEnabled(): Boolean
    suspend fun setAutoProtection(enabled: Boolean)
    fun isNotificationsEnabled(): Boolean
    suspend fun setNotifications(enabled: Boolean)
fun isDarkModeEnabled(): Boolean
    suspend fun setDarkMode(enabled: Boolean)
    fun getLastScanTime(): Long
    suspend fun setLastScanTime(time: Long)
    fun getScanFrequency(): Int
    suspend fun setScanFrequency(frequency: Int)
}