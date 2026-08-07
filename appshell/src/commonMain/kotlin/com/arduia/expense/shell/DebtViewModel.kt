package com.arduia.expense.shell

import com.arduia.expense.data.CurrencySettingsRepository
import com.arduia.expense.data.DebtRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.DebtDirection
import com.arduia.expense.feature.debt.AggregateDebtsUseCase
import com.arduia.expense.feature.debt.CheckDebtConflictUseCase
import com.arduia.expense.feature.debt.CreateDebtUseCase
import com.arduia.expense.feature.debt.DeleteDebtUseCase
import com.arduia.expense.feature.debt.SettleDebtUseCase
import com.arduia.expense.shared.StatefulViewModel
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.PlatformDateFormatter
import com.arduia.expense.ui.design.currencySymbol
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class DebtRow(
    val debtId: String,
    val personName: String,
    val amount: String,
    val dueLabel: String?,
    val note: String?,
    val isSettled: Boolean,
)

data class DebtUiState(
    val direction: DebtDirection = DebtDirection.OWED_TO_ME,
    val netLabel: String = "",
    val active: List<DebtRow> = emptyList(),
    val settled: List<DebtRow> = emptyList(),
    val conflictWarning: Boolean = false,
    val isLoading: Boolean = true,
) {
    val isEmpty: Boolean get() = !isLoading && active.isEmpty() && settled.isEmpty()

    /** True when viewing money the user lent out (owed *to* them). */
    val isLentTab: Boolean get() = direction == DebtDirection.OWED_TO_ME
}

/** The debt use cases, bundled so the ViewModel's constructor stays readable. */
data class DebtActions(
    val aggregate: AggregateDebtsUseCase,
    val create: CreateDebtUseCase,
    val settle: SettleDebtUseCase,
    val delete: DeleteDebtUseCase,
    val checkConflict: CheckDebtConflictUseCase,
)

/**
 * 09 · Debt Tracker — lent/owed ledger with settle and delete.
 *
 * The net total and the active/settled split come from [AggregateDebtsUseCase] so both platforms
 * bucket identically; [CheckDebtConflictUseCase] surfaces the US-DEBT-4 opposite-direction warning
 * before a create is committed.
 */
class DebtViewModel(
    private val debtRepository: DebtRepository,
    private val currencySettingsRepository: CurrencySettingsRepository,
    private val actions: DebtActions,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : StatefulViewModel<DebtUiState>(DebtUiState(), dispatcher) {
    private var debts: List<Debt> = emptyList()
    private var symbol: String = "$"

    init {
        viewModelScope.launch {
            val code = (currencySettingsRepository.getHomeCurrency() as? Result.Success)?.data?.code
            symbol = currencySymbol(code ?: "USD")
            debtRepository.observeAll().collect { all ->
                debts = all
                project()
            }
        }
    }

    fun onDirectionChange(direction: DebtDirection) {
        setState { it.copy(direction = direction) }
        project()
    }

    /** Call before [create] so the view can warn about an opposite-side debt for the same person. */
    suspend fun checkConflict(personName: String) {
        val conflict = actions.checkConflict(personName.trim(), currentState().direction)
        setState { it.copy(conflictWarning = conflict) }
    }

    fun clearConflictWarning() {
        setState { it.copy(conflictWarning = false) }
    }

    suspend fun create(
        personName: String,
        rawAmount: String,
        dueEpochMillis: Long?,
        note: String?,
        recordAsTransaction: Boolean,
    ): Boolean {
        val code = (currencySettingsRepository.getHomeCurrency() as? Result.Success)?.data?.code ?: "USD"
        return actions.create(
            personName = personName.trim(),
            rawAmount = rawAmount,
            direction = currentState().direction,
            currencyCode = code,
            dueEpochMillis = dueEpochMillis,
            note = note,
            recordAsTransaction = recordAsTransaction,
        )
    }

    suspend fun settle(debtId: String) {
        debts.firstOrNull { it.id.value == debtId }?.let { actions.settle(it) }
    }

    suspend fun delete(debtId: String) {
        actions.delete(debtId)
    }

    private fun project() {
        val aggregate = actions.aggregate(debts, currentState().direction)
        setState {
            it.copy(
                netLabel = AmountInput.formatMoney(aggregate.netCents, symbol),
                active = aggregate.active.map { debt -> debt.toRow() },
                settled = aggregate.settled.map { debt -> debt.toRow() },
                isLoading = false,
            )
        }
    }

    private fun Debt.toRow(): DebtRow =
        DebtRow(
            debtId = id.value,
            personName = personName,
            amount = AmountInput.formatMoney(money.amount.valueInCents, symbol),
            dueLabel = dueEpochMillis?.let { PlatformDateFormatter.shortDateLabel(it) },
            note = note,
            isSettled = isSettled,
        )
}
