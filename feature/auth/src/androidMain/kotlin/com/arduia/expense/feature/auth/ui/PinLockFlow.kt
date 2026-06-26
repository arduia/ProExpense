package com.arduia.expense.feature.auth.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import com.arduia.expense.data.Result
import com.arduia.expense.feature.auth.BiometricAuthenticator
import com.arduia.expense.feature.auth.PinAuthRepository
import com.arduia.expense.feature.auth.R
import com.arduia.expense.feature.auth.ui.preview.PinEntryMode
import com.arduia.expense.feature.auth.ui.preview.PinEntryUiState
import com.arduia.expense.feature.auth.ui.preview.pinSecurityQuestions
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.rememberProReduceMotion
import com.arduia.expense.ui.theme.stepTransition
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

private const val RECOVERY_MAX_ATTEMPTS = 5

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
                lockoutUntil = null
                countdownLabel = null
                entryError = false
                break
            }
            val totalSeconds = (remaining / 1000).toInt()
            countdownLabel = "%d:%02d".format(totalSeconds / 60, totalSeconds % 60)
            delay(1000)
        }
    }

    val canUseBiometric = biometricEnrolled && activity != null &&
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

    fun handleDigit(digit: Int) {
        if (lockoutUntil != null) return
        if (entryError) {
            entryError = false
            entryBuffer = ""
        }
        if (entryBuffer.length < 6) {
            entryBuffer += digit
            if (entryBuffer.length == 6) {
                val pin = entryBuffer
                scope.launch {
                    when (val result = pinAuthRepository.verifyPin(pin)) {
                        is Result.Success -> {
                            if (result.data) {
                                pinAuthRepository.resetFailedAttempts()
                                onUnlocked()
                            } else {
                                pinAuthRepository.incrementFailedAttempts()
                                entryBuffer = ""
                                entryError = true
                                when (val lockResult = pinAuthRepository.getLockoutUntilMs()) {
                                    is Result.Success -> lockoutUntil = lockResult.data
                                    is Result.Error -> Unit
                                }
                            }
                        }
                        is Result.Error -> {
                            entryBuffer = ""
                            entryError = true
                        }
                    }
                }
            }
        }
    }

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
            label = "pinLockStep",
        ) { target ->
            when (target) {
                PinLockStep.Entry -> PinEntryScreen(
                    state = PinEntryUiState(
                        filledDots = entryBuffer.length,
                        mode = when {
                            lockoutUntil != null -> PinEntryMode.Locked
                            entryError -> PinEntryMode.Error
                            else -> PinEntryMode.Default
                        },
                        countdownLabel = countdownLabel,
                        showBiometric = canUseBiometric && lockoutUntil == null,
                    ),
                    onDigit = ::handleDigit,
                    onBackspace = {
                        if (entryBuffer.isNotEmpty()) entryBuffer = entryBuffer.dropLast(1)
                    },
                    onBiometric = { startBiometric() },
                    onForgot = {
                        recoveryAnswer = ""
                        recoveryAttempts = 0
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
                    val questionText = pinSecurityQuestions
                        .firstOrNull { it.id == recoveryQuestionId }
                        ?.let { stringResource(it.textRes) }
                        ?: ""
                    val attemptsLabel = if (recoveryError) {
                        stringResource(R.string.pin_recover_wrong)
                    } else {
                        stringResource(R.string.pin_recover_attempts, recoveryAttempts, RECOVERY_MAX_ATTEMPTS)
                    }
                    PinRecoveryScreen(
                        questionText = questionText,
                        answer = recoveryAnswer,
                        attemptsLabel = attemptsLabel,
                        onAnswerChange = { recoveryAnswer = it },
                        onVerify = {
                            scope.launch {
                                when (val result = pinAuthRepository.verifySecurityAnswer(recoveryAnswer)) {
                                    is Result.Success -> {
                                        if (result.data) {
                                            recoveryError = false
                                            newPin = ""
                                            entryBuffer = ""
                                            step = PinLockStep.RecoverNewPin
                                        } else {
                                            recoveryAttempts += 1
                                            recoveryError = true
                                            recoveryAnswer = ""
                                            if (recoveryAttempts >= RECOVERY_MAX_ATTEMPTS) {
                                                step = PinLockStep.Entry
                                            }
                                        }
                                    }
                                    is Result.Error -> {
                                        recoveryError = true
                                        recoveryAnswer = ""
                                    }
                                }
                            }
                        },
                        onBack = { step = PinLockStep.Entry },
                    )
                }
                PinLockStep.RecoverNewPin -> PinSetPinScreen(
                    state = PinEntryUiState(filledDots = entryBuffer.length),
                    headingRes = R.string.pin_set_new_heading,
                    onDigit = { digit ->
                        if (entryBuffer.length < 6) {
                            entryBuffer += digit
                            if (entryBuffer.length == 6) {
                                newPin = entryBuffer
                                entryBuffer = ""
                                confirmError = false
                                step = PinLockStep.RecoverConfirmPin
                            }
                        }
                    },
                    onBackspace = {
                        if (entryBuffer.isNotEmpty()) entryBuffer = entryBuffer.dropLast(1)
                    },
                    onBack = { step = PinLockStep.Recovery },
                )
                PinLockStep.RecoverConfirmPin -> PinSetPinScreen(
                    state = PinEntryUiState(
                        filledDots = entryBuffer.length,
                        mode = if (confirmError) PinEntryMode.Error else PinEntryMode.Default,
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
                                    val confirmedPin = newPin
                                    scope.launch {
                                        pinAuthRepository.setPin(confirmedPin)
                                        pinAuthRepository.resetFailedAttempts()
                                        onUnlocked()
                                    }
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
                    onBack = { step = PinLockStep.RecoverNewPin },
                )
            }
        }
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
