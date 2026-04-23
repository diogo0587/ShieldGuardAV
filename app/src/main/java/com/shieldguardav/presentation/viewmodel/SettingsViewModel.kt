package com.shieldguardav.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shieldguardav.domain.repository.SettingsRepository
import com.shieldguardav.domain.usecase.UpdateSignaturesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val autoProtection: Boolean = true,
    val notifications: Boolean = true,
    val darkMode: Boolean = false,
    val scanFrequency: Int = 24,
    val lastScanTime: Long = 0,
    val isUpdatingSignatures: Boolean = false,
    val signatureUpdateSuccess: Boolean? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val updateSignaturesUseCase: UpdateSignaturesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        _uiState.update {
            it.copy(
                autoProtection = settingsRepository.isAutoProtectionEnabled(),
                notifications = settingsRepository.isNotificationsEnabled(),
                darkMode = settingsRepository.isDarkModeEnabled(),
                scanFrequency = settingsRepository.getScanFrequency(),
                lastScanTime = settingsRepository.getLastScanTime()
            )
        }
    }

    fun setAutoProtection(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAutoProtection(enabled)
            _uiState.update { it.copy(autoProtection = enabled) }
        }
    }

    fun setNotifications(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setNotifications(enabled)
            _uiState.update { it.copy(notifications = enabled) }
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDarkMode(enabled)
            _uiState.update { it.copy(darkMode = enabled) }
        }
    }

    fun setScanFrequency(frequency: Int) {
        viewModelScope.launch {
            settingsRepository.setScanFrequency(frequency)
            _uiState.update { it.copy(scanFrequency = frequency) }
        }
    }

    fun updateSignatures() {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingSignatures = true) }
            try {
                val success = updateSignaturesUseCase()
                _uiState.update {
                    it.copy(
                        isUpdatingSignatures = false,
                        signatureUpdateSuccess = success
                    )
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        isUpdatingSignatures = false,
                        signatureUpdateSuccess = false
                    )
                }
            }
        }
    }
}