package com.arduia.expense.feature.sharedcost

import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Participant
import com.arduia.expense.domain.formatWithSymbol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class SharedCostInputUiState(
    val amountText: String = "",
    val peopleCount: Int = 2,
    val showZeroValidation: Boolean = false,
)

data class SharedSummaryUiState(
    val people: List<Pair<String, String>> = emptyList(),
    val history: List<SharedHistoryItemState> = emptyList(),
)

data class SharedHistoryItemState(
    val title: String,
    val dateLabel: String,
    val total: String,
    val peopleCount: Int,
)

class SharedCostInputViewModel(
    private val sharedCostRepository: SharedCostRepository,
    private val homeCurrencyCode: String,
    private val nowEpochMillis: () -> Long,
    private val scope: CoroutineScope,
    private val onCalculated: (String) -> Unit,
) {
    private val _uiState = MutableStateFlow(SharedCostInputUiState())
    val uiState: StateFlow<SharedCostInputUiState> = _uiState.asStateFlow()

    fun onAmountChange(text: String) {
        _uiState.update { it.copy(amountText = text, showZeroValidation = false) }
    }

    fun incrementPeople() {
        _uiState.update { it.copy(peopleCount = (it.peopleCount + 1).coerceAtMost(20)) }
    }

    fun decrementPeople() {
        _uiState.update { it.copy(peopleCount = (it.peopleCount - 1).coerceAtLeast(1)) }
    }

    fun onCalculate() {
        val state = _uiState.value
        val total = state.amountText.toDoubleOrNull() ?: 0.0
        if (total <= 0.0) {
            _uiState.update { it.copy(showZeroValidation = true) }
            return
        }
        scope.launch {
            val cents = (total * 100).toLong()
            val participants = List(state.peopleCount) { index ->
                Participant(id = "p$index", name = "Person ${index + 1}")
            }
            val input = SharedCostInput(
                title = "Shared cost",
                totalAmount = Amount(cents),
                currency = CurrencyCode(homeCurrencyCode),
                participants = participants,
                recordedAtEpochMillis = nowEpochMillis(),
            )
            when (val result = sharedCostRepository.create(input)) {
                is Result.Success -> onCalculated(result.data.id)
                is Result.Error -> Unit
            }
        }
    }
}

class SharedSummaryViewModel(
    private val sharedCostRepository: SharedCostRepository,
    private val sharedCostId: String,
    private val homeCurrencyCode: String,
    private val scope: CoroutineScope,
) {
    private val _uiState = MutableStateFlow(SharedSummaryUiState())
    val uiState: StateFlow<SharedSummaryUiState> = _uiState.asStateFlow()

    init {
        scope.launch {
            when (val settlement = sharedCostRepository.getSettlement(sharedCostId)) {
                is Result.Success -> {
                    val people = settlement.data.lines.map { line ->
                        line.participant.name to line.owedAmount.formatWithSymbol(homeCurrencyCode)
                    }
                    _uiState.update { it.copy(people = people) }
                }
                is Result.Error -> Unit
            }
            when (val all = sharedCostRepository.getAll()) {
                is Result.Success -> {
                    val history = all.data.map { cost ->
                        SharedHistoryItemState(
                            title = cost.title,
                            dateLabel = "Recent",
                            total = cost.totalAmount.formatWithSymbol(homeCurrencyCode),
                            peopleCount = cost.participants.size,
                        )
                    }
                    _uiState.update { it.copy(history = history) }
                }
                is Result.Error -> Unit
            }
        }
    }
}
