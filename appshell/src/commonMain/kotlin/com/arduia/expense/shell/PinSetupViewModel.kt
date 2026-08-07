package com.arduia.expense.shell

import com.arduia.expense.data.Result
import com.arduia.expense.feature.auth.DisablePinUseCase
import com.arduia.expense.feature.auth.PinEntryLogic
import com.arduia.expense.feature.auth.SecurityQuestionCatalog
import com.arduia.expense.feature.auth.SetupPinUseCase
import com.arduia.expense.shared.StatefulViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/** Which field the keypad is currently filling on 14 · PIN Setup. */
enum class PinSetupStage {
    Enter,
    Confirm,
    SecurityQuestion,
}

data class PinSetupScreenState(
    val stage: PinSetupStage = PinSetupStage.Enter,
    val newPin: String = "",
    val confirmPin: String = "",
    val questionId: String = SecurityQuestionCatalog.PET,
    val answer: String = "",
    val enableBiometric: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val completed: Boolean = false,
) {
    val pinLength: Int get() = PinEntryLogic.PIN_LENGTH

    val questionIds: List<String> get() = SecurityQuestionCatalog.IDS

    val mismatch: Boolean
        get() = confirmPin.length == pinLength && confirmPin != newPin

    val canSave: Boolean
        get() = newPin.length == pinLength && confirmPin == newPin && answer.isNotBlank() && !isSaving

    /** Dots for whichever field the keypad is filling. */
    val activeBuffer: String
        get() = if (stage == PinSetupStage.Confirm) confirmPin else newPin
}

/**
 * 14 · PIN Setup — create or disable the local PIN.
 *
 * Buffer rules reuse [PinEntryLogic] so setup and unlock behave identically; persistence goes
 * through [SetupPinUseCase], which already sequences PIN → security question → biometric and
 * short-circuits on the first failure.
 */
class PinSetupViewModel(
    private val setupPin: SetupPinUseCase,
    private val disablePin: DisablePinUseCase,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : StatefulViewModel<PinSetupScreenState>(PinSetupScreenState(), dispatcher) {
    fun onDigit(digit: Int) {
        val state = currentState()
        when (state.stage) {
            PinSetupStage.Enter ->
                when (val result = PinEntryLogic.appendDigit(state.newPin, digit)) {
                    is PinEntryLogic.DigitResult.Updated -> setState { it.copy(newPin = result.buffer) }
                    is PinEntryLogic.DigitResult.Completed ->
                        setState { it.copy(newPin = result.pin, stage = PinSetupStage.Confirm) }
                }
            PinSetupStage.Confirm ->
                when (val result = PinEntryLogic.appendDigit(state.confirmPin, digit)) {
                    is PinEntryLogic.DigitResult.Updated -> setState { it.copy(confirmPin = result.buffer) }
                    is PinEntryLogic.DigitResult.Completed ->
                        setState {
                            it.copy(
                                confirmPin = result.pin,
                                stage =
                                    if (result.pin == it.newPin) {
                                        PinSetupStage.SecurityQuestion
                                    } else {
                                        PinSetupStage.Confirm
                                    },
                            )
                        }
                }
            PinSetupStage.SecurityQuestion -> Unit
        }
    }

    fun onBackspace() {
        setState {
            when (it.stage) {
                PinSetupStage.Enter -> it.copy(newPin = PinEntryLogic.backspace(it.newPin))
                PinSetupStage.Confirm -> it.copy(confirmPin = PinEntryLogic.backspace(it.confirmPin))
                PinSetupStage.SecurityQuestion -> it
            }
        }
    }

    /** Re-entering the confirm step clears it, so a mismatch never leaves stale digits on screen. */
    fun onRetryConfirm() {
        setState { it.copy(confirmPin = "", stage = PinSetupStage.Confirm) }
    }

    fun onQuestionSelected(questionId: String) {
        setState { it.copy(questionId = questionId) }
    }

    fun onAnswerChange(answer: String) {
        setState { it.copy(answer = answer) }
    }

    fun onBiometricToggled(enabled: Boolean) {
        setState { it.copy(enableBiometric = enabled) }
    }

    suspend fun save(): Boolean {
        if (!currentState().canSave) return false
        setState { it.copy(isSaving = true, errorMessage = null) }
        val state = currentState()
        val result =
            setupPin(
                pin = state.newPin,
                securityQuestionId = state.questionId,
                securityAnswer = state.answer.trim(),
                enableBiometric = state.enableBiometric,
            )
        val succeeded = result is Result.Success
        setState {
            it.copy(
                isSaving = false,
                completed = succeeded,
                errorMessage = (result as? Result.Error)?.message,
            )
        }
        return succeeded
    }

    suspend fun disable(): Boolean {
        val result = disablePin()
        val succeeded = result is Result.Success
        setState {
            it.copy(
                completed = succeeded,
                errorMessage = (result as? Result.Error)?.message,
            )
        }
        return succeeded
    }
}
