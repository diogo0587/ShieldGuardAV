package com.shieldguardav.data.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.shieldguardav.domain.model.*
import com.shieldguardav.domain.repository.AppRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AppRepository {

    private val packageManager: PackageManager = context.packageManager

    override suspend fun getInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val packages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getInstalledPackages(
                PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong())
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS)
        }

        packages.mapNotNull { packageInfo ->
            try {
                mapToAppInfo(packageInfo)
            } catch (e: Exception) {
                null
            }
        }.sortedBy { it.appName.lowercase() }
    }

    override suspend fun getAppInfo(packageName: String): AppInfo? = withContext(Dispatchers.IO) {
        try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(
                    packageName,
                    PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong())
                )
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            }
            mapToAppInfo(packageInfo)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun analyzeAppRisks(appInfo: AppInfo): AppInfo = withContext(Dispatchers.Default) {
        val dangerousPermissions = getDangerousPermissions()
        val appPermissions = appInfo.permissions.map { perm ->
            val isDangerous = dangerousPermissions.any { it.equals(perm, ignoreCase = true) }
            PermissionRisk(
                permission = perm,
                isDangerous = isDangerous,
                description = getPermissionDescription(perm)
            )
        }

        val riskyPermissions = appPermissions.filter { it.isDangerous }
        val riskLevel = when {
            riskyPermissions.any { it.permission in CRITICAL_PERMISSIONS } -> RiskLevel.CRITICAL
            riskyPermissions.size >= 5 -> RiskLevel.HIGH
            riskyPermissions.size >= 3 -> RiskLevel.MEDIUM
            riskyPermissions.size >= 1 -> RiskLevel.LOW
            else -> RiskLevel.SAFE
        }

        appInfo.copy(
            riskLevel = riskLevel,
            permissionRisks = appPermissions
        )
    }

    override suspend fun uninstallApp(packageName: String) = withContext(Dispatchers.IO) {
        val intent = android.content.Intent(android.content.Intent.ACTION_DELETE)
        intent.data = android.net.Uri.parse("package:$packageName")
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    override fun getAppsFlow(): Flow<List<AppInfo>> {
        return MutableStateFlow(emptyList())
    }

    private fun mapToAppInfo(packageInfo: PackageInfo): AppInfo {
        val appName = packageInfo.applicationInfo?.let {
            packageManager.getApplicationLabel(it).toString()
        } ?: packageInfo.packageName

        val permissions = packageInfo.requestedPermissions?.toList() ?: emptyList()
        val isSystemApp = packageInfo.applicationInfo?.let {
            it.flags and ApplicationInfo.FLAG_SYSTEM != 0
        } ?: false

        val apkPath = packageInfo.applicationInfo?.sourceDir ?: ""
        val dataDir = packageInfo.applicationInfo?.dataDir ?: ""

        return AppInfo(
            packageName = packageInfo.packageName,
            appName = appName,
            versionName = packageInfo.versionName ?: "Unknown",
            versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            },
            permissions = permissions,
            isSystemApp = isSystemApp,
            installTime = packageInfo.firstInstallTime,
            updateTime = packageInfo.lastUpdateTime,
            dataDir = dataDir,
            apkPath = apkPath,
            targetSdk = packageInfo.applicationInfo?.targetSdkVersion ?: 0
        )
    }

    private fun getDangerousPermissions(): List<String> = listOf(
        "android.permission.READ_SMS",
        "android.permission.SEND_SMS",
        "android.permission.RECEIVE_SMS",
        "android.permission.READ_CONTACTS",
        "android.permission.WRITE_CONTACTS",
        "android.permission.READ_CALL_LOG",
        "android.permission.WRITE_CALL_LOG",
        "android.permission.CAMERA",
        "android.permission.RECORD_AUDIO",
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.ACCESS_COARSE_LOCATION",
        "android.permission.READ_PHONE_STATE",
        "android.permission.CALL_PHONE",
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.BODY_SENSORS",
        "android.permission.ACCESS_BACKGROUND_LOCATION",
        "android.permission.ACTIVITY_RECOGNITION"
    )

    private fun getPermissionDescription(permission: String): String = when (permission) {
        "android.permission.READ_SMS" -> "Can read your text messages"
        "android.permission.SEND_SMS" -> "Can send text messages"
        "android.permission.READ_CONTACTS" -> "Can read your contacts"
        "android.permission.CAMERA" -> "Can take photos and videos"
        "android.permission.RECORD_AUDIO" -> "Can record audio"
        "android.permission.ACCESS_FINE_LOCATION" -> "Can access your precise location"
        "android.permission.READ_PHONE_STATE" -> "Can access phone state and identity"
        "android.permission.CALL_PHONE" -> "Can make phone calls"
        else -> "Special permission"
    }

    companion object {
        private val CRITICAL_PERMISSIONS = listOf(
            "android.permission.READ_SMS",
            "android.permission.READ_CALL_LOG",
            "android.permission.READ_PHONE_STATE"
        )
    }
}