package com.arduia.expense.feature.auth.ui.preview

import com.arduia.expense.feature.auth.PinEntryMode
import com.arduia.expense.feature.auth.PinEntryUiState
import com.arduia.expense.feature.auth.R
import com.arduia.expense.feature.auth.SecurityQuestionCatalog

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

/** Ids and order come from the shared catalog; only the localized text is Android's. */
val pinSecurityQuestions =
    listOf(
        PinSecurityQuestionUi(SecurityQuestionCatalog.PET, R.string.security_question_pet),
        PinSecurityQuestionUi(SecurityQuestionCatalog.CITY, R.string.security_question_city),
        PinSecurityQuestionUi(SecurityQuestionCatalog.SCHOOL, R.string.security_question_school),
        PinSecurityQuestionUi(SecurityQuestionCatalog.MAIDEN, R.string.security_question_maiden),
        PinSecurityQuestionUi(SecurityQuestionCatalog.NICKNAME, R.string.security_question_nickname),
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
