package com.shieldguardav.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.shieldguardav.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private val prefs: SharedPreferences by lazy {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            EncryptedSharedPreferences.create(
                context,
                "shield_guard_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            context.getSharedPreferences("shield_guard_prefs", Context.MODE_PRIVATE)
        }
    }

    override fun isAutoProtectionEnabled(): Boolean {
        return prefs.getBoolean(KEY_AUTO_PROTECTION, true)
    }

    override suspend fun setAutoProtection(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_PROTECTION, enabled).apply()
    }

    override fun isNotificationsEnabled(): Boolean {
        return prefs.getBoolean(KEY_NOTIFICATIONS, true)
    }

    override suspend fun setNotifications(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply()
    }

    override fun isDarkModeEnabled(): Boolean {
        return prefs.getBoolean(KEY_DARK_MODE, false)
    }

    override suspend fun setDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }

    override fun getLastScanTime(): Long {
        return prefs.getLong(KEY_LAST_SCAN, 0)
    }

    override suspend fun setLastScanTime(time: Long) {
        prefs.edit().putLong(KEY_LAST_SCAN, time).apply()
    }

    override fun getScanFrequency(): Int {
        return prefs.getInt(KEY_SCAN_FREQUENCY, 24)
    }

    override suspend fun setScanFrequency(frequency: Int) {
        prefs.edit().putInt(KEY_SCAN_FREQUENCY, frequency).apply()
    }

    companion object {
        private const val KEY_AUTO_PROTECTION = "auto_protection"
        private const val KEY_NOTIFICATIONS = "notifications"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_LAST_SCAN = "last_scan"
        private const val KEY_SCAN_FREQUENCY = "scan_frequency"
    }
}