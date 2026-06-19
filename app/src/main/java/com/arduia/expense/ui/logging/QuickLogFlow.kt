package com.arduia.expense.ui.logging

import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.arduia.expense.feature.logging.AmountInputLogic
import com.arduia.expense.ui.navigation.stepTransition
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.rememberProReduceMotion

enum class QuickLogStep {
    Amount,
    Details,
}

@Composable
fun QuickLogFlow(
    onDismiss: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
    initialStep: QuickLogStep = QuickLogStep.Amount,
) {
    var step by rememberSaveable { mutableStateOf(initialStep) }
    var amount by rememberSaveable { mutableStateOf("") }
    var selectedCategoryId by rememberSaveable { mutableStateOf("food") }
    var note by rememberSaveable { mutableStateOf("") }
    val dateLabel = "Today, May 25"
    val timeLabel = "12:30 PM"
    var eventTag by rememberSaveable { mutableStateOf<String?>(null) }
    val motion = ProExpenseTheme.motion
    val reduceMotion = rememberProReduceMotion()

    AnimatedContent(
        targetState = step,
        modifier = modifier,
        transitionSpec = {
            stepTransition(
                motion = motion,
                fromIndex = initialState.ordinal,
                toIndex = targetState.ordinal,
                reduceMotion = reduceMotion,
            )
        },
        label = "quickLogNav",
    ) { currentStep ->
        when (currentStep) {
            QuickLogStep.Amount -> AddAmountScreen(
                modifier = Modifier,
                amount = amount,
                selectedCategoryId = selectedCategoryId,
                onAmountChange = { amount = it },
                onCategorySelected = { selectedCategoryId = it },
                onClose = onDismiss,
                onSave = onSaved,
                onNext = { step = QuickLogStep.Details },
            )
            QuickLogStep.Details -> {
                val display = AmountInputLogic.toDisplay(amount)
                val amountLabel = "$${
                    if (display.decimal != null) "${display.whole}.${display.decimal}" else display.whole
                }"
                AddDetailsScreen(
                    modifier = Modifier,
                    amountLabel = amountLabel,
                    selectedCategoryId = selectedCategoryId,
                    note = note,
                    dateLabel = dateLabel,
                    timeLabel = timeLabel,
                    eventTag = eventTag,
                    onNoteChange = { note = it },
                    onCategorySelected = { selectedCategoryId = it },
                    onBack = { step = QuickLogStep.Amount },
                    onEditAmount = { step = QuickLogStep.Amount },
                    onClearTag = { eventTag = null },
                    onSave = onSaved,
                )
            }
        }
    }
}
