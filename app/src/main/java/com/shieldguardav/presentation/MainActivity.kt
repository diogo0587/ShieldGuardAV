package com.shieldguardav.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shieldguardav.presentation.ui.navigation.Screen
import com.shieldguardav.presentation.ui.navigation.bottomNavItems
import com.shieldguardav.presentation.ui.screens.apps.AppsScreen
import com.shieldguardav.presentation.ui.screens.home.HomeScreen
import com.shieldguardav.presentation.ui.screens.security.SecurityScreen
import com.shieldguardav.presentation.ui.screens.settings.SettingsScreen
import com.shieldguardav.presentation.ui.theme.ShieldGuardTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShieldGuardTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val showBottomBar = currentRoute in listOf(
                    Screen.Home.route,
                    Screen.Apps.route,
                    Screen.Security.route,
                    Screen.Settings.route
                )

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar {
                                bottomNavItems.forEach { screen ->
                                    NavigationBarItem(
                                        selected = currentRoute == screen.route,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.startDestinationId)
                                                launchSingleTop = true
                                            }
                                        },
                                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                                        label = { Text(screen.title) }
                                    )
                                }
                            }
                        }
                    }
                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.fillMaxSize().padding(paddingValues)
                    ) {
                        composable(Screen.Home.route) { HomeScreen(hiltViewModel()) }
                        composable(Screen.Apps.route) { AppsScreen(hiltViewModel()) }
                        composable(Screen.Security.route) { SecurityScreen(hiltViewModel()) }
                        composable(Screen.Settings.route) { SettingsScreen(hiltViewModel()) }
                    }
                }
            }
        }
    }
}