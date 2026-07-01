package com.arduia.expense.feature.auth.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.data.Result
import com.arduia.expense.feature.auth.PinAuthRepository
import com.arduia.expense.feature.auth.R
import com.arduia.expense.feature.auth.VerifyPinResult
import com.arduia.expense.feature.auth.VerifyPinUseCase
import com.arduia.expense.feature.auth.ui.preview.PinEntryMode
import com.arduia.expense.feature.auth.ui.preview.PinEntryUiState
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * Gates a security-sensitive change (disable/change PIN) behind re-entering the current PIN.
 * Reuses the same lockout state as the main unlock screen so this can't be used to brute-force
 * around it.
 */
@Composable
fun PinVerifyFlow(
    headingRes: Int,
    helperRes: Int,
    onVerified: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pinAuthRepository: PinAuthRepository = koinInject()
    val verifyPin: VerifyPinUseCase = koinInject()
    val scope = rememberCoroutineScope()

    var entryBuffer by remember { mutableStateOf("") }
    var entryError by remember { mutableStateOf(false) }
    var lockoutUntil by remember { mutableStateOf<Long?>(null) }
    var countdownLabel by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        when (val result = pinAuthRepository.getLockoutUntilMs()) {
            is Result.Success -> lockoutUntil = result.data
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
                    when (val result = verifyPin(pin)) {
                        is VerifyPinResult.Unlocked -> onVerified()
                        is VerifyPinResult.Incorrect -> {
                            entryBuffer = ""
                            entryError = true
                            lockoutUntil = result.lockoutUntilMs
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

    PinEntryScreen(
        state = PinEntryUiState(
            filledDots = entryBuffer.length,
            mode = when {
                lockoutUntil != null -> PinEntryMode.Locked
                entryError -> PinEntryMode.Error
                else -> PinEntryMode.Default
            },
            countdownLabel = countdownLabel,
            showBiometric = false,
        ),
        headingRes = headingRes,
        helperRes = helperRes,
        showForgot = false,
        onBack = onCancel,
        backLabel = stringResource(R.string.pin_disable_verify_cancel),
        onDigit = ::handleDigit,
        onBackspace = { if (entryBuffer.isNotEmpty()) entryBuffer = entryBuffer.dropLast(1) },
        onBiometric = {},
        onForgot = {},
        modifier = modifier,
    )
}

@Preview(
    name = "PIN verify — disable",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun PinVerifyFlowPreview() {
    ProExpenseTheme {
        PinVerifyFlow(
            headingRes = R.string.pin_disable_verify_heading,
            helperRes = R.string.pin_disable_verify_helper,
            onVerified = {},
            onCancel = {},
        )
    }
}
