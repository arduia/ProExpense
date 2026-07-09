package com.arduia.expense.feature.auth.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.feature.auth.PinEntryLogic
import com.arduia.expense.feature.auth.PinEntryMode
import com.arduia.expense.feature.auth.PinEntryUiState
import com.arduia.expense.feature.auth.R
import com.arduia.expense.feature.auth.ResetPinUseCase
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.rememberProReduceMotion
import com.arduia.expense.ui.theme.stepTransition
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

private enum class PinChangeStep { VerifyCurrent, EnterNew, ConfirmNew }

/**
 * Change-PIN flow (US-AUTH-7 Scenario 1): verify the current PIN, then enter and confirm a new
 * one. Mirrors [PinLockFlow]'s recovery-reset steps but is reached deliberately from Settings,
 * not after a forgotten-PIN challenge.
 */
@Composable
fun PinChangeFlow(
    onDone: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val motion = ProExpenseTheme.motion
    val reduceMotion = rememberProReduceMotion()
    val resetPin: ResetPinUseCase = koinInject()
    val scope = rememberCoroutineScope()

    var step by remember { mutableStateOf(PinChangeStep.VerifyCurrent) }
    var entryBuffer by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var confirmError by remember { mutableStateOf(false) }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(ProExpenseTheme.colors.paper)
                // Rendered as an overlay sibling above the More hub — swallow taps so they can't
                // fall through to whatever's underneath (see PinEntryScreen for the same fix).
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = {}),
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
            label = "pinChangeStep",
        ) { target ->
            when (target) {
                PinChangeStep.VerifyCurrent ->
                    PinVerifyFlow(
                        headingRes = R.string.pin_change_verify_heading,
                        helperRes = R.string.pin_change_verify_helper,
                        onVerified = {
                            entryBuffer = ""
                            step = PinChangeStep.EnterNew
                        },
                        onCancel = onCancel,
                    )
                PinChangeStep.EnterNew ->
                    PinSetPinScreen(
                        state = PinEntryUiState(filledDots = entryBuffer.length, digits = entryBuffer),
                        headingRes = R.string.pin_change_new_heading,
                        onDigit = { digit ->
                            when (val result = PinEntryLogic.appendDigit(entryBuffer, digit)) {
                                is PinEntryLogic.DigitResult.Updated -> entryBuffer = result.buffer
                                is PinEntryLogic.DigitResult.Completed -> {
                                    newPin = result.pin
                                    entryBuffer = ""
                                    confirmError = false
                                    step = PinChangeStep.ConfirmNew
                                }
                            }
                        },
                        onBackspace = { entryBuffer = PinEntryLogic.backspace(entryBuffer) },
                        onBack = onCancel,
                    )
                PinChangeStep.ConfirmNew ->
                    PinSetPinScreen(
                        state =
                            PinEntryUiState(
                                filledDots = entryBuffer.length,
                                mode = if (confirmError) PinEntryMode.Error else PinEntryMode.Default,
                                digits = entryBuffer,
                            ),
                        headingRes = R.string.pin_confirm_heading,
                        onDigit = { digit ->
                            when (val result = PinEntryLogic.appendDigit(entryBuffer, digit, hadError = confirmError)) {
                                is PinEntryLogic.DigitResult.Updated -> {
                                    confirmError = false
                                    entryBuffer = result.buffer
                                }
                                is PinEntryLogic.DigitResult.Completed -> {
                                    confirmError = false
                                    if (result.pin == newPin) {
                                        entryBuffer = result.pin
                                        val confirmedPin = newPin
                                        scope.launch {
                                            resetPin(confirmedPin)
                                            onDone()
                                        }
                                    } else {
                                        confirmError = true
                                        entryBuffer = ""
                                    }
                                }
                            }
                        },
                        onBackspace = { entryBuffer = PinEntryLogic.backspace(entryBuffer) },
                        onBack = {
                            entryBuffer = ""
                            step = PinChangeStep.EnterNew
                        },
                    )
            }
        }
    }
}

@Preview(
    name = "PIN change flow",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun PinChangeFlowPreview() {
    ProExpenseTheme {
        PinChangeFlow(onDone = {}, onCancel = {})
    }
}
