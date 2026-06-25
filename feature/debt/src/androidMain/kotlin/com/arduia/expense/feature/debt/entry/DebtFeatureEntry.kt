package com.arduia.expense.feature.debt.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.arduia.expense.data.DebtRepository
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.DebtDirection
import com.arduia.expense.domain.DebtId
import com.arduia.expense.domain.Money
import com.arduia.expense.feature.debt.ui.DebtFlow
import com.arduia.expense.feature.debt.ui.preview.DebtListUiState
import com.arduia.expense.feature.debt.ui.preview.DebtRecordUi
import com.arduia.expense.feature.debt.ui.preview.DebtSide
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.shortDateLabel
import java.util.Locale
import kotlin.math.roundToLong
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

        val debts by debtRepository.observeAll().collectAsState(emptyList())

        val lentState = debts.toListUiState(DebtSide.Lent, DebtDirection.OWED_TO_ME)
        val oweState = debts.toListUiState(DebtSide.Owe, DebtDirection.I_OWE)

        DebtFlow(
            onDismiss = onDismiss,
            lentState = lentState,
            oweState = oweState,
            onSaveRecord = { side, person, amountRaw ->
                scope.launch {
                    val amountValue = AmountInput.numericValue(amountRaw) ?: 0.0
                    val debt = Debt(
                        id = DebtId(newDebtId(person)),
                        personName = person,
                        money = Money(Amount((amountValue * 100).roundToLong()), CurrencyCode("USD")),
                        direction = if (side == DebtSide.Lent) DebtDirection.OWED_TO_ME else DebtDirection.I_OWE,
                    )
                    debtRepository.upsert(debt)
                }
            },
            onDeleteRecord = { id ->
                scope.launch { debtRepository.delete(DebtId(id)) }
            },
            onSettleRecord = { id ->
                val debt = debts.firstOrNull { it.id.value == id }
                if (debt != null) {
                    scope.launch { debtRepository.upsert(debt.copy(isSettled = true)) }
                }
            },
            modifier = modifier,
        )
    }
}

object DebtFeatureUi : DebtFeatureEntry by DebtFeatureEntryImpl()

private fun newDebtId(person: String): String =
    person.trim().lowercase(Locale.US).replace(" ", "-") + "-" + System.currentTimeMillis()

private fun List<Debt>.toListUiState(side: DebtSide, direction: DebtDirection): DebtListUiState {
    val matching = filter { it.direction == direction }
    val active = matching.filter { !it.isSettled }
    val settled = matching.filter { it.isSettled }
    val netCents = active.sumOf { it.money.amount.valueInCents }
    return DebtListUiState(
        side = side,
        netLabel = moneyLabel(netCents),
        activeCount = active.size,
        active = active.map { it.toRecordUi(settled = false) },
        settled = settled.map { it.toRecordUi(settled = true) },
    )
}

private fun Debt.toRecordUi(settled: Boolean): DebtRecordUi = DebtRecordUi(
    id = id.value,
    name = personName,
    dateLabel = dueEpochMillis?.let { shortDateLabel(it) } ?: "No due date",
    amountLabel = moneyLabel(money.amount.valueInCents),
    settled = settled,
)

private fun moneyLabel(valueInCents: Long): String =
    "$" + AmountInput.formatDisplay(String.format(Locale.US, "%.2f", valueInCents / 100.0))
