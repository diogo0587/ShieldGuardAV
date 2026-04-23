package com.shieldguardav.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shieldguardav.domain.model.AppInfo
import com.shieldguardav.domain.model.RiskLevel
import com.shieldguardav.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppsUiState(
    val apps: List<AppInfo> = emptyList(),
    val filteredApps: List<AppInfo> = emptyList(),
    val selectedApp: AppInfo? = null,
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val filterRisk: RiskLevel? = null,
    val showSystemApps: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AppsViewModel @Inject constructor(
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
    private val getAppInfoUseCase: GetAppInfoUseCase,
    private val analyzeAppRisksUseCase: AnalyzeAppRisksUseCase,
    private val uninstallAppUseCase: UninstallAppUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppsUiState())
    val uiState: StateFlow<AppsUiState> = _uiState.asStateFlow()

    init {
        loadApps()
    }

    fun loadApps() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val apps = getInstalledAppsUseCase()
                val analyzedApps = apps.map { app ->
                    analyzeAppRisksUseCase(app)
                }
                _uiState.update { 
                    it.copy(
                        apps = analyzedApps,
                        filteredApps = filterApps(analyzedApps, it.searchQuery, it.filterRisk, it.showSystemApps),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun selectApp(packageName: String) {
        viewModelScope.launch {
            val app = getAppInfoUseCase(packageName)
            if (app != null) {
                val analyzed = analyzeAppRisksUseCase(app)
                _uiState.update { it.copy(selectedApp = analyzed) }
            }
        }
    }

    fun clearSelectedApp() {
        _uiState.update { it.copy(selectedApp = null) }
    }

    fun search(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredApps = filterApps(state.apps, query, state.filterRisk, state.showSystemApps)
            )
        }
    }

    fun filterByRisk(risk: RiskLevel?) {
        _uiState.update { state ->
            state.copy(
                filterRisk = risk,
                filteredApps = filterApps(state.apps, state.searchQuery, risk, state.showSystemApps)
            )
        }
    }

    fun toggleSystemApps() {
        _uiState.update { state ->
            val newShow = !state.showSystemApps
            state.copy(
                showSystemApps = newShow,
                filteredApps = filterApps(state.apps, state.searchQuery, state.filterRisk, newShow)
            )
        }
    }

    fun uninstallApp(packageName: String) {
        viewModelScope.launch {
            try {
                uninstallAppUseCase(packageName)
                loadApps()
            } catch (_: Exception) { }
        }
    }

    private fun filterApps(
        apps: List<AppInfo>,
        query: String,
        risk: RiskLevel?,
        showSystem: Boolean
    ): List<AppInfo> {
        return apps.filter { app ->
            val matchesQuery = query.isEmpty() ||
                app.appName.contains(query, ignoreCase = true) ||
                app.packageName.contains(query, ignoreCase = true)
            val matchesRisk = risk == null || app.riskLevel == risk
            val matchesSystem = showSystem || !app.isSystemApp
            matchesQuery && matchesRisk && matchesSystem
        }
    }
}