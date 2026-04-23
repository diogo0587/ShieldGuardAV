package com.shieldguardav.presentation.ui.screens.apps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shieldguardav.domain.model.AppInfo
import com.shieldguardav.domain.model.RiskLevel
import com.shieldguardav.presentation.viewmodel.AppsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppsScreen(viewModel: AppsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Installed Apps", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.search(it) },
            label = { Text("Search apps...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = uiState.filterRisk == null,
                onClick = { viewModel.filterByRisk(null) },
                label = { Text("All") }
            )
            FilterChip(
                selected = uiState.filterRisk == RiskLevel.SAFE,
                onClick = { viewModel.filterByRisk(RiskLevel.SAFE) },
                label = { Text("Safe") }
            )
            FilterChip(
                selected = uiState.filterRisk == RiskLevel.HIGH || uiState.filterRisk == RiskLevel.CRITICAL,
                onClick = { viewModel.filterByRisk(RiskLevel.HIGH) },
                label = { Text("Risky") }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.filteredApps) { app ->
                    AppCard(app = app, onAppClick = { viewModel.selectApp(app.packageName) })
                }
            }
        }
    }

    if (uiState.selectedApp != null) {
        AppDetailsDialog(
            app = uiState.selectedApp!!,
            onDismiss = { viewModel.clearSelectedApp() },
            onUninstall = {
                viewModel.uninstallApp(it)
                viewModel.clearSelectedApp()
            }
        )
    }
}

@Composable
fun AppCard(app: AppInfo, onAppClick: () -> Unit) {
    Card(onClick = onAppClick, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = when (app.riskLevel) {
                    RiskLevel.SAFE -> Icons.Default.CheckCircle
                    RiskLevel.LOW, RiskLevel.MEDIUM -> Icons.Default.Warning
                    else -> Icons.Default.Error
                },
                contentDescription = null,
                tint = when (app.riskLevel) {
                    RiskLevel.SAFE -> MaterialTheme.colorScheme.primary
                    RiskLevel.LOW, RiskLevel.MEDIUM -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.error
                },
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(app.appName, fontWeight = FontWeight.SemiBold)
                Text(app.packageName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Text(
                when (app.riskLevel) {
                    RiskLevel.SAFE -> "Safe"
                    RiskLevel.LOW -> "Low"
                    RiskLevel.MEDIUM -> "Med"
                    RiskLevel.HIGH -> "High"
                    RiskLevel.CRITICAL -> "CRIT"
                    else -> "?"
                },
                color = when (app.riskLevel) {
                    RiskLevel.SAFE -> MaterialTheme.colorScheme.primary
                    RiskLevel.LOW -> MaterialTheme.colorScheme.tertiary
                    RiskLevel.MEDIUM -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.error
                },
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AppDetailsDialog(app: AppInfo, onDismiss: () -> Unit, onUninstall: (String) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(app.appName) },
        text = {
            Column {
                Text("Package: ${app.packageName}")
                Text("Version: ${app.versionName}")
                Text("Risk Level: ${app.riskLevel.name}")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Permissions (${app.permissions.size}):", fontWeight = FontWeight.Bold)
                app.permissions.take(10).forEach { perm ->
                    Text("• ${perm.split(".").last()}", style = MaterialTheme.typography.bodySmall)
                }
                if (app.permissions.size > 10) {
                    Text("... and ${app.permissions.size - 10} more", style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(onClick = { onUninstall(app.packageName) }) {
                Text("Uninstall")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}