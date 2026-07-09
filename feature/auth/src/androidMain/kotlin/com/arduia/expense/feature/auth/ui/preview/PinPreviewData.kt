package com.arduia.expense.feature.auth.ui.preview

import com.arduia.expense.feature.auth.PinEntryMode
import com.arduia.expense.feature.auth.PinEntryUiState
import com.arduia.expense.feature.auth.R

data class PinSetupUiState(
    val pinAuthOn: Boolean = true,
    val biometricOn: Boolean = false,
    val biometricCapable: Boolean = true,
    val newPin: String = "483920",
    val confirmPin: String = "483",
)

data class PinSecurityQuestionUi(
    val id: String,
    val textRes: Int,
)

val pinSecurityQuestions =
    listOf(
        PinSecurityQuestionUi("pet", R.string.security_question_pet),
        PinSecurityQuestionUi("city", R.string.security_question_city),
        PinSecurityQuestionUi("school", R.string.security_question_school),
        PinSecurityQuestionUi("maiden", R.string.security_question_maiden),
        PinSecurityQuestionUi("nickname", R.string.security_question_nickname),
    )

val previewPinEntry = PinEntryUiState(filledDots = 4, mode = PinEntryMode.Default)

val previewPinWrong =
    PinEntryUiState(
        filledDots = 0,
        mode = PinEntryMode.Error,
        showBiometric = false,
    )

val previewPinLock =
    PinEntryUiState(
        filledDots = 4,
        mode = PinEntryMode.Locked,
        countdownLabel = "0:15",
        showBiometric = false,
    )

val previewPinSetup = PinSetupUiState()

val previewPinSetConfirmMismatch =
    PinEntryUiState(
        filledDots = 0,
        mode = PinEntryMode.Error,
        showBiometric = false,
    )

val previewPinSetRevealed =
    PinEntryUiState(
        filledDots = 4,
        mode = PinEntryMode.Default,
        showBiometric = false,
        digits = "1234",
    )
