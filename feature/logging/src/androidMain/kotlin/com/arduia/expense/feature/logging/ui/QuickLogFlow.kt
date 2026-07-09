package com.arduia.expense.feature.logging.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.feature.logging.R
import com.arduia.expense.feature.logging.ui.preview.ExpenseEntryState
import com.arduia.expense.feature.logging.ui.preview.hasValidExchangeRate
import com.arduia.expense.feature.logging.ui.preview.previewExpenseAmountTyped
import com.arduia.expense.feature.logging.ui.preview.previewExpenseDraft
import com.arduia.expense.shared.CurrencyCatalog
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.CurrencyPickerContent
import com.arduia.expense.ui.design.DateTimePickerSheet
import com.arduia.expense.ui.design.ProBottomSheetHost
import com.arduia.expense.ui.design.ProToastHost
import com.arduia.expense.ui.design.TagLinkOption
import com.arduia.expense.ui.design.customExpenseCategories
import com.arduia.expense.ui.design.defaultExpenseCategories
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme
import com.arduia.expense.ui.theme.rememberProReduceMotion
import com.arduia.expense.ui.theme.stepTransition
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private enum class QuickLogStep {
    DraftPrompt,
    Amount,
    Details,
}

private val currencyOptions =
    CurrencyCatalog.ALL.map {
        com.arduia.expense.ui.design
            .CurrencyOption(it.code, it.name)
    }

