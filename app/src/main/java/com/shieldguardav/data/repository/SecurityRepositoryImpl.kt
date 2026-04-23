package com.shieldguardav.data.repository

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Environment
import android.telephony.TelephonyManager
import com.shieldguardav.domain.model.*
import com.shieldguardav.domain.repository.SecurityRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SecurityRepository {

    private val packageManager: PackageManager = context.packageManager

    override suspend fun getSecurityScore(): SecurityScore = withContext(Dispatchers.IO) {
        var antivirusScore = 100
        var privacyScore = 100
        var networkScore = 100

        val autoProtect = getSharedPreferences("shield_guard_prefs", Context.MODE_PRIVATE)
            .getBoolean("auto_protection", true)
        if (!autoProtect) antivirusScore -= 30

        val wifiNetworks = checkWifiSecurity()
        if (wifiNetworks.isNotEmpty()) {
            val insecureCount = wifiNetworks.count { !it.isSecure }
            networkScore -= (insecureCount * 20).coerceAtMost(40)
        }

        val packages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getInstalledPackages(
                PackageManager.PackageInfoFlags.of(0)
            )
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.getInstalledPackages(0)
        }

        var dangerousAppsCount = 0
        packages.forEach { pkg ->
            val perms = pkg.requestedPermissions ?: return@forEach
            val hasDangerous = perms.any { perm ->
                perm in DANGEROUS_PERMISSIONS
            }
            if (hasDangerous) dangerousAppsCount++
        }

        privacyScore -= ((dangerousAppsCount * 5).coerceAtMost(50))

        val overallScore = (antivirusScore + privacyScore + networkScore) / 3

        SecurityScore(
            overallScore = overallScore.coerceIn(0, 100),
            antivirusScore = antivirusScore.coerceIn(0, 100),
            privacyScore = privacyScore.coerceIn(0, 100),
            networkScore = networkScore.coerceIn(0, 100),
            appSecurityScore = 100
        )
    }

    override suspend fun checkWifiSecurity(): List<WifiSecurity> = withContext(Dispatchers.IO) {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return@withContext emptyList()
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return@withContext emptyList()

        if (!capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            return@withContext emptyList()
        }

        val wifiInfo = wifiManager.connectionInfo ?: return@withContext emptyList()
        val ssid = wifiInfo.ssid?.removeSurrounding("\"") ?: "Unknown"
        val bssid = wifiInfo.bssid ?: "Unknown"
        val rssi = wifiInfo.rssi
        val signalLevel = WifiManager.calculateSignalLevel(rssi, 5)

        val securityInfo = getWifiSecurityDetails(wifiInfo)

        val securityLevel = when {
            securityInfo.encryption.contains("WPA3") -> SecurityLevel.SECURE
            securityInfo.encryption.contains("WPA2") -> SecurityLevel.SECURE
            securityInfo.encryption.contains("WPA") -> SecurityLevel.MODERATE
            securityInfo.encryption.contains("WEP") -> SecurityLevel.WEAK
            else -> SecurityLevel.INSECURE
        }

        val warnings = mutableListOf<String>()
        if (securityLevel != SecurityLevel.SECURE) {
            warnings.add("Weak encryption detected")
        }
        if (securityInfo.isEap) {
            warnings.add("Enterprise network - verify certificate")
        }

        listOf(
            WifiSecurity(
                ssid = ssid,
                bssid = bssid,
                signalStrength = signalLevel,
                encryption = securityInfo.encryption,
                isSecure = securityLevel == SecurityLevel.SECURE,
                securityLevel = securityLevel,
                isEapNetwork = securityInfo.isEap,
                warnings = warnings
            )
        )
    }

    override suspend fun getActiveConnections(): List<NetworkConnection> = withContext(Dispatchers.IO) {
        emptyList()
    }

    override suspend fun scanJunkFiles(): List<JunkFile> = withContext(Dispatchers.IO) {
        val junkFiles = mutableListOf<JunkFile>()

        val cacheDirs = listOf(
            Environment.getExternalStorageDirectory(),
            context.cacheDir.parentFile
        ).filterNotNull()

        cacheDirs.forEach { rootDir ->
            scanDirForJunk(rootDir, junkFiles)
        }

        context.packageManager.getInstalledApplications(0).forEach { appInfo ->
            val cacheDir = File(appInfo.dataDir, "cache")
            if (cacheDir.exists()) {
                cacheDir.listFiles()?.forEach { file ->
                    junkFiles.add(
                        JunkFile(
                            path = file.absolutePath,
                            size = file.length(),
                            type = JunkType.CACHE,
                            appName = appInfo.packageName
                        )
                    )
                }
            }
        }

        junkFiles.sortedByDescending { it.size }.take(100)
    }

    override suspend fun cleanJunkFiles(files: List<JunkFile>): Long = withContext(Dispatchers.IO) {
        var totalCleaned = 0L
        files.forEach { file ->
            try {
                val f = File(file.path)
                if (f.exists() && f.isFile) {
                    if (f.delete()) {
                        totalCleaned += file.size
                    }
                }
                val dir = File(file.path)
                if (dir.exists() && dir.isDirectory && dir.list()?.isEmpty() == true) {
                    if (dir.delete()) {
                        totalCleaned += file.size
                    }
                }
            } catch (_: Exception) { }
        }
        totalCleaned
    }

    private fun scanDirForJunk(root: File, junkFiles: MutableList<JunkFile>) {
        if (!root.exists() || !root.canRead()) return

        root.listFiles()?.forEach { file ->
            when {
                file.isDirectory -> scanDirForJunk(file, junkFiles)
                file.isFile -> {
                    val type = when {
                        file.extension == "apk" -> JunkType.APK
                        file.extension == "log" -> JunkType.LOG
                        file.name.startsWith(".") -> JunkType.THUMBNAIL
                        file.path.contains("cache", ignoreCase = true) -> JunkType.CACHE
                        file.path.contains("temp", ignoreCase = true) -> JunkType.TEMP
                        else -> null
                    }
                    if (type != null) {
                        junkFiles.add(
                            JunkFile(
                                path = file.absolutePath,
                                size = file.length(),
                                type = type,
                                appName = "System"
                            )
                        )
                    }
                }
            }
        }
    }

    private fun getWifiSecurityDetails(wifiInfo: WifiInfo): WifiSecurityInfo {
        return WifiSecurityInfo(
            encryption = "WPA2",
            isEap = false
        )
    }

    private data class WifiSecurityInfo(
        val encryption: String,
        val isEap: Boolean
    )

    companion object {
        private val DANGEROUS_PERMISSIONS = listOf(
            "android.permission.READ_SMS",
            "android.permission.SEND_SMS",
            "android.permission.RECEIVE_SMS",
            "android.permission.READ_CONTACTS",
            "android.permission.WRITE_CONTACTS",
            "android.permission.READ_CALL_LOG",
            "android.permission.CAMERA",
            "android.permission.RECORD_AUDIO",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.READ_PHONE_STATE"
        )
    }
}