package com.arduia.expense.feature.auth.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import com.arduia.expense.data.Result
import com.arduia.expense.feature.auth.BiometricAuthenticator
import com.arduia.expense.feature.auth.R
import com.arduia.expense.feature.auth.SetupPinUseCase
import com.arduia.expense.feature.auth.ui.preview.PinEntryMode
import com.arduia.expense.feature.auth.ui.preview.PinEntryUiState
import com.arduia.expense.feature.auth.ui.preview.PinSetupUiState
import com.arduia.expense.feature.auth.ui.preview.pinSecurityQuestions
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.rememberProReduceMotion
import com.arduia.expense.ui.theme.stepTransition
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

private enum class PinSetupStep { Setup, EnterNew, EnterConfirm, Security }

@Composable
fun PinSetupFlow(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val motion = ProExpenseTheme.motion
    val reduceMotion = rememberProReduceMotion()
    val setupPin: SetupPinUseCase = koinInject()
    val scope = rememberCoroutineScope()
    val activity = LocalContext.current as? FragmentActivity
    val biometricCapable = activity != null && BiometricAuthenticator.isAvailable(activity)

    var step by remember { mutableStateOf(PinSetupStep.Setup) }
    var pinAuthOn by remember { mutableStateOf(true) }
    var biometricOn by remember { mutableStateOf(false) }
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var entryBuffer by remember { mutableStateOf("") }
    var confirmError by remember { mutableStateOf(false) }
    var selectedQuestion by remember { mutableStateOf("pet") }
    var answer by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val canSave = pinAuthOn && newPin.length == 6 && confirmPin.length == 6 && answer.isNotBlank()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper),
    ) {
        AnimatedContent(
            targetState = step,
            transitionSpec = {
                motion.stepTransition(
                    fromIndex = initialState.ordinal,
                    toIndex = targetState.ordinal,
                    reduceMotion = reduceMotion,
                )
            },
            label = "pinSetupStep",
        ) { target ->
            when (target) {
                PinSetupStep.Setup -> PinSetupScreen(
                    state = PinSetupUiState(
                        pinAuthOn = pinAuthOn,
                        biometricOn = biometricOn,
                        biometricCapable = biometricCapable,
                        newPinFilled = newPin.length,
                        confirmPinFilled = confirmPin.length,
                    ),
                    onTogglePin = { pinAuthOn = it },
                    onToggleBiometric = { biometricOn = it },
                    onRevealNew = {
                        entryBuffer = ""
                        step = PinSetupStep.EnterNew
                    },
                    onRevealConfirm = {
                        entryBuffer = ""
                        confirmError = false
                        step = PinSetupStep.EnterConfirm
                    },
                    onRecoveryClick = { step = PinSetupStep.Security },
                    onSave = {
                        errorMessage = null
                        scope.launch {
                            val result = setupPin(
                                pin = newPin,
                                securityQuestionId = selectedQuestion,
                                securityAnswer = answer,
                                enableBiometric = biometricOn,
                            )
                            if (result is Result.Error) {
                                errorMessage = result.message
                                return@launch
                            }
                            onDismiss()
                        }
                    },
                    onBack = onDismiss,
                    saveEnabled = canSave,
                    errorMessage = errorMessage,
                )
                PinSetupStep.EnterNew -> PinSetPinScreen(
                    state = PinEntryUiState(filledDots = entryBuffer.length, digits = entryBuffer),
                    headingRes = R.string.pin_set_new_heading,
                    onDigit = { digit ->
                        if (entryBuffer.length < 6) {
                            entryBuffer += digit
                            if (entryBuffer.length == 6) {
                                newPin = entryBuffer
                                confirmPin = ""
                                step = PinSetupStep.Setup
                            }
                        }
                    },
                    onBackspace = {
                        if (entryBuffer.isNotEmpty()) entryBuffer = entryBuffer.dropLast(1)
                    },
                    onBack = { step = PinSetupStep.Setup },
                )
                PinSetupStep.EnterConfirm -> PinSetPinScreen(
                    state = PinEntryUiState(
                        filledDots = entryBuffer.length,
                        mode = if (confirmError) PinEntryMode.Error else PinEntryMode.Default,
                        digits = entryBuffer,
                    ),
                    headingRes = R.string.pin_confirm_heading,
                    onDigit = { digit ->
                        if (confirmError) {
                            confirmError = false
                            entryBuffer = ""
                        }
                        if (entryBuffer.length < 6) {
                            entryBuffer += digit
                            if (entryBuffer.length == 6) {
                                if (entryBuffer == newPin) {
                                    confirmPin = entryBuffer
                                    step = PinSetupStep.Setup
                                } else {
                                    confirmError = true
                                    entryBuffer = ""
                                }
                            }
                        }
                    },
                    onBackspace = {
                        if (entryBuffer.isNotEmpty()) entryBuffer = entryBuffer.dropLast(1)
                    },
                    onBack = { step = PinSetupStep.Setup },
                )
                PinSetupStep.Security -> PinSecurityQuestionScreen(
                    questions = pinSecurityQuestions,
                    selectedId = selectedQuestion,
                    answer = answer,
                    onSelect = { selectedQuestion = it },
                    onAnswerChange = { answer = it },
                    onEnable = { step = PinSetupStep.Setup },
                    onBack = { step = PinSetupStep.Setup },
                )
            }
        }
    }
}

@Preview(
    name = "PIN setup flow",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun PinSetupFlowPreview() {
    ProExpenseTheme {
        PinSetupFlow(onDismiss = {})
    }
}
