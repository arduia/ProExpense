package com.arduia.expense.feature.debt.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.arduia.expense.data.DebtRepository
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.DebtDirection
import com.arduia.expense.domain.DebtId
import com.arduia.expense.feature.debt.AggregateDebtsUseCase
import com.arduia.expense.feature.debt.CreateDebtUseCase
import com.arduia.expense.feature.debt.DebtAggregate
import com.arduia.expense.feature.debt.DeleteDebtUseCase
import com.arduia.expense.feature.debt.SettleDebtUseCase
import com.arduia.expense.feature.debt.ui.DebtFlow
import com.arduia.expense.feature.debt.ui.preview.DebtListUiState
import com.arduia.expense.feature.debt.ui.preview.DebtRecordUi
import com.arduia.expense.feature.debt.ui.preview.DebtSide
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.shortDateLabel
import java.util.Locale
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

interface DebtFeatureEntry {
    @Composable
    fun DebtOverlay(
        onDismiss: () -> Unit,
        modifier: Modifier = Modifier,
    )
}

internal class DebtFeatureEntryImpl : DebtFeatureEntry {
    @Composable
    override fun DebtOverlay(onDismiss: () -> Unit, modifier: Modifier) {
        val scope = rememberCoroutineScope()
        val debtRepository: DebtRepository = koinInject()
        val aggregateDebts: AggregateDebtsUseCase = koinInject()
        val createDebt: CreateDebtUseCase = koinInject()
        val deleteDebt: DeleteDebtUseCase = koinInject()
        val settleDebt: SettleDebtUseCase = koinInject()

        val debts by debtRepository.observeAll().collectAsState(emptyList())

        val lentState = aggregateDebts(debts, DebtDirection.OWED_TO_ME).toUiState(DebtSide.Lent)
        val oweState = aggregateDebts(debts, DebtDirection.I_OWE).toUiState(DebtSide.Owe)

        DebtFlow(
            onDismiss = onDismiss,
            lentState = lentState,
            oweState = oweState,
            onSaveRecord = { side, person, amountRaw ->
                val direction = if (side == DebtSide.Lent) DebtDirection.OWED_TO_ME else DebtDirection.I_OWE
                scope.launch { createDebt(person, amountRaw, direction) }
            },
            onDeleteRecord = { id ->
                scope.launch { deleteDebt(id) }
            },
            onSettleRecord = { id ->
                val debt = debts.firstOrNull { it.id.value == id }
                if (debt != null) {
                    scope.launch { settleDebt(debt) }
                }
            },
            modifier = modifier,
        )
    }
}

object DebtFeatureUi : DebtFeatureEntry by DebtFeatureEntryImpl()

private fun DebtAggregate.toUiState(side: DebtSide): DebtListUiState = DebtListUiState(
    side = side,
    netLabel = moneyLabel(netCents),
    activeCount = active.size,
    active = active.map { it.toRecordUi(settled = false) },
    settled = settled.map { it.toRecordUi(settled = true) },
)

private fun Debt.toRecordUi(settled: Boolean): DebtRecordUi = DebtRecordUi(
    id = id.value,
    name = personName,
    dateLabel = dueEpochMillis?.let { shortDateLabel(it) } ?: "No due date",
    amountLabel = moneyLabel(money.amount.valueInCents),
    settled = settled,
)

private fun moneyLabel(valueInCents: Long): String =
    "$" + AmountInput.formatDisplay(String.format(Locale.US, "%.2f", valueInCents / 100.0))
