package com.shieldguardav.domain.model

data class SecurityScore(
    val overallScore: Int,
    val antivirusScore: Int,
    val privacyScore: Int,
    val networkScore: Int,
    val appSecurityScore: Int,
    val lastUpdated: Long = System.currentTimeMillis()
)

data class WifiSecurity(
    val ssid: String,
    val bssid: String,
    val signalStrength: Int,
    val encryption: String,
    val isSecure: Boolean,
    val securityLevel: SecurityLevel,
    val isEapNetwork: Boolean = false,
    val warnings: List<String> = emptyList()
)

enum class SecurityLevel {
    SECURE,
    MODERATE,
    WEAK,
    INSECURE
}

data class NetworkConnection(
    val packageName: String,
    val appName: String,
    val localAddress: String,
    val remoteAddress: String,
    val remotePort: Int,
    val protocol: String,
    val isActive: Boolean,
    val uid: Int
)

data class JunkFile(
    val path: String,
    val size: Long,
    val type: JunkType,
    val appName: String
)

enum class JunkType {
    CACHE,
    TEMP,
    LOG,
    APK,
    THUMBNAIL,
    DOWNLOAD
}