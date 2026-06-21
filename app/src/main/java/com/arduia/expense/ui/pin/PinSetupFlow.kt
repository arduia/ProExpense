package com.arduia.expense.ui.pin

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.ui.preview.PinSetupUiState
import com.arduia.expense.ui.preview.pinSecurityQuestions
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.rememberProReduceMotion
import com.arduia.expense.ui.theme.stepTransition

private enum class PinSetupStep { Setup, Security }

@Composable
fun PinSetupFlow(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val motion = ProExpenseTheme.motion
    val reduceMotion = rememberProReduceMotion()

    var step by remember { mutableStateOf(PinSetupStep.Setup) }
    var pinAuthOn by remember { mutableStateOf(true) }
    var selectedQuestion by remember { mutableStateOf("pet") }
    var answer by remember { mutableStateOf("") }

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
                    state = PinSetupUiState(pinAuthOn = pinAuthOn),
                    onTogglePin = { pinAuthOn = it },
                    onToggleBiometric = {},
                    onRevealNew = {},
                    onRevealConfirm = {},
                    onRecoveryClick = { step = PinSetupStep.Security },
                    onSave = { step = PinSetupStep.Security },
                    onBack = onDismiss,
                )
                PinSetupStep.Security -> PinSecurityQuestionScreen(
                    questions = pinSecurityQuestions,
                    selectedId = selectedQuestion,
                    answer = answer,
                    onSelect = { selectedQuestion = it },
                    onAnswerChange = { answer = it },
                    onEnable = onDismiss,
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
