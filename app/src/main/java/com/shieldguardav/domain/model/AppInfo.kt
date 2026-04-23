package com.shieldguardav.domain.model

data class AppInfo(
    val packageName: String,
    val appName: String,
    val versionName: String,
    val versionCode: Long,
    val permissions: List<String>,
    val isSystemApp: Boolean,
    val installTime: Long,
    val updateTime: Long,
    val dataDir: String,
    val apkPath: String,
    val targetSdk: Int,
    val riskLevel: RiskLevel = RiskLevel.UNKNOWN,
    val permissionRisks: List<PermissionRisk> = emptyList()
)

enum class RiskLevel {
    SAFE,
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL,
    UNKNOWN
}

data class PermissionRisk(
    val permission: String,
    val isDangerous: Boolean,
    val description: String
)