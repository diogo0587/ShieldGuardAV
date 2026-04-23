package com.shieldguardav.data.repository

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.shieldguardav.data.local.dao.QuarantineDao
import com.shieldguardav.data.local.dao.ScanHistoryDao
import com.shieldguardav.data.local.dao.ScanResultDao
import com.shieldguardav.data.local.entity.QuarantineEntity
import com.shieldguardav.data.local.entity.ScanHistoryEntity
import com.shieldguardav.data.local.entity.ScanResultEntity
import com.shieldguardav.domain.model.*
import com.shieldguardav.domain.repository.AntivirusRepository
import com.shieldguardav.domain.repository.MalwareDatabaseRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AntivirusRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val scanResultDao: ScanResultDao,
    private val scanHistoryDao: ScanHistoryDao,
    private val quarantineDao: QuarantineDao,
    private val malwareDatabaseRepository: MalwareDatabaseRepository
) : AntivirusRepository {

    private val packageManager: PackageManager = context.packageManager
    private val isScanning = AtomicBoolean(false)
    private val _scanProgress = MutableStateFlow(ScanProgress("", 0, 0, 0))

    override suspend fun scanFile(filePath: String): ScanResult? = withContext(Dispatchers.IO) {
        val file = File(filePath)
        if (!file.exists()) return@withContext null

        val hash = calculateMD5(file)
        val signature = malwareDatabaseRepository.checkSignature(hash)

        if (signature != null) {
            ScanResult(
                packageName = filePath,
                appName = file.name,
                threatType = signature.type,
                threatName = signature.name,
                severity = signature.severity,
                filePath = filePath,
                status = ThreatStatus.FOUND
            )
        } else {
            null
        }
    }

    override suspend fun scanPackage(packageName: String): ScanResult? = withContext(Dispatchers.IO) {
        try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong()))
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            }

            val apkPath = packageInfo.applicationInfo?.sourceDir ?: return@withContext null
            val file = File(apkPath)
            val hash = calculateMD5(file)
            val signature = malwareDatabaseRepository.checkSignature(hash)

            if (signature != null) {
                ScanResult(
                    packageName = packageName,
                    appName = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString(),
                    threatType = signature.type,
                    threatName = signature.name,
                    severity = signature.severity,
                    filePath = apkPath,
                    status = ThreatStatus.FOUND
                )
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun quickScan(): Flow<ScanProgress> = flow {
        isScanning.set(true)
        _scanProgress.value = ScanProgress("", 0, 0, 0, isRunning = true)

        val installedApps = packageManager.getInstalledPackages(0)
        val totalFiles = installedApps.size

        emit(_scanProgress.value.copy(totalFiles = totalFiles))

        var scanned = 0
        var threats = 0
        val threatsList = mutableListOf<ScanResult>()

        installedApps.forEach { packageInfo ->
            if (!isScanning.get()) {
                emit(_scanProgress.value.copy(isComplete = true, isRunning = false))
                return@flow
            }

            scanned++
            _scanProgress.value = _scanProgress.value.copy(
                currentFile = packageInfo.packageName,
                scannedFiles = scanned
            )
            emit(_scanProgress.value)

            val result = scanPackage(packageInfo.packageName)
            if (result != null) {
                threats++
                threatsList.add(result)
                scanResultDao.insertScanResult(result.toEntity())
            }
        }

        isScanning.set(false)
        emit(_scanProgress.value.copy(
            isComplete = true,
            isRunning = false,
            threatsFound = threats
        ))
    }.flowOn(Dispatchers.IO)

    override suspend fun fullScan(): Flow<ScanProgress> = flow {
        isScanning.set(true)
        _scanProgress.value = ScanProgress("", 0, 0, 0, isRunning = true)

        val installedApps = packageManager.getInstalledPackages(0)
        val totalFiles = installedApps.size

        emit(_scanProgress.value.copy(totalFiles = totalFiles))

        var scanned = 0
        var threats = 0

        installedApps.forEach { packageInfo ->
            if (!isScanning.get()) {
                emit(_scanProgress.value.copy(isComplete = true, isRunning = false))
                return@flow
            }

            scanned++
            _scanProgress.value = _scanProgress.value.copy(
                currentFile = packageInfo.packageName,
                scannedFiles = scanned
            )
            emit(_scanProgress.value)

            val result = scanPackage(packageInfo.packageName)
            if (result != null) {
                threats++
                scanResultDao.insertScanResult(result.toEntity())
            }
        }

        val history = ScanHistoryEntity(
            scanType = ScanType.FULL.name,
            startTime = System.currentTimeMillis() - (totalFiles * 100),
            endTime = System.currentTimeMillis(),
            filesScanned = scanned,
            threatsFound = threats,
            status = ScanStatus.COMPLETED.name
        )
        scanHistoryDao.insertHistory(history)

        isScanning.set(false)
        emit(_scanProgress.value.copy(
            isComplete = true,
            isRunning = false,
            threatsFound = threats
        ))
    }.flowOn(Dispatchers.IO)

    override suspend fun scanCustom(paths: List<String>): Flow<ScanProgress> = flow {
        isScanning.set(true)
        val totalFiles = paths.size

        emit(_scanProgress.value.copy(totalFiles = totalFiles, isRunning = true))

        var scanned = 0
        var threats = 0

        paths.forEach { path ->
            if (!isScanning.get()) {
                emit(_scanProgress.value.copy(isComplete = true, isRunning = false))
                return@flow
            }

            scanned++
            _scanProgress.value = _scanProgress.value.copy(
                currentFile = path,
                scannedFiles = scanned
            )
            emit(_scanProgress.value)

            val result = scanFile(path)
            if (result != null) {
                threats++
                scanResultDao.insertScanResult(result.toEntity())
            }
        }

        isScanning.set(false)
        emit(_scanProgress.value.copy(
            isComplete = true,
            isRunning = false,
            threatsFound = threats
        ))
    }.flowOn(Dispatchers.IO)

    override fun getScanProgress(): Flow<ScanProgress> = _scanProgress.asStateFlow()

    override suspend fun cancelScan() {
        isScanning.set(false)
        _scanProgress.value = _scanProgress.value.copy(isRunning = false)
    }

    override suspend fun quarantineThreat(scanResult: ScanResult) {
        val entity = QuarantineEntity(
            originalPath = scanResult.filePath,
            quarantinedPath = scanResult.filePath,
            packageName = scanResult.packageName,
            appName = scanResult.appName,
            threatName = scanResult.threatName,
            quarantineDate = System.currentTimeMillis()
        )
        quarantineDao.insertQuarantined(entity)

        val updatedResult = scanResult.copy(status = ThreatStatus.QUARANTINED)
        scanResultDao.updateScanResult(updatedResult.toEntity())
    }

    override suspend fun deleteThreat(scanResult: ScanResult) {
        try {
            val file = File(scanResult.filePath)
            if (file.exists() && !file.isDirectory) {
                file.delete()
            }
            val updatedResult = scanResult.copy(status = ThreatStatus.DELETED)
            scanResultDao.updateScanResult(updatedResult.toEntity())
        } catch (_: Exception) { }
    }

    override suspend fun restoreFromQuarantine(id: Long) {
        quarantineDao.deleteById(id)
    }

    override fun getQuarantinedItems(): Flow<List<ScanResult>> {
        return quarantineDao.getAllQuarantined().map { entities ->
            entities.map { it.toScanResult() }
        }
    }

    override fun getScanResults(): Flow<List<ScanResult>> {
        return scanResultDao.getAllScanResults().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getThreatsCount(): Flow<Int> {
        return scanResultDao.getThreatsCount()
    }

    private fun calculateMD5(file: File): String {
        return try {
            val md = MessageDigest.getInstance("MD5")
            val bytes = file.readBytes()
            val digest = md.digest(bytes)
            digest.joinToString("") { "%02x".format(it) }
        } catch (_: Exception) {
            ""
        }
    }

    private fun ScanResult.toEntity(): ScanResultEntity {
        return ScanResultEntity(
            id = id,
            packageName = packageName,
            appName = appName,
            threatType = threatType.name,
            threatName = threatName,
            severity = severity.name,
            filePath = filePath,
            scanDate = scanDate,
            status = status.name
        )
    }

    private fun ScanResultEntity.toDomain(): ScanResult {
        return ScanResult(
            id = id,
            packageName = packageName,
            appName = appName,
            threatType = ThreatType.valueOf(threatType),
            threatName = threatName,
            severity = ThreatSeverity.valueOf(severity),
            filePath = filePath,
            scanDate = scanDate,
            status = ThreatStatus.valueOf(status)
        )
    }

    private fun QuarantineEntity.toScanResult(): ScanResult {
        return ScanResult(
            packageName = packageName,
            appName = appName,
            threatType = ThreatType.UNKNOWN,
            threatName = threatName,
            severity = ThreatSeverity.HIGH,
            filePath = originalPath,
            status = ThreatStatus.QUARANTINED
        )
    }
}