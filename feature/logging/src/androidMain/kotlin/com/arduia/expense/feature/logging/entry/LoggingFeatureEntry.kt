package com.arduia.expense.feature.logging.entry

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arduia.expense.domain.ExpenseTagType
import com.arduia.expense.feature.logging.LoggedExpenseHandoff
import com.arduia.expense.feature.logging.ui.preview.ExpenseEntryState
import com.arduia.expense.ui.design.TagLinkKind

interface LoggingFeatureEntry {
    @Composable
    fun QuickLogFlow(
        onDismiss: () -> Unit,
        onSaved: (LoggedExpenseHandoff) -> Unit,
        modifier: Modifier = Modifier,
        start: LoggedExpenseHandoff? = null,
    )
}

internal class LoggingFeatureEntryImpl : LoggingFeatureEntry {
    @Composable
    override fun QuickLogFlow(
        onDismiss: () -> Unit,
        onSaved: (LoggedExpenseHandoff) -> Unit,
        modifier: Modifier,
        start: LoggedExpenseHandoff?,
    ) {
        com.arduia.expense.feature.logging.ui.QuickLogFlow(
            onDismiss = onDismiss,
            onSaved = { state -> onSaved(state.toHandoff()) },
            modifier = modifier,
            startState = start?.toEntryState() ?: ExpenseEntryState(),
        )
    }
}

object LoggingFeatureUi : LoggingFeatureEntry by LoggingFeatureEntryImpl()

private fun ExpenseEntryState.toHandoff(): LoggedExpenseHandoff = LoggedExpenseHandoff(
    id = id,
    categoryId = selectedCategoryId,
    note = note,
    rawAmount = rawAmount,
    currencyCode = currencyCode,
    recordedAtEpochMillis = recordedAtEpochMillis,
    timeLabel = timeLabel,
    tagType = linkedTagKind?.toTagType(),
    tagId = linkedTagId,
    linkedTagLabel = linkedTagLabel,
)

private fun LoggedExpenseHandoff.toEntryState(): ExpenseEntryState = ExpenseEntryState(
    id = id,
    rawAmount = rawAmount,
    selectedCategoryId = categoryId,
    note = note,
    timeLabel = timeLabel,
    recordedAtEpochMillis = recordedAtEpochMillis,
    linkedTagId = tagId,
    linkedTagKind = tagType?.toLinkKind(),
    linkedTagLabel = linkedTagLabel,
    currencyCode = currencyCode,
)

private fun TagLinkKind.toTagType(): ExpenseTagType = when (this) {
    TagLinkKind.Event -> ExpenseTagType.EVENT
    TagLinkKind.Debt -> ExpenseTagType.DEBT
}

private fun ExpenseTagType.toLinkKind(): TagLinkKind = when (this) {
    ExpenseTagType.EVENT -> TagLinkKind.Event
    ExpenseTagType.DEBT -> TagLinkKind.Debt
}
