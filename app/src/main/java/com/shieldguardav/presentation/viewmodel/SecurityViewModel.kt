package com.shieldguardav.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shieldguardav.domain.model.*
import com.shieldguardav.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SecurityUiState(
    val securityScore: SecurityScore? = null,
    val wifiSecurity: List<WifiSecurity> = emptyList(),
    val junkFiles: List<JunkFile> = emptyList(),
    val totalJunkSize: Long = 0,
    val isLoading: Boolean = false,
    val isScanningJunk: Boolean = false,
    val isCleaning: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val getSecurityScoreUseCase: GetSecurityScoreUseCase,
    private val checkWifiSecurityUseCase: CheckWifiSecurityUseCase,
    private val scanJunkFilesUseCase: ScanJunkFilesUseCase,
    private val cleanJunkFilesUseCase: CleanJunkFilesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SecurityUiState())
    val uiState: StateFlow<SecurityUiState> = _uiState.asStateFlow()

    init {
        loadSecurityData()
    }

    fun loadSecurityData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val score = getSecurityScoreUseCase()
                val wifi = checkWifiSecurityUseCase()
                _uiState.update {
                    it.copy(
                        securityScore = score,
                        wifiSecurity = wifi,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun scanJunkFiles() {
        viewModelScope.launch {
            _uiState.update { it.copy(isScanningJunk = true) }
            try {
                val junkFiles = scanJunkFilesUseCase()
                val totalSize = junkFiles.sumOf { it.size }
                _uiState.update {
                    it.copy(
                        junkFiles = junkFiles,
                        totalJunkSize = totalSize,
                        isScanningJunk = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isScanningJunk = false, errorMessage = e.message) }
            }
        }
    }

    fun cleanAllJunk() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCleaning = true) }
            try {
                val cleaned = cleanJunkFilesUseCase(_uiState.value.junkFiles)
                _uiState.update {
                    it.copy(
                        junkFiles = emptyList(),
                        totalJunkSize = 0,
                        isCleaning = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isCleaning = false, errorMessage = e.message) }
            }
        }
    }

    fun cleanSelectedJunk(files: List<JunkFile>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCleaning = true) }
            try {
                cleanJunkFilesUseCase(files)
                val remaining = _uiState.value.junkFiles.filter { it !in files }
                val remainingSize = remaining.sumOf { it.size }
                _uiState.update {
                    it.copy(
                        junkFiles = remaining,
                        totalJunkSize = remainingSize,
                        isCleaning = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isCleaning = false, errorMessage = e.message) }
            }
        }
    }
}