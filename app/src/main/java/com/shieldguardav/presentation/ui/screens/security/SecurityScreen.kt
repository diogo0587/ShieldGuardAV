package com.shieldguardav.presentation.ui.screens.security

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
import com.shieldguardav.domain.model.JunkFile
import com.shieldguardav.presentation.viewmodel.SecurityViewModel

@Composable
fun SecurityScreen(viewModel: SecurityViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Security Center", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Security Score", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    uiState.securityScore?.let { score ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${score.overallScore}", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Antivirus: ${score.antivirusScore}%", fontSize = 14.sp)
                                Text("Privacy: ${score.privacyScore}%", fontSize = 14.sp)
                                Text("Network: ${score.networkScore}%", fontSize = 14.sp)
                            }
                        }
                    }
                    Button(
                        onClick = { viewModel.loadSecurityData() },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) { Text("Refresh Score") }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("WiFi Security", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (uiState.wifiSecurity.isEmpty()) {
                        Text("No WiFi connected", style = MaterialTheme.typography.bodyMedium)
                    } else {
                        uiState.wifiSecurity.forEach { wifi ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    if (wifi.isSecure) Icons.Default.Lock else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (wifi.isSecure) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(wifi.ssid, fontWeight = FontWeight.SemiBold)
                                    Text("Encryption: ${wifi.encryption}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Junk Cleaner", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.weight(1f))
                        if (uiState.totalJunkSize > 0) {
                            Text("${uiState.totalJunkSize / 1024 / 1024} MB", color = MaterialTheme.colorScheme.error)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (uiState.junkFiles.isEmpty()) {
                        Button(
                            onClick = { viewModel.scanJunkFiles() },
                            enabled = !uiState.isScanningJunk
                        ) {
                            if (uiState.isScanningJunk) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text("Scan Junk Files")
                        }
                    } else {
                        Text("Found ${uiState.junkFiles.size} junk files", fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        uiState.junkFiles.take(5).forEach { file ->
                            Text("• ${file.path.substringAfterLast("/")} (${file.size / 1024} KB)",
                                style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { viewModel.cleanAllJunk() },
                                enabled = !uiState.isCleaning
                            ) {
                                if (uiState.isCleaning) CircularProgressIndicator(modifier = Modifier.size(16.dp))
                                else Text("Clean All")
                            }
                        }
                    }
                }
            }
        }
    }
}