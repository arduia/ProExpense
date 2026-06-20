package com.arduia.expense.feature.auth

import com.arduia.expense.data.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SecuritySettingsUiState(
    val isPinEnabled: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val biometricAvailable: Boolean = false,
    val message: String? = null,
)

class SecuritySettingsViewModel(
    private val pinAuthRepository: PinAuthRepository,
    private val biometricAvailability: BiometricAvailability,
    private val scope: CoroutineScope,
    private val onChangePinRequested: () -> Unit,
) {
    private val _uiState = MutableStateFlow(
        SecuritySettingsUiState(biometricAvailable = biometricAvailability.isAvailable()),
    )
    val uiState: StateFlow<SecuritySettingsUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        scope.launch {
            val pinConfigured = when (val result = pinAuthRepository.isPinConfigured()) {
                is Result.Success -> result.data
                is Result.Error -> false
            }
            _uiState.update {
                it.copy(
                    isPinEnabled = pinConfigured,
                    isBiometricEnabled = pinAuthRepository.isBiometricEnrolled(),
                    biometricAvailable = biometricAvailability.isAvailable(),
                )
            }
        }
    }

    fun onChangePinRequested() {
        onChangePinRequested()
    }

    fun onDisablePin() {
        scope.launch {
            when (pinAuthRepository.clearPin()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(isPinEnabled = false, isBiometricEnabled = false, message = null)
                    }
                }
                is Result.Error -> _uiState.update { it.copy(message = "Could not disable PIN") }
            }
        }
    }

    fun onBiometricToggled(enabled: Boolean) {
        scope.launch {
            when (pinAuthRepository.setBiometricEnrolled(enabled)) {
                is Result.Success -> _uiState.update { it.copy(isBiometricEnabled = enabled, message = null) }
                is Result.Error -> _uiState.update { it.copy(message = "Could not update biometric setting") }
            }
        }
    }
}
