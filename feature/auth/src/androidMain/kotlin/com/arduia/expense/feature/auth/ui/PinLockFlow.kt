package com.arduia.expense.feature.auth.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import com.arduia.expense.data.ClearDataRepository
import com.arduia.expense.data.Result
import com.arduia.expense.feature.auth.BiometricAuthenticator
import com.arduia.expense.feature.auth.DisablePinUseCase
import com.arduia.expense.feature.auth.PIN_RECOVERY_MAX_ATTEMPTS
import com.arduia.expense.feature.auth.PinAuthRepository
import com.arduia.expense.feature.auth.PinEntryLogic
import com.arduia.expense.feature.auth.PinEntryMode
import com.arduia.expense.feature.auth.PinEntryUiState
import com.arduia.expense.feature.auth.R
import com.arduia.expense.feature.auth.RecoveryAnswerResult
import com.arduia.expense.feature.auth.ResetPinUseCase
import com.arduia.expense.feature.auth.VerifyPinResult
import com.arduia.expense.feature.auth.VerifyPinUseCase
import com.arduia.expense.feature.auth.VerifyRecoveryAnswerUseCase
import com.arduia.expense.feature.auth.ui.preview.pinSecurityQuestions
import com.arduia.expense.ui.design.ProAlertDialog
import com.arduia.expense.ui.design.ProButtonVariant
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.rememberProReduceMotion
import com.arduia.expense.ui.theme.stepTransition
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

private enum class PinLockStep { Entry, Recovery, RecoverNewPin, RecoverConfirmPin }

