package com.shieldguardav

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ShieldGuardApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)

            val scannerChannel = NotificationChannel(
                SCANNER_CHANNEL_ID,
                getString(R.string.scanner_notification_channel),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.scanner_notification_desc)
            }

            val protectionChannel = NotificationChannel(
                PROTECTION_CHANNEL_ID,
                getString(R.string.protection_notification_channel),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.protection_notification_desc)
            }

            notificationManager.createNotificationChannels(
                listOf(scannerChannel, protectionChannel)
            )
        }
    }

    companion object {
        const val SCANNER_CHANNEL_ID = "scanner_channel"
        const val PROTECTION_CHANNEL_ID = "protection_channel"
    }
}