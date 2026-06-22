package com.arduia.expense.feature.logging.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.feature.logging.R
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.ProToastHost
import com.arduia.expense.feature.logging.ui.preview.ExpenseEntryState
import com.arduia.expense.feature.logging.ui.preview.previewExpenseAmountTyped
import com.arduia.expense.feature.logging.ui.preview.previewExpenseDraft
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.rememberProReduceMotion
import com.arduia.expense.ui.theme.stepTransition

private enum class QuickLogStep {
    DraftPrompt,
    Amount,
    Details,
}

@Composable
fun QuickLogFlow(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    startState: ExpenseEntryState = ExpenseEntryState(),
    showDraftPrompt: Boolean = false,
    draftAmountLabel: String? = null,
    onSaved: (ExpenseEntryState) -> Unit = { onDismiss() },
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val motion = ProExpenseTheme.motion
    val reduceMotion = rememberProReduceMotion()
    val savedMessage = stringResource(R.string.toast_expense_saved)

    var step by rememberSaveable {
        mutableStateOf(
            if (showDraftPrompt) QuickLogStep.DraftPrompt.name else QuickLogStep.Amount.name,
        )
    }
    var state by remember { mutableStateOf(startState) }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    val currentStep = QuickLogStep.valueOf(step)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper),
    ) {
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                motion.stepTransition(
                    fromIndex = initialState.ordinal,
                    toIndex = targetState.ordinal,
                    reduceMotion = reduceMotion,
                )
            },
            label = "quickLogStep",
        ) { target ->
            when (target) {
                QuickLogStep.DraftPrompt -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colors.scrim)
                            .padding(horizontal = dimens.screenPadding),
                        contentAlignment = Alignment.Center,
                    ) {
                        ExpenseDraftDialog(
                            amountLabel = draftAmountLabel ?: "$${
                                AmountInput.formatDisplay(
                                    state.rawAmount.ifEmpty { "0" },
                                )
                            }",
                            onContinue = { step = QuickLogStep.Amount.name },
                            onDiscard = onDismiss,
                        )
                    }
                }
                QuickLogStep.Amount -> {
                    AddExpenseAmountScreen(
                        state = state,
                        onClose = onDismiss,
                        onKey = { key ->
                            state = state.copy(
                                rawAmount = AmountInput.applyKey(state.rawAmount, key),
                                showZeroValidation = false,
                            )
                        },
                        onBackspace = {
                            state = state.copy(
                                rawAmount = AmountInput.applyBackspace(state.rawAmount),
                                showZeroValidation = false,
                            )
                        },
                        onCategorySelected = { categoryId ->
                            state = state.copy(selectedCategoryId = categoryId)
                        },
                        onSave = {
                            if (AmountInput.canProceed(state.rawAmount)) {
                                toastMessage = savedMessage
                                onSaved(state)
                            } else {
                                state = state.copy(showZeroValidation = true)
                            }
                        },
                        onNext = {
                            if (AmountInput.canProceed(state.rawAmount)) {
                                step = QuickLogStep.Details.name
                            } else {
                                state = state.copy(showZeroValidation = true)
                            }
                        },
                    )
                }
                QuickLogStep.Details -> {
                    AddExpenseDetailsScreen(
                        state = state,
                        onBackToAmount = { step = QuickLogStep.Amount.name },
                        onCategorySelected = { categoryId ->
                            state = state.copy(selectedCategoryId = categoryId)
                        },
                        onNoteChange = { note -> state = state.copy(note = note) },
                        onDateClick = {},
                        onOpenTagSheet = { state = state.copy(showTagSheet = true) },
                        onCloseTagSheet = { state = state.copy(showTagSheet = false) },
                        onTagSelected = { option ->
                            state = state.copy(
                                linkedTagId = option.id,
                                linkedTagKind = option.kind,
                                linkedTagLabel = option.title,
                            )
                        },
                        onClearTag = {
                            state = state.copy(
                                linkedTagId = null,
                                linkedTagKind = null,
                                linkedTagLabel = null,
                            )
                        },
                        onSave = {
                            toastMessage = savedMessage
                            onSaved(state)
                        },
                    )
                }
            }
        }

        ProToastHost(
            message = toastMessage,
            onDismiss = { toastMessage = null },
        )
    }
}

@Preview(
    name = "Quick log — amount",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun QuickLogFlowAmountPreview() {
    ProExpenseTheme {
        QuickLogFlow(
            onDismiss = {},
            startState = previewExpenseAmountTyped,
        )
    }
}

@Preview(
    name = "Quick log — draft prompt",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun QuickLogFlowDraftPreview() {
    ProExpenseTheme {
        QuickLogFlow(
            onDismiss = {},
            startState = previewExpenseDraft,
            showDraftPrompt = true,
            draftAmountLabel = "$12.50",
        )
    }
}
