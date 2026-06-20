package com.arduia.expense.feature.auth

import com.arduia.expense.data.LockoutRepository
import com.arduia.expense.data.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class PinEntryMode {
    ENTRY,
    WRONG,
    LOCKOUT,
    RECOVERY,
}

data class PinEntryUiState(
    val mode: PinEntryMode = PinEntryMode.ENTRY,
    val filledDots: Int = 0,
    val lockoutSeconds: Int = 0,
    val recoveryAnswer: String = "",
    val biometricShouldTrigger: Boolean = false,
)

class PinEntryViewModel(
    private val pinAuthRepository: PinAuthRepository,
    private val lockoutRepository: LockoutRepository,
    private val nowEpochMillis: () -> Long,
    private val scope: CoroutineScope,
    private val onUnlocked: () -> Unit,
    private val onRecoverySuccess: () -> Unit,
) {
    companion object {
        const val MAX_ATTEMPTS = 5
        const val LOCKOUT_MS = 30_000L
    }

    private val _uiState = MutableStateFlow(PinEntryUiState())
    val uiState: StateFlow<PinEntryUiState> = _uiState.asStateFlow()

    private var currentPin: String = ""
    private var lockoutJob: Job? = null

    fun onScreenLoaded() {
        scope.launch {
            refreshLockoutState()
            val locked = lockoutRepository.isLockedOut(nowEpochMillis())
            _uiState.update {
                it.copy(
                    biometricShouldTrigger = !locked && pinAuthRepository.isBiometricEnrolled(),
                )
            }
        }
    }

    fun onDigit(digit: Int) {
        val mode = _uiState.value.mode
        if (mode == PinEntryMode.LOCKOUT || mode == PinEntryMode.RECOVERY) return
        if (currentPin.length >= 6) return
        currentPin += digit.toString()
        _uiState.update { it.copy(filledDots = currentPin.length) }
        if (currentPin.length == 6) {
            scope.launch { verifyPin(currentPin) }
        }
    }

    fun onBackspace() {
        if (_uiState.value.mode == PinEntryMode.RECOVERY) return
        if (currentPin.isNotEmpty()) {
            currentPin = currentPin.dropLast(1)
            _uiState.update {
                it.copy(
                    filledDots = currentPin.length,
                    mode = if (it.mode == PinEntryMode.WRONG) PinEntryMode.ENTRY else it.mode,
                )
            }
        }
    }

    fun onForgotPin() {
        _uiState.update { it.copy(mode = PinEntryMode.RECOVERY, filledDots = 0) }
        currentPin = ""
    }

    fun onRecoveryAnswerChange(answer: String) {
        _uiState.update { it.copy(recoveryAnswer = answer) }
    }

    fun onUnlockRecovery() {
        val answer = _uiState.value.recoveryAnswer.trim()
        if (answer.isBlank()) return
        scope.launch {
            when (val result = pinAuthRepository.verifySecurityAnswer(answer)) {
                is Result.Success -> {
                    if (result.data) {
                        lockoutRepository.resetLockout()
                        onRecoverySuccess()
                    }
                }
                is Result.Error -> Unit
            }
        }
    }

    fun onBiometricUnlock() {
        if (_uiState.value.mode == PinEntryMode.LOCKOUT) return
        scope.launch {
            lockoutRepository.resetLockout()
            onUnlocked()
        }
    }

    fun onBiometricFailed() {
        if (_uiState.value.mode == PinEntryMode.LOCKOUT) return
        scope.launch { handleWrongPin() }
    }

    private suspend fun verifyPin(pin: String) {
        if (lockoutRepository.isLockedOut(nowEpochMillis())) {
            refreshLockoutState()
            return
        }
        when (val result = pinAuthRepository.verifyPin(pin)) {
            is Result.Success -> {
                if (result.data) {
                    lockoutRepository.resetLockout()
                    currentPin = ""
                    onUnlocked()
                } else {
                    handleWrongPin()
                }
            }
            is Result.Error -> handleWrongPin()
        }
    }

    private suspend fun handleWrongPin() {
        currentPin = ""
        val state = lockoutRepository.recordFailedAttempt(nowEpochMillis(), MAX_ATTEMPTS, LOCKOUT_MS)
        _uiState.update {
            it.copy(
                filledDots = 0,
                mode = if (state.isLockedOut) PinEntryMode.LOCKOUT else PinEntryMode.WRONG,
                lockoutSeconds = state.secondsRemaining,
            )
        }
        if (state.isLockedOut) startLockoutTicker()
    }

    private suspend fun refreshLockoutState() {
        val locked = lockoutRepository.isLockedOut(nowEpochMillis())
        if (locked) {
            val until = lockoutRepository.getLockoutUntilEpochMillis() ?: 0L
            val seconds = ((until - nowEpochMillis()) / 1000).toInt().coerceAtLeast(1)
            _uiState.update {
                it.copy(mode = PinEntryMode.LOCKOUT, lockoutSeconds = seconds, filledDots = 0)
            }
            startLockoutTicker()
        }
    }

    private fun startLockoutTicker() {
        lockoutJob?.cancel()
        lockoutJob = scope.launch {
            while (_uiState.value.lockoutSeconds > 0) {
                delay(1000)
                _uiState.update { it.copy(lockoutSeconds = (it.lockoutSeconds - 1).coerceAtLeast(0)) }
            }
            lockoutRepository.resetLockout()
            _uiState.update { it.copy(mode = PinEntryMode.ENTRY, lockoutSeconds = 0, filledDots = 0) }
        }
    }
}
