package com.shieldguardav.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shieldguardav.domain.model.*
import com.shieldguardav.domain.repository.MalwareDatabaseRepository
import com.shieldguardav.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isScanning: Boolean = false,
    val scanProgress: ScanProgress = ScanProgress("", 0, 0, 0),
    val securityScore: SecurityScore? = null,
    val threatsFound: Int = 0,
    val lastScanTime: Long = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val quickScanUseCase: QuickScanUseCase,
    private val fullScanUseCase: FullScanUseCase,
    private val getSecurityScoreUseCase: GetSecurityScoreUseCase,
    private val getThreatsCountUseCase: GetThreatsCountUseCase,
    private val loadSignaturesUseCase: LoadSignaturesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
        observeThreats()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                loadSignaturesUseCase()
                val score = getSecurityScoreUseCase()
                _uiState.update { it.copy(securityScore = score, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    private fun observeThreats() {
        viewModelScope.launch {
            getThreatsCountUseCase().collect { count ->
                _uiState.update { it.copy(threatsFound = count) }
            }
        }
    }

    fun startQuickScan() {
        if (_uiState.value.isScanning) return
        viewModelScope.launch {
            _uiState.update { it.copy(isScanning = true) }
            quickScanUseCase().collect { progress ->
                _uiState.update { it.copy(scanProgress = progress) }
                if (progress.isComplete) {
                    _uiState.update { it.copy(isScanning = false) }
                    loadData()
                }
            }
        }
    }

    fun startFullScan() {
        if (_uiState.value.isScanning) return
        viewModelScope.launch {
            _uiState.update { it.copy(isScanning = true) }
            fullScanUseCase().collect { progress ->
                _uiState.update { it.copy(scanProgress = progress) }
                if (progress.isComplete) {
                    _uiState.update { it.copy(isScanning = false) }
                    loadData()
                }
            }
        }
    }

    fun cancelScan() {
        viewModelScope.update { it.copy(isScanning = false) }
    }

    fun refreshScore() {
        viewModelScope.launch {
            val score = getSecurityScoreUseCase()
            _uiState.update { it.copy(securityScore = score) }
        }
    }
}