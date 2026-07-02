package com.arduia.expense.feature.logging.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.data.Result
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
import com.arduia.expense.ui.design.TagLinkKind
import com.arduia.expense.ui.design.TagLinkOption
import com.arduia.expense.ui.design.shortDateLabel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.launch
import org.koin.compose.currentKoinScope
import org.koin.compose.koinInject

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
    )

    @Composable
    fun EditExpenseFlow(
        recordId: String,
        onDismiss: () -> Unit,
        onSaved: () -> Unit,
        modifier: Modifier = Modifier,
        homeCurrencySymbol: String = "$",
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
    ) {
        val scope = rememberCoroutineScope()
        val viewModel = rememberLoggingViewModel()
        val uiState by viewModel.uiState.collectAsState()
        val (defaultCategories, customCategories) = rememberCategoryLists()

        val tagEvents = uiState.tagOptions.toTagLinkOptions(TagOptionKind.EVENT, homeCurrencySymbol)
        val tagDebts = uiState.tagOptions.toTagLinkOptions(TagOptionKind.DEBT, homeCurrencySymbol)
        val linkedEvent = initialLinkedEventId?.let { id -> tagEvents.firstOrNull { it.id == id } }

        com.arduia.expense.feature.logging.ui.QuickLogFlow(
            onDismiss = onDismiss,
            startState = initialDraftState ?: ExpenseEntryState(
                currencyCode = currencyCode,
                selectedCategoryId = defaultCategoryId,
                linkedTagId = linkedEvent?.id,
                linkedTagKind = linkedEvent?.kind,
                linkedTagLabel = linkedEvent?.title,
            ),
            showDraftPrompt = initialDraftState != null,
            draftAmountLabel = initialDraftState?.let { homeCurrencySymbol + AmountInput.formatDisplay(it.rawAmount) },
            onSaved = { state ->
                scope.launch {
                    when (viewModel.save(state.toSaveInput())) {
                        is SaveExpenseOutcome.Saved -> onSaved(state.toHandoff())
                        SaveExpenseOutcome.InvalidAmount -> {} // UI already has inline validation
                        is SaveExpenseOutcome.Failed -> {} // Error silently; UI already has toast handling
                    }
                }
            },
            tagEvents = tagEvents,
            tagDebts = tagDebts,
            defaultCategories = defaultCategories,
            customCategories = customCategories,
            modifier = modifier,
        )
    }

    @Composable
    override fun EditExpenseFlow(
        recordId: String,
        onDismiss: () -> Unit,
        onSaved: () -> Unit,
        modifier: Modifier,
        homeCurrencySymbol: String,
    ) {
        val scope = rememberCoroutineScope()
        val viewModel = rememberLoggingViewModel()
        val uiState by viewModel.uiState.collectAsState()
        val (defaultCategories, customCategories) = rememberCategoryLists()

        val tagEvents = uiState.tagOptions.toTagLinkOptions(TagOptionKind.EVENT, homeCurrencySymbol)
        val tagDebts = uiState.tagOptions.toTagLinkOptions(TagOptionKind.DEBT, homeCurrencySymbol)
        val eventNames = uiState.tagOptions.filter { it.kind == TagOptionKind.EVENT }
            .associate { it.id to it.eventName.orEmpty() }
        val debtNames = uiState.tagOptions.filter { it.kind == TagOptionKind.DEBT }
            .associate { it.id to debtLabel(it) }

        var startState by remember(recordId) { mutableStateOf<ExpenseEntryState?>(null) }

        LaunchedEffect(recordId, eventNames, debtNames) {
            viewModel.loadForEdit(recordId)
        }

        val record = uiState.existingRecord
        if (record != null && record.id.value == recordId && startState == null) {
            startState = record.toEntryState(eventNames, debtNames)
        }

        val loaded = startState
        if (loaded != null && record != null) {
            com.arduia.expense.feature.logging.ui.QuickLogFlow(
                onDismiss = onDismiss,
                startState = loaded,
                onSaved = { state ->
                    scope.launch {
                        when (viewModel.update(state.toSaveInput())) {
                            is SaveExpenseOutcome.Saved -> onSaved()
                            SaveExpenseOutcome.InvalidAmount -> {}
                            is SaveExpenseOutcome.Failed -> {}
                        }
                    }
                },
                tagEvents = tagEvents,
                tagDebts = tagDebts,
                defaultCategories = defaultCategories,
                customCategories = customCategories,
                modifier = modifier,
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

/** Live default/custom category chip lists sourced from [CategoryRepository], not hardcoded. */
@Composable
private fun rememberCategoryLists(): Pair<List<Pair<String, String>>, List<Pair<String, String>>> {
    val categoryRepository: CategoryRepository = koinInject()
    val categories by categoryRepository.observeAll().collectAsState(emptyList())
    val defaultCategories = categories
        .filter { !it.isCustom }
        .sortedBy { it.sortOrder }
        .map { it.id.value to it.name }
    val customCategories = categories
        .filter { it.isCustom }
        .sortedBy { it.sortOrder }
        .map { it.id.value to it.name }
    return defaultCategories to customCategories
}

private fun List<TagOption>.toTagLinkOptions(kind: TagOptionKind, currencySymbol: String): List<TagLinkOption> =
    filter { it.kind == kind }.map { option ->
        when (kind) {
            TagOptionKind.EVENT -> TagLinkOption(
                id = option.id,
                title = option.eventName.orEmpty(),
                subtitle = shortDateLabel(option.eventStartEpochMillis ?: 0L) + " - " +
                    shortDateLabel(option.eventEndEpochMillis ?: 0L),
                kind = TagLinkKind.Event,
            )
            TagOptionKind.DEBT -> TagLinkOption(
                id = option.id,
                title = debtLabel(option),
                subtitle = moneyLabel(option.debtAmountCents ?: 0L, currencySymbol),
                kind = TagLinkKind.Debt,
            )
        }
    }

private fun debtLabel(option: TagOption): String {
    val direction = if (option.debtIsOwedToMe == true) "Lent" else "Owe"
    return "$direction · ${option.debtPersonName.orEmpty()}"
}

private fun ExpenseEntryState.toSaveInput(): SaveExpenseInput = SaveExpenseInput(
    rawAmount = rawAmount,
    currencyCode = currencyCode,
    categoryId = selectedCategoryId,
    note = note,
    recordedAtEpochMillis = recordedAtEpochMillis,
    linkTagId = linkedTagId,
    linkTagKind = when (linkedTagKind) {
        TagLinkKind.Event -> TagOptionKind.EVENT
        TagLinkKind.Debt -> TagOptionKind.DEBT
        null -> null
    },
)

object LoggingFeatureUi : LoggingFeatureEntry by LoggingFeatureEntryImpl()

private fun ExpenseEntryState.toHandoff(): LoggedExpenseHandoff = LoggedExpenseHandoff(
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
    val (tagId, tagKind, tagLabel) = when (val current = link) {
        is RecordLink.ToEvent -> Triple(current.eventId.value, TagLinkKind.Event, eventNames[current.eventId.value])
        is RecordLink.ToDebt -> Triple(current.debtId.value, TagLinkKind.Debt, debtNames[current.debtId.value])
        else -> Triple(null, null, null)
    }
    return ExpenseEntryState(
        rawAmount = if (money.amount.valueInCents % 100 == 0L) {
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
    )
}

private fun moneyLabel(valueInCents: Long, currencySymbol: String): String =
    currencySymbol + AmountInput.formatDisplay(String.format(Locale.US, "%.2f", valueInCents / 100.0))
