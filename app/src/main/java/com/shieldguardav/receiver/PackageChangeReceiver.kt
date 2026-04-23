package com.shieldguardav.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PackageChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val packageName = intent.data?.schemeSpecificPart
        when (intent.action) {
            Intent.ACTION_PACKAGE_ADDED -> { }
            Intent.ACTION_PACKAGE_REMOVED -> { }
            Intent.ACTION_PACKAGE_REPLACED -> { }
        }
    }
}
