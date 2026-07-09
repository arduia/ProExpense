package com.arduia.expense.feature.logging.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordLink
import com.arduia.expense.feature.logging.LoggedExpenseHandoff
import com.arduia.expense.feature.logging.LoggingViewModel
import com.arduia.expense.feature.logging.SaveExpenseInput
import com.arduia.expense.feature.logging.SaveExpenseOutcome
import com.arduia.expense.feature.logging.TagOption
import com.arduia.expense.feature.logging.TagOptionKind
import com.arduia.expense.feature.logging.ui.QuickLogFlow
import com.arduia.expense.feature.logging.ui.preview.ExpenseEntryState
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.PlatformDateFormatter
import com.arduia.expense.ui.design.TagLinkKind
import com.arduia.expense.ui.design.TagLinkOption
import kotlinx.coroutines.launch
import org.koin.compose.currentKoinScope
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

interface LoggingFeatureEntry {
    @Composable
    fun QuickLogFlow(
        onDismiss: () -> Unit,
        onSaved: (LoggedExpenseHandoff) -> Unit,
        modifier: Modifier = Modifier,
        currencyCode: String = "USD",
        defaultCategoryId: String = "food",
        initialLinkedEventId: String? = null,
        initialDraftState: ExpenseEntryState? = null,
        homeCurrencySymbol: String = "$",
        homeCurrencyCode: String = currencyCode,
        onSaveFailed: (String) -> Unit = {},
        defaultCategories: List<Pair<String, String>> = emptyList(),
        customCategories: List<Pair<String, String>> = emptyList(),
        defaultIncomeCategories: List<Pair<String, String>> = emptyList(),
        customIncomeCategories: List<Pair<String, String>> = emptyList(),
        onAddCategory: () -> Unit = {},
    )

    @Composable
    fun EditExpenseFlow(
        recordId: String,
        onDismiss: () -> Unit,
        onSaved: () -> Unit,
        modifier: Modifier = Modifier,
        homeCurrencySymbol: String = "$",
        onSaveFailed: (String) -> Unit = {},
        defaultCategories: List<Pair<String, String>> = emptyList(),
        customCategories: List<Pair<String, String>> = emptyList(),
        defaultIncomeCategories: List<Pair<String, String>> = emptyList(),
        customIncomeCategories: List<Pair<String, String>> = emptyList(),
        onAddCategory: () -> Unit = {},
    )
}

internal class LoggingFeatureEntryImpl : LoggingFeatureEntry {
    @Composable
    override fun QuickLogFlow(
        onDismiss: () -> Unit,
        onSaved: (LoggedExpenseHandoff) -> Unit,
        modifier: Modifier,
        currencyCode: String,
        defaultCategoryId: String,
        initialLinkedEventId: String?,
        initialDraftState: ExpenseEntryState?,
        homeCurrencySymbol: String,
        homeCurrencyCode: String,
        onSaveFailed: (String) -> Unit,
        defaultCategories: List<Pair<String, String>>,
        customCategories: List<Pair<String, String>>,
        defaultIncomeCategories: List<Pair<String, String>>,
        customIncomeCategories: List<Pair<String, String>>,
        onAddCategory: () -> Unit,
    ) {
        val scope = rememberCoroutineScope()
        val viewModel = rememberLoggingViewModel()
        val uiState by viewModel.uiState.collectAsState()

        // A resumed draft is unauthenticated (US-LOG-7: shown before any PIN check), so it must
        // never expose live event/debt names or amounts via the `@` tag sheet — restrict to
        // exactly what the user already typed until the app is unlocked.
        val restrictSensitiveData = initialDraftState != null
        val tagEvents =
            if (restrictSensitiveData) {
                emptyList()
            } else {
                uiState.tagOptions.toTagLinkOptions(TagOptionKind.EVENT, homeCurrencySymbol)
            }
        val tagDebts =
            if (restrictSensitiveData) {
                emptyList()
            } else {
                uiState.tagOptions.toTagLinkOptions(TagOptionKind.DEBT, homeCurrencySymbol)
            }
        val linkedEvent = initialLinkedEventId?.let { id -> tagEvents.firstOrNull { it.id == id } }
        var saveErrorMessage by remember { mutableStateOf<String?>(null) }

        com.arduia.expense.feature.logging.ui.QuickLogFlow(
            onDismiss = onDismiss,
            startState =
                initialDraftState ?: ExpenseEntryState(
                    currencyCode = currencyCode,
                    homeCurrencyCode = homeCurrencyCode,
                    selectedCategoryId = defaultCategoryId,
                    linkedTagId = linkedEvent?.id,
                    linkedTagKind = linkedEvent?.kind,
                    linkedTagLabel = linkedEvent?.title,
                    // recordedAtEpochMillis default is a fixed preview/screenshot fixture (US-LOG
                    // baselines need a deterministic date) — a brand-new entry must start at "now".
                    recordedAtEpochMillis = System.currentTimeMillis(),
                ),
            showDraftPrompt = initialDraftState != null,
            draftAmountLabel = initialDraftState?.let { homeCurrencySymbol + AmountInput.formatDisplay(it.rawAmount) },
            onSaved = { state ->
                scope.launch {
                    when (val outcome = viewModel.save(state.toSaveInput())) {
                        is SaveExpenseOutcome.Saved -> onSaved(state.toHandoff())
                        SaveExpenseOutcome.InvalidAmount -> {} // UI already has inline validation
                        SaveExpenseOutcome.InvalidExchangeRate -> {} // UI blocks Save until the rate is valid
                        is SaveExpenseOutcome.Failed -> {
                            saveErrorMessage = outcome.message
                            onSaveFailed(outcome.message)
                        }
                    }
                }
            },
            tagEvents = tagEvents,
            tagDebts = tagDebts,
            defaultCategories = defaultCategories,
            customCategories = customCategories,
            defaultIncomeCategories = defaultIncomeCategories,
            customIncomeCategories = customIncomeCategories,
            modifier = modifier,
            saveErrorMessage = saveErrorMessage,
            initialLinkedTag = linkedEvent,
            onAddCategory = onAddCategory,
        )
    }

