package com.arduia.expense.feature.sharedcost

import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Participant
import com.arduia.expense.domain.formatWithSymbol
import com.arduia.expense.domain.parseAmountToCents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SharedCostInputUiState(
    val amountText: String = "",
    val peopleCount: Int = 2,
    val splitMode: SplitMode = SplitMode.EVEN,
    val customAmountTexts: List<String> = emptyList(),
    val customAmountError: String? = null,
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
    private val _uiState = MutableStateFlow(SharedCostInputUiState(customAmountTexts = listOf("", "")))
    val uiState: StateFlow<SharedCostInputUiState> = _uiState.asStateFlow()

    fun onAmountChange(text: String) {
        _uiState.update { it.copy(amountText = text, showZeroValidation = false, customAmountError = null) }
    }

    fun incrementPeople() {
        _uiState.update { state ->
            val count = (state.peopleCount + 1).coerceAtMost(20)
            state.copy(
                peopleCount = count,
                customAmountTexts = resizeCustomTexts(state.customAmountTexts, count),
            )
        }
    }

    fun decrementPeople() {
        _uiState.update { state ->
            val count = (state.peopleCount - 1).coerceAtLeast(1)
            state.copy(
                peopleCount = count,
                customAmountTexts = resizeCustomTexts(state.customAmountTexts, count),
            )
        }
    }

    fun onSplitModeChanged(mode: SplitMode) {
        val state = _uiState.value
        val totalCents = parseAmountToCents(state.amountText) ?: 0L
        val prefilled = if (mode == SplitMode.CUSTOM && totalCents > 0) {
            calculateEvenSplit(totalCents, state.peopleCount).map { cents ->
                (cents / 100.0).toString()
            }
        } else {
            state.customAmountTexts
        }
        _uiState.update {
            it.copy(splitMode = mode, customAmountTexts = prefilled, customAmountError = null)
        }
    }

    fun onCustomAmountChange(index: Int, value: String) {
        _uiState.update { state ->
            val updated = state.customAmountTexts.toMutableList()
            if (index in updated.indices) {
                updated[index] = value
            }
            state.copy(customAmountTexts = updated, customAmountError = null)
        }
    }

    fun onCalculate() {
        val state = _uiState.value
        val cents = parseAmountToCents(state.amountText) ?: 0L
        if (cents <= 0L) {
            _uiState.update { it.copy(showZeroValidation = true) }
            return
        }
        scope.launch {
            val participants = List(state.peopleCount) { index ->
                Participant(id = "p$index", name = "Person ${index + 1}")
            }
            val customShares = if (state.splitMode == SplitMode.CUSTOM) {
                val shares = state.customAmountTexts.map { text ->
                    parseAmountToCents(text) ?: 0L
                }
                val error = validateCustomSplit(cents, shares)
                if (error != null) {
                    _uiState.update { it.copy(customAmountError = error) }
                    return@launch
                }
                shares
            } else {
                null
            }
            val input = SharedCostInput(
                title = "Shared cost",
                totalAmount = Amount(cents),
                currency = CurrencyCode(homeCurrencyCode),
                participants = participants,
                recordedAtEpochMillis = nowEpochMillis(),
                splitMode = state.splitMode,
                customShareCents = customShares,
            )
            when (val result = sharedCostRepository.create(input)) {
                is Result.Success -> onCalculated(result.data.id)
                is Result.Error -> Unit
            }
        }
    }

    private fun resizeCustomTexts(current: List<String>, count: Int): List<String> {
        if (current.size == count) return current
        return List(count) { index -> current.getOrNull(index).orEmpty() }
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
