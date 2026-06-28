package com.arduia.expense.feature.logging.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.arduia.expense.data.DebtRepository
import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.DebtId
import com.arduia.expense.domain.EventId
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.RecordId
import com.arduia.expense.domain.RecordLink
import com.arduia.expense.domain.RecordType
import com.arduia.expense.feature.logging.LogRecordInput
import com.arduia.expense.feature.logging.LoggedExpenseHandoff
import com.arduia.expense.feature.logging.LoggingRepository
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
import org.koin.compose.koinInject
import kotlin.math.roundToLong

interface LoggingFeatureEntry {
    @Composable
    fun QuickLogFlow(
        onDismiss: () -> Unit,
        onSaved: (LoggedExpenseHandoff) -> Unit,
        modifier: Modifier = Modifier,
    )

    @Composable
    fun EditExpenseFlow(
        recordId: String,
        onDismiss: () -> Unit,
        onSaved: () -> Unit,
        modifier: Modifier = Modifier,
    )
}

internal class LoggingFeatureEntryImpl : LoggingFeatureEntry {
    @Composable
    override fun QuickLogFlow(
        onDismiss: () -> Unit,
        onSaved: (LoggedExpenseHandoff) -> Unit,
        modifier: Modifier,
    ) {
        val scope = rememberCoroutineScope()
        val loggingRepository: LoggingRepository = koinInject()
        val eventRepository: EventRepository = koinInject()
        val debtRepository: DebtRepository = koinInject()

        val events by eventRepository.observeAll().collectAsState(emptyList())
        val debts by debtRepository.observeAll().collectAsState(emptyList())

        val tagEvents = events.map { event ->
            TagLinkOption(
                id = event.id.value,
                title = event.name,
                subtitle = shortDateLabel(event.startEpochMillis) + " - " + shortDateLabel(event.endEpochMillis),
                kind = TagLinkKind.Event,
            )
        }

        val tagDebts = debts.map { debt ->
            val direction = if (debt.direction == com.arduia.expense.domain.DebtDirection.OWED_TO_ME) "Lent" else "Owe"
            TagLinkOption(
                id = debt.id.value,
                title = "$direction · ${debt.personName}",
                subtitle = moneyLabel(debt.money.amount.valueInCents),
                kind = TagLinkKind.Debt,
            )
        }

        val onHandoff = onSaved
        com.arduia.expense.feature.logging.ui.QuickLogFlow(
            onDismiss = onDismiss,
            onSaved = { state ->
                scope.launch {
                    val amount = AmountInput.numericValue(state.rawAmount) ?: 0.0
                    val input = LogRecordInput(
                        money = Money(
                            Amount((amount * 100).roundToLong()),
                            CurrencyCode(state.currencyCode),
                        ),
                        homeCurrencyMoney = Money(
                            Amount((amount * 100).roundToLong()),
                            CurrencyCode(state.currencyCode),
                        ),
                        categoryId = CategoryId(state.selectedCategoryId),
                        type = RecordType.EXPENSE,
                        note = state.note.ifBlank { null },
                        recordedAtEpochMillis = state.recordedAtEpochMillis,
                        link = when (state.linkedTagKind) {
                            TagLinkKind.Event -> RecordLink.ToEvent(
                                com.arduia.expense.domain.EventId(state.linkedTagId.orEmpty()),
                            )
                            TagLinkKind.Debt -> RecordLink.ToDebt(
                                com.arduia.expense.domain.DebtId(state.linkedTagId.orEmpty()),
                            )
                            null -> RecordLink.None
                        },
                    )
                    when (loggingRepository.createRecord(input)) {
                        is Result.Success -> onHandoff(state.toHandoff())
                        is Result.Error -> {} // Error silently; UI already has toast handling
                    }
                }
            },
            tagEvents = tagEvents,
            tagDebts = tagDebts,
            modifier = modifier,
        )
    }

