package com.shieldguardav.presentation.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Apps : Screen("apps", "Apps", Icons.Default.Apps)
    object Security : Screen("security", "Security", Icons.Default.Security)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object ScanDetails : Screen("scan_details", "Scan Details", Icons.Default.Details)
    object AppDetails : Screen("app_details/{packageName}", "App Details", Icons.Default.Info) {
        fun createRoute(packageName: String) = "app_details/$packageName"
    }
    object Quarantine : Screen("quarantine", "Quarantine", Icons.Default.Lock)
    object WifiSecurity : Screen("wifi_security", "WiFi Security", Icons.Default.Wifi)
    object JunkCleaner : Screen("junk_cleaner", "Junk Cleaner", Icons.Default.CleaningServices)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Apps,
    Screen.Security,
    Screen.Settings
)