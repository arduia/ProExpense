package com.arduia.expense.ui.debt

import com.arduia.expense.R
import com.arduia.expense.data.DebtRepository
import com.arduia.expense.data.ProfileRepository
import com.arduia.expense.data.getOrNull
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.DebtDirection
import com.arduia.expense.feature.debt.ui.preview.DebtDetailUiState
import com.arduia.expense.feature.debt.ui.preview.DebtListUiState
import com.arduia.expense.feature.debt.ui.preview.DebtRecordUi
import com.arduia.expense.feature.debt.ui.preview.DebtSide
import com.arduia.expense.ui.UiMessageBus
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.format.DateLabels
import com.arduia.expense.ui.format.MoneyFormatter
import com.arduia.expense.ui.orPost
import com.arduia.expense.ui.postIfError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToLong

data class DebtScreenState(
    val lent: DebtListUiState = emptyDebtList(DebtSide.Lent),
    val owe: DebtListUiState = emptyDebtList(DebtSide.Owe),
    val details: Map<String, DebtDetailUiState> = emptyMap(),
)

private fun emptyDebtList(side: DebtSide) = DebtListUiState(
    side = side,
    netLabel = "",
    activeCount = 0,
    active = emptyList(),
    settled = emptyList(),
)

/**
 * Read/write model for the Debt tracker. Splits debts into the Lent (owed to me) and Owe (I owe)
 * sides, derives each side's net + active/settled lists, and exposes per-record detail. Create,
 * mark-settled and delete persist through the repository. Debts carry no recorded-at timestamp, so
 * dates surface the due date where present.
 */
class DebtViewModel(
    private val debtRepository: DebtRepository,
    private val profileRepository: ProfileRepository,
    private val scope: CoroutineScope,
    private val uiMessages: UiMessageBus = UiMessageBus(),
    private val idGenerator: () -> String = { java.util.UUID.randomUUID().toString() },
) {
    private val _state = MutableStateFlow(DebtScreenState())
    val state: StateFlow<DebtScreenState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        scope.launch { reload() }
    }

    fun create(side: DebtSide, person: String, amountRaw: String) {
        scope.launch {
            val cents = (AmountInput.numericValue(amountRaw) ?: 0.0)
                .times(100).roundToLong().coerceIn(0L, Amount.MAX_VALUE_IN_CENTS)
            debtRepository.upsert(
                Debt(
                    id = idGenerator(),
                    personName = person.trim(),
                    amount = Amount(cents),
                    direction = side.toDirection(),
                    dueEpochMillis = null,
                    isSettled = false,
                ),
            ).postIfError(uiMessages, R.string.data_load_error)
            reload()
        }
    }

    fun markSettled(id: String) {
        scope.launch {
            val debt = debtRepository.getById(id).getOrNull() ?: return@launch
            debtRepository.upsert(debt.copy(isSettled = true)).postIfError(uiMessages, R.string.data_load_error)
            reload()
        }
    }

    fun delete(id: String) {
        scope.launch {
            debtRepository.delete(id).postIfError(uiMessages, R.string.expense_delete_error)
            reload()
        }
    }

    private suspend fun reload() {
        val currency = profileRepository.getProfile().getOrNull()?.homeCurrency?.code ?: "USD"
        val debts = debtRepository.getAll().orPost(uiMessages, R.string.data_load_error, emptyList())

        _state.value = DebtScreenState(
            lent = debts.filter { it.direction == DebtDirection.OWED_TO_ME }.toListState(DebtSide.Lent, currency),
            owe = debts.filter { it.direction == DebtDirection.I_OWE }.toListState(DebtSide.Owe, currency),
            details = debts.associate { it.id to it.toDetail(currency) },
        )
    }

    private fun List<Debt>.toListState(side: DebtSide, currency: String): DebtListUiState {
        val active = filter { !it.isSettled }
        val settled = filter { it.isSettled }
        val netCents = active.sumOf { it.amount.valueInCents }
        return DebtListUiState(
            side = side,
            netLabel = MoneyFormatter.format(netCents, currency),
            activeCount = active.size,
            active = active.map { it.toRow(currency) },
            settled = settled.map { it.toRow(currency) },
        )
    }

    private fun Debt.toRow(currency: String): DebtRecordUi = DebtRecordUi(
        id = id,
        name = personName,
        dateLabel = dueEpochMillis?.let { DateLabels.shortDate(it) } ?: "—",
        amountLabel = MoneyFormatter.format(amount.valueInCents, currency),
        subtitle = dueEpochMillis?.let { "due ${DateLabels.shortDate(it)}" },
        settled = isSettled,
    )

    private fun Debt.toDetail(currency: String): DebtDetailUiState = DebtDetailUiState(
        id = id,
        side = direction.toSide(),
        name = personName,
        amountLabel = MoneyFormatter.format(amount.valueInCents, currency),
        dateRecordedLabel = "—",
        dueLabel = dueEpochMillis?.let { DateLabels.fullDate(it) } ?: "No due date",
        statusLabel = if (isSettled) "Settled" else "Active",
        settled = isSettled,
        note = null,
        linkedExpense = null,
    )

    private fun DebtSide.toDirection(): DebtDirection = when (this) {
        DebtSide.Lent -> DebtDirection.OWED_TO_ME
        DebtSide.Owe -> DebtDirection.I_OWE
    }

    private fun DebtDirection.toSide(): DebtSide = when (this) {
        DebtDirection.OWED_TO_ME -> DebtSide.Lent
        DebtDirection.I_OWE -> DebtSide.Owe
    }
}
