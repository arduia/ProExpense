package com.arduia.expense.feature.logging.entry

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arduia.expense.feature.logging.LoggedExpenseHandoff
import com.arduia.expense.feature.logging.ui.QuickLogFlow
import com.arduia.expense.feature.logging.ui.preview.ExpenseEntryState

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
        val onHandoff = onSaved
        com.arduia.expense.feature.logging.ui.QuickLogFlow(
            onDismiss = onDismiss,
            onSaved = { state -> onHandoff(state.toHandoff()) },
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