    @Composable
    override fun EditExpenseFlow(
        recordId: String,
        onDismiss: () -> Unit,
        onSaved: () -> Unit,
        modifier: Modifier,
        homeCurrencySymbol: String,
        onSaveFailed: (String) -> Unit,
        defaultCategories: List<Pair<String, String>>,
        customCategories: List<Pair<String, String>>,
        defaultIncomeCategories: List<Pair<String, String>>,
        customIncomeCategories: List<Pair<String, String>>,
        onAddCategory: () -> Unit,
    ) {
        val scope = rememberCoroutineScope()
        val viewModel = rememberLoggingViewModel()
        val uiState by viewModel.uiState.collectAsState()

        val tagEvents = uiState.tagOptions.toTagLinkOptions(TagOptionKind.EVENT, homeCurrencySymbol)
        val tagDebts = uiState.tagOptions.toTagLinkOptions(TagOptionKind.DEBT, homeCurrencySymbol)
        val eventNames =
            uiState.tagOptions
                .filter { it.kind == TagOptionKind.EVENT }
                .associate { it.id to it.eventName.orEmpty() }
        val debtNames =
            uiState.tagOptions
                .filter { it.kind == TagOptionKind.DEBT }
                .associate { it.id to debtLabel(it) }

        var startState by remember(recordId) { mutableStateOf<ExpenseEntryState?>(null) }

        LaunchedEffect(recordId, eventNames, debtNames) {
            viewModel.loadForEdit(recordId)
        }

        val record = uiState.existingRecord
        if (record != null && record.id.value == recordId && startState == null) {
            startState = record.toEntryState(eventNames, debtNames)
        }

        var saveErrorMessage by remember(recordId) { mutableStateOf<String?>(null) }

        val loaded = startState
        if (loaded != null && record != null) {
            com.arduia.expense.feature.logging.ui.QuickLogFlow(
                onDismiss = onDismiss,
                startState = loaded,
                onSaved = { state ->
                    scope.launch {
                        when (val outcome = viewModel.update(state.toSaveInput())) {
                            is SaveExpenseOutcome.Saved -> onSaved()
                            SaveExpenseOutcome.InvalidAmount -> {}
                            SaveExpenseOutcome.InvalidExchangeRate -> {}
                            is SaveExpenseOutcome.Failed -> {
                                saveErrorMessage = outcome.message
                                onSaveFailed(outcome.message)
                            }
                        }
                    }
                },
                tagEvents = tagEvents,
                tagDebts = tagDebts,
                defaultCategories = defaultCategories,
                customCategories = customCategories,
                defaultIncomeCategories = defaultIncomeCategories,
                customIncomeCategories = customIncomeCategories,
                modifier = modifier,
                // Editing writes into the existing record via update(), never via the create-path
                // draft slot — persisting it there would surface as a duplicate-creating "Continue"
                // prompt after simply backing out of an edit.
                persistDraft = false,
                saveErrorMessage = saveErrorMessage,
                onAddCategory = onAddCategory,
            )
        }
    }
}