@Composable
fun PinLockFlow(
    onUnlocked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val motion = ProExpenseTheme.motion
    val reduceMotion = rememberProReduceMotion()
    val pinAuthRepository: PinAuthRepository = koinInject()
    val verifyPin: VerifyPinUseCase = koinInject()
    val verifyRecoveryAnswer: VerifyRecoveryAnswerUseCase = koinInject()
    val resetPin: ResetPinUseCase = koinInject()
    val disablePin: DisablePinUseCase = koinInject()
    val clearDataRepository: ClearDataRepository = koinInject()
    val scope = rememberCoroutineScope()
    val activity = LocalContext.current as? FragmentActivity

    var step by remember { mutableStateOf(PinLockStep.Entry) }
    var entryBuffer by remember { mutableStateOf("") }
    var entryError by remember { mutableStateOf(false) }
    var lockoutUntil by remember { mutableStateOf<Long?>(null) }
    var countdownLabel by remember { mutableStateOf<String?>(null) }
    var biometricEnrolled by remember { mutableStateOf(false) }

    var recoveryQuestionId by remember { mutableStateOf<String?>(null) }
    var recoveryAnswer by remember { mutableStateOf("") }
    var recoveryAttempts by remember { mutableStateOf(0) }
    var recoveryError by remember { mutableStateOf(false) }
    var recoveryExhausted by remember { mutableStateOf(false) }
    var showResetConfirm by remember { mutableStateOf(false) }
    var newPin by remember { mutableStateOf("") }
    var confirmError by remember { mutableStateOf(false) }

    val biometricTitle = stringResource(R.string.pin_biometric_prompt_title)
    val biometricSubtitle = stringResource(R.string.pin_biometric_prompt_subtitle)
    val biometricNegative = stringResource(R.string.pin_biometric_prompt_negative)

    LaunchedEffect(Unit) {
        when (val result = pinAuthRepository.getLockoutUntilMs()) {
            is Result.Success -> lockoutUntil = result.data
            is Result.Error -> Unit
        }
        when (val result = pinAuthRepository.isBiometricEnrolled()) {
            is Result.Success -> biometricEnrolled = result.data
            is Result.Error -> Unit
        }
    }

    LaunchedEffect(lockoutUntil) {
        val until = lockoutUntil
        if (until == null) {
            countdownLabel = null
            return@LaunchedEffect
        }
        while (true) {
            val remaining = until - System.currentTimeMillis()
            if (remaining <= 0) {
                // The countdown completing must reset the *persisted* attempt count (US-AUTH-5:
                // "attempt counter resets once the countdown completes") — clearing only local UI
                // state left the next wrong digit escalate straight to the next lockout tier
                // (30s -> 60s -> 5min) instead of a fresh 5-attempt budget.
                pinAuthRepository.resetFailedAttempts()
                lockoutUntil = null
                countdownLabel = null
                entryError = false
                recoveryAttempts = 0
                recoveryError = false
                break
            }
            countdownLabel = PinEntryLogic.countdownLabel(remaining)
            delay(1000)
        }
    }

    val canUseBiometric =
        biometricEnrolled &&
            activity != null &&
            BiometricAuthenticator.isAvailable(activity)

    fun startBiometric() {
        val current = activity ?: return
        BiometricAuthenticator.authenticate(
            activity = current,
            title = biometricTitle,
            subtitle = biometricSubtitle,
            negativeButtonText = biometricNegative,
            onSuccess = {
                scope.launch {
                    pinAuthRepository.resetFailedAttempts()
                    onUnlocked()
                }
            },
            onError = {},
        )
    }

    // Auto-prompt once per lock-screen appearance (US-AUTH-6 Scenario 2) — a cancelled/failed
    // prompt just leaves the already-visible PIN entry as the fallback, no extra state needed.
    var hasAutoPromptedBiometric by remember { mutableStateOf(false) }
    LaunchedEffect(canUseBiometric, lockoutUntil, step) {
        if (!hasAutoPromptedBiometric && canUseBiometric && lockoutUntil == null && step == PinLockStep.Entry) {
            hasAutoPromptedBiometric = true
            startBiometric()
        }
    }

    fun handleDigit(digit: Int) {
        if (lockoutUntil != null) return
        when (val result = PinEntryLogic.appendDigit(entryBuffer, digit, hadError = entryError)) {
            is PinEntryLogic.DigitResult.Updated -> {
                entryError = false
                entryBuffer = result.buffer
            }
            is PinEntryLogic.DigitResult.Completed -> {
                entryError = false
                entryBuffer = result.pin
                scope.launch {
                    when (val verifyResult = verifyPin(result.pin)) {
                        is VerifyPinResult.Unlocked -> onUnlocked()
                        is VerifyPinResult.Incorrect -> {
                            entryBuffer = ""
                            entryError = true
                            lockoutUntil = verifyResult.lockoutUntilMs
                        }
                        is VerifyPinResult.Error -> {
                            entryBuffer = ""
                            entryError = true
                        }
                    }
                }
            }
        }
    }

    Box(
        modifier =
            modifier
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
            label = "pinLockStep",
        ) { target ->
            when (target) {
                PinLockStep.Entry ->
                    PinEntryScreen(
                        state =
                            PinEntryUiState(
                                filledDots = entryBuffer.length,
                                mode = PinEntryLogic.entryMode(lockedOut = lockoutUntil != null, error = entryError),
                                countdownLabel = countdownLabel,
                                showBiometric = canUseBiometric && lockoutUntil == null,
                            ),
                        onDigit = ::handleDigit,
                        onBackspace = { entryBuffer = PinEntryLogic.backspace(entryBuffer) },
                        onBiometric = { startBiometric() },
                        onForgot = {
                            // Only the input field and its inline error clear on re-entry — the
                            // attempt count and exhausted flag must survive a Back/Forgot round trip
                            // within this session, or a user could reset their 5-attempt recovery
                            // budget indefinitely just by backing out and tapping Forgot again
                            // (US-AUTH-8: same lockout pattern as PIN entry).
                            recoveryAnswer = ""
                            recoveryError = false
                            scope.launch {
                                when (val result = pinAuthRepository.getSecurityQuestionId()) {
                                    is Result.Success -> recoveryQuestionId = result.data
                                    is Result.Error -> Unit
                                }
                            }
                            step = PinLockStep.Recovery
                        },
                    )
                PinLockStep.Recovery -> {
                    val questionText =
                        pinSecurityQuestions
                            .firstOrNull { it.id == recoveryQuestionId }
                            ?.let { stringResource(it.textRes) }
                            ?: ""
                    val attemptsLabel =
                        if (recoveryError) {
                            stringResource(R.string.pin_recover_wrong)
                        } else {
                            stringResource(R.string.pin_recover_attempts, recoveryAttempts, PIN_RECOVERY_MAX_ATTEMPTS)
                        }
                    PinRecoveryScreen(
                        questionText = questionText,
                        answer = recoveryAnswer,
                        attemptsLabel = attemptsLabel,
                        onAnswerChange = { recoveryAnswer = it },
                        verifyEnabled = recoveryAnswer.isNotBlank() && !recoveryExhausted && lockoutUntil == null,
                        showResetOption = recoveryExhausted,
                        onResetApp = { showResetConfirm = true },
                        onVerify = {
                            scope.launch {
                                when (val result = verifyRecoveryAnswer(recoveryAnswer, recoveryAttempts)) {
                                    is RecoveryAnswerResult.Correct -> {
                                        pinAuthRepository.resetFailedAttempts()
                                        recoveryError = false
                                        newPin = ""
                                        entryBuffer = ""
                                        step = PinLockStep.RecoverNewPin
                                    }
                                    is RecoveryAnswerResult.Incorrect -> {
                                        recoveryAttempts = result.attempts
                                        recoveryError = true
                                        recoveryAnswer = ""
                                        // Shares the PIN-entry lockout counter (US-AUTH-8: "same
                                        // pattern as PIN lockout") so it's persisted and survives
                                        // a restart mid-lockout, unlike a purely local counter.
                                        pinAuthRepository.incrementFailedAttempts()
                                        when (val lockoutResult = pinAuthRepository.getLockoutUntilMs()) {
                                            is Result.Success -> lockoutUntil = lockoutResult.data
                                            is Result.Error -> Unit
                                        }
                                        if (result.attemptsExhausted) {
                                            recoveryExhausted = true
                                        }
                                    }
                                    is RecoveryAnswerResult.Error -> {
                                        recoveryError = true
                                        recoveryAnswer = ""
                                    }
                                }
                            }
                        },
                        onBack = { step = PinLockStep.Entry },
                    )
                }
                PinLockStep.RecoverNewPin ->
                    PinSetPinScreen(
                        state = PinEntryUiState(filledDots = entryBuffer.length),
                        headingRes = R.string.pin_set_new_heading,
                        onDigit = { digit ->
                            when (val result = PinEntryLogic.appendDigit(entryBuffer, digit)) {
                                is PinEntryLogic.DigitResult.Updated -> entryBuffer = result.buffer
                                is PinEntryLogic.DigitResult.Completed -> {
                                    newPin = result.pin
                                    entryBuffer = ""
                                    confirmError = false
                                    step = PinLockStep.RecoverConfirmPin
                                }
                            }
                        },
                        onBackspace = { entryBuffer = PinEntryLogic.backspace(entryBuffer) },
                        onBack = { step = PinLockStep.Recovery },
                    )
                PinLockStep.RecoverConfirmPin ->
                    PinSetPinScreen(
                        state =
                            PinEntryUiState(
                                filledDots = entryBuffer.length,
                                mode = if (confirmError) PinEntryMode.Error else PinEntryMode.Default,
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
                                            onUnlocked()
                                        }
                                    } else {
                                        confirmError = true
                                        entryBuffer = ""
                                    }
                                }
                            }
                        },
                        onBackspace = { entryBuffer = PinEntryLogic.backspace(entryBuffer) },
                        onBack = { step = PinLockStep.RecoverNewPin },
                    )
            }
        }

        ProAlertDialog(
            visible = showResetConfirm,
            icon = ProIconGlyph.Close,
            iconTint = colors.danger,
            iconBackground = colors.dangerTint,
            title = stringResource(R.string.pin_recover_reset_title),
            body = AnnotatedString(stringResource(R.string.pin_recover_reset_body)),
            confirmLabel = stringResource(R.string.pin_recover_reset_confirm),
            onConfirm = {
                showResetConfirm = false
                scope.launch {
                    disablePin()
                    clearDataRepository.clearAll()
                    onUnlocked()
                }
            },
            dismissLabel = stringResource(R.string.pin_recover_reset_cancel),
            onDismiss = { showResetConfirm = false },
            confirmVariant = ProButtonVariant.Danger,
        )
    }
}

@Preview(
    name = "PIN lock flow",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun PinLockFlowPreview() {
    ProExpenseTheme {
        PinLockFlow(onUnlocked = {})
    }
}