@Composable
fun QuickLogFlow(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    startState: ExpenseEntryState = ExpenseEntryState(),
    showDraftPrompt: Boolean = false,
    draftAmountLabel: String? = null,
    onSaved: (ExpenseEntryState) -> Unit = { onDismiss() },
    tagEvents: List<TagLinkOption> = emptyList(),
    tagDebts: List<TagLinkOption> = emptyList(),
    defaultCategories: List<Pair<String, String>> = defaultExpenseCategories,
    customCategories: List<Pair<String, String>> = customExpenseCategories,
    // Editing an existing record must never write it into the resumable-draft slot — otherwise
    // backing out of an edit leaves the record's values behind as a "draft" that a later
    // Continue re-creates as a duplicate (US-HIS-6 "no duplicate rows").
    persistDraft: Boolean = true,
    // Set by the caller when an async save fails. The flow stays mounted on failure (unlike
    // success, which dismisses immediately), so this is the one outcome this composable can
    // reliably surface itself.
    saveErrorMessage: String? = null,
    // A pre-selected event/debt link (e.g. "Add expense" from an Event card) resolved from a
    // live, asynchronously-loaded tag catalog. It is often still null on first composition —
    // baking it only into `startState` would silently drop it, since `state` below is captured
    // once via `remember` and never re-reads `startState` after that. Re-applied via effect
    // below whenever it transitions from unresolved to resolved.
    initialLinkedTag: TagLinkOption? = null,
    onAddCategory: () -> Unit = {},
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val motion = ProExpenseTheme.motion
    val reduceMotion = rememberProReduceMotion()
    val context = LocalContext.current

    var step by rememberSaveable {
        mutableStateOf(
            if (showDraftPrompt) QuickLogStep.DraftPrompt.name else QuickLogStep.Amount.name,
        )
    }
    var state by remember { mutableStateOf(startState) }
    var toastMessage by remember { mutableStateOf<String?>(null) }
    var showDateTimePicker by remember { mutableStateOf(false) }
    var showCurrencySheet by remember { mutableStateOf(false) }

    // The success toast is shown by the destination screen after this flow dismisses (see
    // ExpenseApp) — this composable unmounts too quickly on save for its own toast to ever
    // render. A failed save keeps the flow open, so the error toast is shown here instead.
    LaunchedEffect(saveErrorMessage) {
        if (saveErrorMessage != null) toastMessage = saveErrorMessage
    }

    // Re-applies a pre-selected event/debt link once its async lookup resolves, since it is
    // frequently still null when `state` above captures its initial snapshot (US-EVT-4 "Add
    // expense" pre-tagging). Only applies while the user hasn't already picked a different tag.
    LaunchedEffect(initialLinkedTag) {
        val tag = initialLinkedTag
        if (tag != null && state.linkedTagId == null) {
            state = state.copy(linkedTagId = tag.id, linkedTagKind = tag.kind, linkedTagLabel = tag.title)
        }
    }

    val currentStep = QuickLogStep.valueOf(step)

    // Persist the in-progress entry as a resumable draft so it survives process death or an
    // accidental close — cleared only on an explicit save or discard, never on backing out.
    LaunchedEffect(state, currentStep) {
        if (persistDraft && currentStep != QuickLogStep.DraftPrompt) {
            ExpenseDraftPrefs.save(context, state)
        }
    }

    Box(
        modifier =
            modifier
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
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .background(colors.scrim)
                                .padding(horizontal = dimens.screenPadding),
                        contentAlignment = Alignment.Center,
                    ) {
                        ExpenseDraftDialog(
                            amountLabel =
                                draftAmountLabel ?: "$${
                                    AmountInput.formatDisplay(
                                        state.rawAmount.ifEmpty { "0" },
                                    )
                                }",
                            onContinue = { step = QuickLogStep.Amount.name },
                            onDiscard = {
                                ExpenseDraftPrefs.clear(context)
                                onDismiss()
                            },
                        )
                    }
                }
                QuickLogStep.Amount -> {
                    AddExpenseAmountScreen(
                        state = state,
                        onClose = onDismiss,
                        onKey = { key ->
                            state =
                                state.copy(
                                    rawAmount = AmountInput.applyKey(state.rawAmount, key),
                                    showZeroValidation = false,
                                )
                        },
                        onBackspace = {
                            state =
                                state.copy(
                                    rawAmount = AmountInput.applyBackspace(state.rawAmount),
                                    showZeroValidation = false,
                                )
                        },
                        onCategorySelected = { categoryId ->
                            state = state.copy(selectedCategoryId = categoryId)
                        },
                        onSave = {
                            if (!AmountInput.canProceed(state.rawAmount)) {
                                state = state.copy(showZeroValidation = true)
                            } else if (!state.hasValidExchangeRate()) {
                                // Quick-commit needs a rate first; the field only lives on Details.
                                step = QuickLogStep.Details.name
                            } else {
                                ExpenseDraftPrefs.clear(context)
                                onSaved(state)
                            }
                        },
                        onNext = {
                            if (AmountInput.canProceed(state.rawAmount)) {
                                step = QuickLogStep.Details.name
                            } else {
                                state = state.copy(showZeroValidation = true)
                            }
                        },
                        onOpenCurrencySheet = { showCurrencySheet = true },
                        defaultCategories = defaultCategories,
                        customCategories = customCategories,
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
                        onDateClick = { showDateTimePicker = true },
                        onExchangeRateChange = { rate -> state = state.copy(exchangeRateRaw = rate) },
                        onOpenTagSheet = { state = state.copy(showTagSheet = true) },
                        onCloseTagSheet = { state = state.copy(showTagSheet = false) },
                        onTagSelected = { option ->
                            state =
                                state.copy(
                                    linkedTagId = option.id,
                                    linkedTagKind = option.kind,
                                    linkedTagLabel = option.title,
                                )
                        },
                        onClearTag = {
                            state =
                                state.copy(
                                    linkedTagId = null,
                                    linkedTagKind = null,
                                    linkedTagLabel = null,
                                )
                        },
                        onSave = {
                            ExpenseDraftPrefs.clear(context)
                            onSaved(state)
                        },
                        tagEvents = tagEvents,
                        tagDebts = tagDebts,
                        showTagField = tagEvents.isNotEmpty() || tagDebts.isNotEmpty(),
                        defaultCategories = defaultCategories,
                        customCategories = customCategories,
                        onAddCategory = onAddCategory,
                    )
                }
            }
        }

        ProToastHost(
            message = toastMessage,
            onDismiss = { toastMessage = null },
        )

        DateTimePickerSheet(
            visible = showDateTimePicker,
            initialEpochMillis = state.recordedAtEpochMillis,
            onConfirm = { epochMillis ->
                val calendar = Calendar.getInstance().apply { timeInMillis = epochMillis }
                val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.US)
                val timeFormat = SimpleDateFormat("h:mm a", Locale.US)
                state =
                    state.copy(
                        recordedAtEpochMillis = epochMillis,
                        dateLabel = dateFormat.format(calendar.time),
                        timeLabel = timeFormat.format(calendar.time),
                    )
                showDateTimePicker = false
            },
            onDismiss = { showDateTimePicker = false },
        )

        ProBottomSheetHost(
            visible = showCurrencySheet,
            title = stringResource(R.string.currency_sheet_title),
            onClose = { showCurrencySheet = false },
            sheetContent = {
                CurrencyPickerContent(
                    options = currencyOptions,
                    selectedCode = state.currencyCode,
                    onSelected = { code ->
                        state =
                            state.copy(
                                currencyCode = code,
                                exchangeRateRaw = if (code == state.homeCurrencyCode) "1" else "",
                            )
                        showCurrencySheet = false
                    },
                )
            },
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

@Preview(
    name = "Quick log — foreign currency selected",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun QuickLogFlowForeignCurrencyPreview() {
    ProExpenseTheme {
        QuickLogFlow(
            onDismiss = {},
            startState = com.arduia.expense.feature.logging.ui.preview.previewExpenseAmountForeignCurrency,
        )
    }
}
