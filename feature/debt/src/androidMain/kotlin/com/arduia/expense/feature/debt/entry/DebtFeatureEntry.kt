package com.arduia.expense.feature.debt.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.arduia.expense.data.DebtRepository
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.DebtDirection
import com.arduia.expense.feature.debt.AggregateDebtsUseCase
import com.arduia.expense.feature.debt.CheckDebtConflictUseCase
import com.arduia.expense.feature.debt.CreateDebtUseCase
import com.arduia.expense.feature.debt.DebtAggregate
import com.arduia.expense.feature.debt.DeleteDebtUseCase
import com.arduia.expense.feature.debt.SettleDebtUseCase
import com.arduia.expense.feature.debt.UpdateDebtUseCase
import com.arduia.expense.feature.debt.ui.DebtFlow
import com.arduia.expense.feature.debt.ui.preview.DebtListUiState
import com.arduia.expense.feature.debt.ui.preview.DebtRecordUi
import com.arduia.expense.feature.debt.ui.preview.DebtSide
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.PlatformDateFormatter
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

interface DebtFeatureEntry {
    @Composable
    fun DebtOverlay(
        onDismiss: () -> Unit,
        modifier: Modifier = Modifier,
        homeCurrencySymbol: String = "$",
        initialSelectedRecordId: String? = null,
        deepLinkBackLabel: String? = null,
        onDeepLinkBack: (() -> Unit)? = null,
    )
}

internal class DebtFeatureEntryImpl : DebtFeatureEntry {
    @Composable
    override fun DebtOverlay(
        onDismiss: () -> Unit,
        modifier: Modifier,
        homeCurrencySymbol: String,
        initialSelectedRecordId: String?,
        deepLinkBackLabel: String?,
        onDeepLinkBack: (() -> Unit)?,
    ) {
        val scope = rememberCoroutineScope()
        val debtRepository: DebtRepository = koinInject()
        val aggregateDebts: AggregateDebtsUseCase = koinInject()
        val createDebt: CreateDebtUseCase = koinInject()
        val updateDebt: UpdateDebtUseCase = koinInject()
        val deleteDebt: DeleteDebtUseCase = koinInject()
        val settleDebt: SettleDebtUseCase = koinInject()
        val checkDebtConflict: CheckDebtConflictUseCase = koinInject()

        // null (not emptyList()) until the first Flow emission arrives, so the summary card
        // doesn't flash "$0 · 0 active" before real data has had a chance to load.
        val debtsOrNull by debtRepository.observeAll().collectAsState(initial = null)
        val isLoading = debtsOrNull == null
        val debts = debtsOrNull.orEmpty()

        val lentState = aggregateDebts(debts, DebtDirection.OWED_TO_ME).toUiState(DebtSide.Lent, homeCurrencySymbol, isLoading)
        val oweState = aggregateDebts(debts, DebtDirection.I_OWE).toUiState(DebtSide.Owe, homeCurrencySymbol, isLoading)

        DebtFlow(
            onDismiss = onDismiss,
            lentState = lentState,
            oweState = oweState,
            onSaveRecord = { side, person, amountRaw, dueEpochMillis, note, recordAsTransaction ->
                val direction = if (side == DebtSide.Lent) DebtDirection.OWED_TO_ME else DebtDirection.I_OWE
                scope.launch {
                    createDebt(
                        person,
                        amountRaw,
                        direction,
                        dueEpochMillis = dueEpochMillis,
                        note = note,
                        recordAsTransaction = recordAsTransaction,
                    )
                }
            },
            onUpdateRecord = { id, person, amountRaw, dueEpochMillis, note, recordAsTransaction ->
                val debt = debts.firstOrNull { it.id.value == id }
                if (debt != null) {
                    scope.launch { updateDebt(debt, person, amountRaw, dueEpochMillis, note, recordAsTransaction) }
                }
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
            onCheckConflict = { person, side ->
                val direction = if (side == DebtSide.Lent) DebtDirection.OWED_TO_ME else DebtDirection.I_OWE
                checkDebtConflict(person, direction)
            },
            initialSelectedRecordId = initialSelectedRecordId,
            deepLinkBackLabel = deepLinkBackLabel,
            onDeepLinkBack = onDeepLinkBack,
            modifier = modifier,
        )
    }
}

object DebtFeatureUi : DebtFeatureEntry by DebtFeatureEntryImpl()

private fun DebtAggregate.toUiState(
    side: DebtSide,
    currencySymbol: String,
    isLoading: Boolean,
): DebtListUiState =
    DebtListUiState(
        side = side,
        netLabel = AmountInput.formatMoney(netCents, currencySymbol),
        activeCount = active.size,
        active = active.map { it.toRecordUi(settled = false, currencySymbol = currencySymbol) },
        settled = settled.map { it.toRecordUi(settled = true, currencySymbol = currencySymbol) },
        isLoading = isLoading,
    )

private fun Debt.toRecordUi(
    settled: Boolean,
    currencySymbol: String,
): DebtRecordUi =
    DebtRecordUi(
        id = id.value,
        name = personName,
        dateLabel = dueEpochMillis?.let { PlatformDateFormatter.shortDateLabel(it) } ?: "No due date",
        amountLabel = AmountInput.formatMoney(money.amount.valueInCents, currencySymbol),
        subtitle = note,
        settled = settled,
        amountCents = money.amount.valueInCents,
        dueEpochMillis = dueEpochMillis,
        recordedAtEpochMillis = recordedAtEpochMillis,
        recordAsTransaction = recordAsTransaction,
    )
