package com.arduia.expense.ui.journal

import com.arduia.expense.R
import com.arduia.expense.data.DebtRepository
import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.ProfileRepository
import com.arduia.expense.data.getOrNull
import com.arduia.expense.domain.ExpenseTagType
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordType
import com.arduia.expense.feature.history.ui.preview.JournalDayUi
import com.arduia.expense.feature.history.ui.preview.JournalDetailUiState
import com.arduia.expense.feature.history.ui.preview.JournalLinkedTagUi
import com.arduia.expense.ui.UiMessageBus
import com.arduia.expense.ui.design.ProTransactionRowModel
import com.arduia.expense.ui.design.expenseCategoryLabel
import com.arduia.expense.ui.format.DateLabels
import com.arduia.expense.ui.format.MoneyFormatter
import com.arduia.expense.ui.orPost
import com.arduia.expense.ui.postIfError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class JournalScreenState(
    val days: List<JournalDayUi> = emptyList(),
    val details: Map<String, JournalDetailUiState> = emptyMap(),
)

/**
 * Read-model for the Journal (Record History) tab. Projects the encrypted [FinanceRecord]s into the
 * feature's day-grouped list + per-record detail, formatted in the profile's home currency, and
 * resolves each record's linked event/debt tag for the detail card. Plain class (scope + clock
 * injected) so it unit-tests against repository fakes with no Android runtime.
 */
class JournalViewModel(
    private val financeRepository: FinanceRecordRepository,
    private val profileRepository: ProfileRepository,
    private val eventRepository: EventRepository,
    private val debtRepository: DebtRepository,
    private val scope: CoroutineScope,
    private val uiMessages: UiMessageBus = UiMessageBus(),
    private val clock: () -> Long = { System.currentTimeMillis() },
) {
    private val _state = MutableStateFlow(JournalScreenState())
    val state: StateFlow<JournalScreenState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        scope.launch { reload() }
    }

    fun delete(id: String) {
        scope.launch {
            if (financeRepository.delete(id).postIfError(uiMessages, R.string.expense_delete_error)) {
                reload()
            }
        }
    }

    private suspend fun reload() {
        val currency = profileRepository.getProfile().getOrNull()?.homeCurrency?.code ?: "USD"
        val expenses = financeRepository.getAll()
            .orPost(uiMessages, R.string.data_load_error, emptyList())
            .filter { it.type == RecordType.EXPENSE }
            .sortedByDescending { it.recordedAtEpochMillis }
        val now = clock()
        val tags = resolveTags(expenses)

        val days = expenses
            .groupBy { DateLabels.startOfDay(it.recordedAtEpochMillis) }
            .toSortedMap(compareByDescending { it })
            .map { (dayMillis, items) ->
                JournalDayUi(
                    id = dayMillis.toString(),
                    title = DateLabels.dayGroupTitle(dayMillis, now),
                    total = MoneyFormatter.format(
                        items.sumOf { it.homeCurrencyAmount.valueInCents },
                        currency,
                    ),
                    rows = items.map { it.toRow(currency, tags) },
                )
            }

        val details = expenses.associate { it.id to it.toDetail(currency, tags) }

        _state.value = JournalScreenState(days = days, details = details)
    }

    /** Resolves the linked-tag label/meta for every tagged record, batched per tag type. */
    private suspend fun resolveTags(expenses: List<FinanceRecord>): Map<String, JournalLinkedTagUi> {
        val tagged = expenses.filter { it.tagType != null && it.tagId != null }
        if (tagged.isEmpty()) return emptyMap()

        val result = mutableMapOf<String, JournalLinkedTagUi>()

        if (tagged.any { it.tagType == ExpenseTagType.EVENT }) {
            val events = eventRepository.getAll().getOrNull().orEmpty().associateBy { it.id }
            tagged.filter { it.tagType == ExpenseTagType.EVENT }.forEach { record ->
                events[record.tagId]?.let { event ->
                    result[record.id] = JournalLinkedTagUi(
                        title = event.name,
                        meta = "Event · ${DateLabels.eventRange(event.startEpochMillis, event.endEpochMillis)}",
                    )
                }
            }
        }

        if (tagged.any { it.tagType == ExpenseTagType.DEBT }) {
            val debts = debtRepository.getAll().getOrNull().orEmpty().associateBy { it.id }
            tagged.filter { it.tagType == ExpenseTagType.DEBT }.forEach { record ->
                debts[record.tagId]?.let { debt ->
                    result[record.id] = JournalLinkedTagUi(title = debt.personName, meta = "Debt")
                }
            }
        }

        return result
    }

    private fun FinanceRecord.toRow(currency: String, tags: Map<String, JournalLinkedTagUi>): ProTransactionRowModel {
        val label = expenseCategoryLabel(categoryId)
        return ProTransactionRowModel(
            id = id,
            categoryId = categoryId,
            note = note?.takeIf { it.isNotBlank() } ?: label,
            meta = "$label · ${DateLabels.timeLabel(recordedAtEpochMillis)}",
            amount = MoneyFormatter.format(homeCurrencyAmount.valueInCents, currency),
            tag = tags[id]?.title,
        )
    }

    private fun FinanceRecord.toDetail(currency: String, tags: Map<String, JournalLinkedTagUi>): JournalDetailUiState {
        val label = expenseCategoryLabel(categoryId)
        return JournalDetailUiState(
            id = id,
            categoryId = categoryId,
            categoryLabel = label.uppercase(),
            amountLabel = MoneyFormatter.format(homeCurrencyAmount.valueInCents, currency),
            dateTimeLabel = DateLabels.detailDateTime(recordedAtEpochMillis),
            note = note.orEmpty(),
            linkedTag = tags[id],
        )
    }
}
