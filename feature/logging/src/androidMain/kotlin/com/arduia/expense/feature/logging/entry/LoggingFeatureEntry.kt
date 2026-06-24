package com.arduia.expense.feature.logging.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.RecordLink
import com.arduia.expense.domain.RecordType
import com.arduia.expense.feature.logging.LogRecordInput
import com.arduia.expense.feature.logging.LoggedExpenseHandoff
import com.arduia.expense.feature.logging.LoggingRepository
import com.arduia.expense.feature.logging.ui.QuickLogFlow
import com.arduia.expense.feature.logging.ui.preview.ExpenseEntryState
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.TagLinkKind
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import kotlin.math.roundToLong

interface LoggingFeatureEntry {
    @Composable
    fun QuickLogFlow(
        onDismiss: () -> Unit,
        onSaved: (LoggedExpenseHandoff) -> Unit,
        modifier: Modifier = Modifier,
    )
}

internal class LoggingFeatureEntryImpl : LoggingFeatureEntry {
    @Composable
    override fun QuickLogFlow(
        onDismiss: () -> Unit,
        onSaved: (LoggedExpenseHandoff) -> Unit,
        modifier: Modifier,
    ) {
        val scope = rememberCoroutineScope()
        val loggingRepository: LoggingRepository = koinInject()

        val onHandoff = onSaved
        com.arduia.expense.feature.logging.ui.QuickLogFlow(
            onDismiss = onDismiss,
            onSaved = { state ->
                scope.launch {
                    val amount = AmountInput.numericValue(state.rawAmount) ?: 0.0
                    val input = LogRecordInput(
                        money = Money(
                            Amount((amount * 100).roundToLong()),
                            CurrencyCode(state.currencyCode),
                        ),
                        homeCurrencyMoney = Money(
                            Amount((amount * 100).roundToLong()),
                            CurrencyCode(state.currencyCode),
                        ),
                        categoryId = CategoryId(state.selectedCategoryId),
                        type = RecordType.EXPENSE,
                        note = state.note.ifBlank { null },
                        recordedAtEpochMillis = System.currentTimeMillis(),
                        link = when (state.linkedTagKind) {
                            TagLinkKind.Event -> RecordLink.ToEvent(
                                com.arduia.expense.domain.EventId(state.linkedTagId.orEmpty()),
                            )
                            TagLinkKind.Debt -> RecordLink.ToDebt(
                                com.arduia.expense.domain.DebtId(state.linkedTagId.orEmpty()),
                            )
                            null -> RecordLink.None
                        },
                    )
                    when (loggingRepository.createRecord(input)) {
                        is Result.Success -> onHandoff(state.toHandoff())
                        is Result.Error -> {} // Error silently; UI already has toast handling
                    }
                }
            },
            modifier = modifier,
        )
    }
}

object LoggingFeatureUi : LoggingFeatureEntry by LoggingFeatureEntryImpl()

private fun ExpenseEntryState.toHandoff(): LoggedExpenseHandoff = LoggedExpenseHandoff(
    categoryId = selectedCategoryId,
    note = note,
    rawAmount = rawAmount,
    timeLabel = timeLabel,
    linkedTagLabel = linkedTagLabel,
)