    @Composable
    override fun EditExpenseFlow(
        recordId: String,
        onDismiss: () -> Unit,
        onSaved: () -> Unit,
        modifier: Modifier,
    ) {
        val scope = rememberCoroutineScope()
        val financeRecordRepository: FinanceRecordRepository = koinInject()
        val eventRepository: EventRepository = koinInject()
        val debtRepository: DebtRepository = koinInject()

        val events by eventRepository.observeAll().collectAsState(emptyList())
        val debts by debtRepository.observeAll().collectAsState(emptyList())
        val eventNames = events.associate { it.id.value to it.name }
        val debtNames = debts.associate {
            val direction = if (it.direction == com.arduia.expense.domain.DebtDirection.OWED_TO_ME) "Lent" else "Owe"
            it.id.value to "$direction · ${it.personName}"
        }

        val tagEvents = events.map { event ->
            TagLinkOption(
                id = event.id.value,
                title = event.name,
                subtitle = shortDateLabel(event.startEpochMillis) + " - " + shortDateLabel(event.endEpochMillis),
                kind = TagLinkKind.Event,
            )
        }
        val tagDebts = debts.map { debt ->
            val direction = if (debt.direction == com.arduia.expense.domain.DebtDirection.OWED_TO_ME) "Lent" else "Owe"
            TagLinkOption(
                id = debt.id.value,
                title = "$direction · ${debt.personName}",
                subtitle = moneyLabel(debt.money.amount.valueInCents),
                kind = TagLinkKind.Debt,
            )
        }

        var existing by remember(recordId) { mutableStateOf<FinanceRecord?>(null) }
        var startState by remember(recordId) { mutableStateOf<ExpenseEntryState?>(null) }

        LaunchedEffect(recordId, eventNames, debtNames) {
            val result = financeRecordRepository.getById(RecordId(recordId))
            if (result is Result.Success && result.data != null) {
                val record = result.data!!
                existing = record
                startState = record.toEntryState(eventNames, debtNames)
            }
        }

        val loaded = startState
        val record = existing
        if (loaded != null && record != null) {
            com.arduia.expense.feature.logging.ui.QuickLogFlow(
                onDismiss = onDismiss,
                startState = loaded,
                onSaved = { state ->
                    scope.launch {
                        val amount = AmountInput.numericValue(state.rawAmount) ?: 0.0
                        val cents = (amount * 100).roundToLong()
                        val money = Money(Amount(cents), CurrencyCode(state.currencyCode))
                        val updated = record.copy(
                            money = money,
                            homeCurrencyMoney = money,
                            categoryId = CategoryId(state.selectedCategoryId),
                            note = state.note.ifBlank { null },
                            recordedAtEpochMillis = state.recordedAtEpochMillis,
                            link = state.toRecordLink(record.link),
                        )
                        when (financeRecordRepository.upsert(updated)) {
                            is Result.Success -> onSaved()
                            is Result.Error -> {}
                        }
                    }
                },
                tagEvents = tagEvents,
                tagDebts = tagDebts,
                modifier = modifier,
            )
        }
    }
}

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
        rawAmount = String.format(Locale.US, "%.2f", money.amount.valueInCents / 100.0),
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

// Preserves a pre-existing shared-cost link when the user leaves the tag untouched, since the tag
// picker only surfaces events and debts.
private fun ExpenseEntryState.toRecordLink(original: RecordLink): RecordLink = when (linkedTagKind) {
    TagLinkKind.Event -> RecordLink.ToEvent(EventId(linkedTagId.orEmpty()))
    TagLinkKind.Debt -> RecordLink.ToDebt(DebtId(linkedTagId.orEmpty()))
    null -> if (original is RecordLink.ToSharedCost) original else RecordLink.None
}

private fun moneyLabel(valueInCents: Long): String =
    "$" + AmountInput.formatDisplay(String.format(Locale.US, "%.2f", valueInCents / 100.0))