@Composable
private fun rememberLoggingViewModel(): LoggingViewModel {
    val scope = currentKoinScope()
    val viewModel = remember { scope.get<LoggingViewModel>() }
    DisposableEffect(viewModel) {
        onDispose { viewModel.onCleared() }
    }
    return viewModel
}

private fun List<TagOption>.toTagLinkOptions(
    kind: TagOptionKind,
    currencySymbol: String,
): List<TagLinkOption> =
    // Closed events can't take new links (US-EVT-5) — exclude them from what's selectable here.
    // Debt/event *names* for an already-linked record are still resolved from the full,
    // unfiltered tagOptions list elsewhere, so this never breaks an existing closed-event link.
    filter { it.kind == kind && (kind != TagOptionKind.EVENT || !it.eventIsClosed) }.map { option ->
        when (kind) {
            TagOptionKind.EVENT ->
                TagLinkOption(
                    id = option.id,
                    title = option.eventName.orEmpty(),
                    subtitle =
                        PlatformDateFormatter.shortDateLabel(option.eventStartEpochMillis ?: 0L) + " - " +
                            PlatformDateFormatter.shortDateLabel(option.eventEndEpochMillis ?: 0L),
                    kind = TagLinkKind.Event,
                )
            TagOptionKind.DEBT ->
                TagLinkOption(
                    id = option.id,
                    title = debtLabel(option),
                    subtitle = AmountInput.formatMoney(option.debtAmountCents ?: 0L, currencySymbol),
                    kind = TagLinkKind.Debt,
                )
        }
    }

private fun debtLabel(option: TagOption): String {
    val direction = if (option.debtIsOwedToMe == true) "Lent" else "Owe"
    return "$direction · ${option.debtPersonName.orEmpty()}"
}

private fun ExpenseEntryState.toSaveInput(): SaveExpenseInput =
    SaveExpenseInput(
        rawAmount = rawAmount,
        currencyCode = currencyCode,
        categoryId = selectedCategoryId,
        note = note,
        recordedAtEpochMillis = recordedAtEpochMillis,
        linkTagId = linkedTagId,
        linkTagKind =
            when (linkedTagKind) {
                TagLinkKind.Event -> TagOptionKind.EVENT
                TagLinkKind.Debt -> TagOptionKind.DEBT
                null -> null
            },
        homeCurrencyCode = homeCurrencyCode,
        exchangeRateRaw = exchangeRateRaw,
        type = type,
    )

object LoggingFeatureUi : LoggingFeatureEntry by LoggingFeatureEntryImpl()

private fun ExpenseEntryState.toHandoff(): LoggedExpenseHandoff =
    LoggedExpenseHandoff(
        categoryId = selectedCategoryId,
        note = note,
        rawAmount = rawAmount,
        timeLabel = timeLabel,
        linkedTagLabel = linkedTagLabel,
    )

private fun FinanceRecord.toEntryState(
    eventNames: Map<String, String>,
    debtNames: Map<String, String>,
): ExpenseEntryState {
    val calendar = Calendar.getInstance().apply { timeInMillis = recordedAtEpochMillis }
    val (tagId, tagKind, tagLabel) =
        when (val current = link) {
            is RecordLink.ToEvent -> Triple(current.eventId.value, TagLinkKind.Event, eventNames[current.eventId.value])
            is RecordLink.ToDebt -> Triple(current.debtId.value, TagLinkKind.Debt, debtNames[current.debtId.value])
            else -> Triple(null, null, null)
        }
    val exchangeRateRaw =
        if (money.currency == homeCurrencyMoney.currency || money.amount.valueInCents == 0L) {
            "1"
        } else {
            String.format(
                Locale.US,
                "%.4f",
                homeCurrencyMoney.amount.valueInCents.toDouble() / money.amount.valueInCents.toDouble(),
            )
        }
    return ExpenseEntryState(
        rawAmount =
            if (money.amount.valueInCents % 100 == 0L) {
                (money.amount.valueInCents / 100).toString()
            } else {
                String.format(Locale.US, "%.2f", money.amount.valueInCents / 100.0)
            },
        selectedCategoryId = categoryId.value,
        note = note.orEmpty(),
        dateLabel = SimpleDateFormat("MMMM d, yyyy", Locale.US).format(calendar.time),
        timeLabel = SimpleDateFormat("h:mm a", Locale.US).format(calendar.time),
        recordedAtEpochMillis = recordedAtEpochMillis,
        linkedTagId = tagId,
        linkedTagKind = tagKind,
        linkedTagLabel = tagLabel,
        currencyCode = money.currency.code,
        homeCurrencyCode = homeCurrencyMoney.currency.code,
        exchangeRateRaw = exchangeRateRaw,
        type = type,
    )
}
