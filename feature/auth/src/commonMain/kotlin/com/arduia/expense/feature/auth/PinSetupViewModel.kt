package com.arduia.expense.feature.auth

import com.arduia.expense.data.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class PinSetupStep {
    ENTER,
    CONFIRM,
    SECURITY_QUESTION,
    BIOMETRIC_OFFER,
    COMPLETE,
}

data class PinSetupUiState(
    val step: PinSetupStep = PinSetupStep.ENTER,
    val filledDots: Int = 0,
    val mismatchError: Boolean = false,
    val securityQuestions: List<SecurityQuestion> = SecurityQuestions.all,
    val selectedQuestionId: String = SecurityQuestions.all.first().id,
    val securityAnswer: String = "",
    val biometricAvailable: Boolean = false,
    val errorMessage: String? = null,
)

class PinSetupViewModel(
    private val pinAuthRepository: PinAuthRepository,
    private val biometricAvailability: BiometricAvailability,
    private val scope: CoroutineScope,
    private val onComplete: () -> Unit,
    private val isChangePinMode: Boolean = false,
) {
    private val _uiState = MutableStateFlow(
        PinSetupUiState(biometricAvailable = biometricAvailability.isAvailable()),
    )
    val uiState: StateFlow<PinSetupUiState> = _uiState.asStateFlow()

    private var createdPin: String = ""
    private var currentEntry: String = ""

    fun onDigit(digit: Int) {
        val state = _uiState.value
        if (state.step == PinSetupStep.SECURITY_QUESTION ||
            state.step == PinSetupStep.BIOMETRIC_OFFER ||
            state.step == PinSetupStep.COMPLETE
        ) {
            return
        }
        if (currentEntry.length >= 6) return
        currentEntry += digit.toString()
        _uiState.update { it.copy(filledDots = currentEntry.length, mismatchError = false) }
        if (currentEntry.length == 6) {
            when (state.step) {
                PinSetupStep.ENTER -> {
                    createdPin = currentEntry
                    currentEntry = ""
                    _uiState.update { it.copy(step = PinSetupStep.CONFIRM, filledDots = 0) }
                }
                PinSetupStep.CONFIRM -> {
                    if (currentEntry == createdPin) {
                        if (isChangePinMode) {
                            scope.launch {
                                when (pinAuthRepository.changePin(createdPin)) {
                                    is Result.Success -> completeSetup()
                                    is Result.Error -> _uiState.update {
                                        it.copy(errorMessage = "Could not save PIN", filledDots = 0)
                                    }
                                }
                            }
                            currentEntry = ""
                            _uiState.update { it.copy(filledDots = 0, mismatchError = false) }
                        } else {
                            currentEntry = ""
                            _uiState.update {
                                it.copy(step = PinSetupStep.SECURITY_QUESTION, filledDots = 0, mismatchError = false)
                            }
                        }
                    } else {
                        currentEntry = ""
                        _uiState.update { it.copy(filledDots = 0, mismatchError = true) }
                    }
                }
            }
        }
    }

    fun onBackspace() {
        if (_uiState.value.step == PinSetupStep.SECURITY_QUESTION) return
        if (currentEntry.isNotEmpty()) {
            currentEntry = currentEntry.dropLast(1)
            _uiState.update { it.copy(filledDots = currentEntry.length, mismatchError = false) }
        }
    }

    fun onSecurityQuestionSelected(questionId: String) {
        _uiState.update { it.copy(selectedQuestionId = questionId) }
    }

    fun onSecurityAnswerChange(answer: String) {
        _uiState.update { it.copy(securityAnswer = answer) }
    }

    fun onContinueSecurity() {
        val state = _uiState.value
        val answer = state.securityAnswer.trim()
        if (answer.isBlank()) return
        scope.launch {
            when (pinAuthRepository.setSecurityAnswer(state.selectedQuestionId, answer)) {
                is Result.Success -> {
                    when (pinAuthRepository.setPin(createdPin)) {
                        is Result.Success -> {
                            if (_uiState.value.biometricAvailable) {
                                _uiState.update { it.copy(step = PinSetupStep.BIOMETRIC_OFFER) }
                            } else {
                                completeSetup()
                            }
                        }
                        is Result.Error -> _uiState.update { it.copy(errorMessage = "Could not save PIN") }
                    }
                }
                is Result.Error -> _uiState.update { it.copy(errorMessage = "Could not save security answer") }
            }
        }
    }

    fun onBiometricOffered(accepted: Boolean) {
        scope.launch {
            if (accepted) {
                pinAuthRepository.setBiometricEnrolled(true)
            }
            completeSetup()
        }
    }

    private fun completeSetup() {
        _uiState.update { it.copy(step = PinSetupStep.COMPLETE) }
        onComplete()
    }
}
