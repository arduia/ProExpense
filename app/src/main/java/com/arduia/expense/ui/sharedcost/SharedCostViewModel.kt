package com.arduia.expense.ui.sharedcost

import com.arduia.expense.data.ProfileRepository
import com.arduia.expense.data.SharedCostRepository
import com.arduia.expense.data.getOrNull
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Participant
import com.arduia.expense.domain.SharedCost
import com.arduia.expense.R
import com.arduia.expense.feature.sharedcost.ui.preview.SharedCostHistoryItemUi
import com.arduia.expense.ui.UiMessageBus
import com.arduia.expense.ui.format.DateLabels
import com.arduia.expense.ui.format.MoneyFormatter
import com.arduia.expense.ui.orPost
import com.arduia.expense.ui.postIfError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SharedCostScreenState(
    val history: List<SharedCostHistoryItemUi> = emptyList(),
)

/**
 * Read/write model for Shared Costs: lists past splits (most recent first) as history items and
 * persists a new split. Each split records the participants and (for uneven splits) their custom
 * cent shares; the home currency formats the labels.
 */
class SharedCostViewModel(
    private val sharedCostRepository: SharedCostRepository,
    private val profileRepository: ProfileRepository,
    private val scope: CoroutineScope,
    private val uiMessages: UiMessageBus = UiMessageBus(),
    private val clock: () -> Long = { System.currentTimeMillis() },
    private val idGenerator: () -> String = { java.util.UUID.randomUUID().toString() },
) {
    private val _state = MutableStateFlow(SharedCostScreenState())
    val state: StateFlow<SharedCostScreenState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        scope.launch { reload() }
    }

    fun create(
        title: String,
        totalCents: Long,
        names: List<String>,
        customShareCents: List<Long>?,
    ) {
        scope.launch {
            val currency = currencyCode()
            sharedCostRepository.upsert(
                SharedCost(
                    id = idGenerator(),
                    title = title.trim(),
                    totalAmount = Amount(totalCents.coerceIn(0L, Amount.MAX_VALUE_IN_CENTS)),
                    currency = CurrencyCode(currency),
                    participants = names.map { Participant(id = idGenerator(), name = it) },
                    recordedAtEpochMillis = clock(),
                    customShareCents = customShareCents,
                ),
            ).postIfError(uiMessages, R.string.data_load_error)
            reload()
        }
    }

    private suspend fun reload() {
        val currency = currencyCode()
        val items = sharedCostRepository.getAll().orPost(uiMessages, R.string.data_load_error, emptyList())
            .map { it.toHistoryItem(currency) }
        _state.value = SharedCostScreenState(history = items)
    }

    private suspend fun currencyCode(): String =
        profileRepository.getProfile().getOrNull()?.homeCurrency?.code ?: "USD"

    private fun SharedCost.toHistoryItem(currency: String): SharedCostHistoryItemUi {
        val count = participants.size.coerceAtLeast(1)
        return SharedCostHistoryItemUi(
            id = id,
            title = title,
            peopleCount = participants.size,
            perPersonLabel = MoneyFormatter.format(totalAmount.valueInCents / count, currency),
            dateLabel = DateLabels.shortDate(recordedAtEpochMillis),
            totalLabel = MoneyFormatter.format(totalAmount.valueInCents, currency),
        )
    }
}
