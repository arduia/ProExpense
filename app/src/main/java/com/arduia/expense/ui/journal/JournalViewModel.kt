package com.arduia.expense.ui.journal

import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.ProfileRepository
import com.arduia.expense.data.getOrNull
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordType
import com.arduia.expense.feature.history.ui.preview.JournalDayUi
import com.arduia.expense.feature.history.ui.preview.JournalDetailUiState
import com.arduia.expense.ui.design.ProTransactionRowModel
import com.arduia.expense.ui.design.expenseCategoryLabel
import com.arduia.expense.ui.format.DateLabels
import com.arduia.expense.ui.format.MoneyFormatter
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
 * feature's day-grouped list + per-record detail, formatted in the profile's home currency. Plain
 * class (scope + clock injected) so it unit-tests against repository fakes with no Android runtime.
 */
class JournalViewModel(
    private val financeRepository: FinanceRecordRepository,
    private val profileRepository: ProfileRepository,
    private val scope: CoroutineScope,
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
            financeRepository.delete(id)
            reload()
        }
    }

    private suspend fun reload() {
        val currency = profileRepository.getProfile().getOrNull()?.homeCurrency?.code ?: "USD"
        val expenses = financeRepository.getAll().getOrNull().orEmpty()
            .filter { it.type == RecordType.EXPENSE }
            .sortedByDescending { it.recordedAtEpochMillis }
        val now = clock()

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
                    rows = items.map { it.toRow(currency) },
                )
            }

        val details = expenses.associate { it.id to it.toDetail(currency) }

        _state.value = JournalScreenState(days = days, details = details)
    }

    private fun FinanceRecord.toRow(currency: String): ProTransactionRowModel {
        val label = expenseCategoryLabel(categoryId)
        return ProTransactionRowModel(
            id = id,
            categoryId = categoryId,
            note = note?.takeIf { it.isNotBlank() } ?: label,
            meta = "$label · ${DateLabels.timeLabel(recordedAtEpochMillis)}",
            amount = MoneyFormatter.format(homeCurrencyAmount.valueInCents, currency),
            tag = null,
        )
    }

    private fun FinanceRecord.toDetail(currency: String): JournalDetailUiState {
        val label = expenseCategoryLabel(categoryId)
        return JournalDetailUiState(
            id = id,
            categoryId = categoryId,
            categoryLabel = label.uppercase(),
            amountLabel = MoneyFormatter.format(homeCurrencyAmount.valueInCents, currency),
            dateTimeLabel = DateLabels.detailDateTime(recordedAtEpochMillis),
            note = note.orEmpty(),
            linkedTag = null,
        )
    }
}
