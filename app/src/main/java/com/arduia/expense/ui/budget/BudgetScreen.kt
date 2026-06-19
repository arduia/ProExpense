package com.arduia.expense.ui.budget

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arduia.expense.ui.design.EventBudgetCardState
import com.arduia.expense.ui.events.EventListScreenContent
import com.arduia.expense.ui.preview.previewEventListActive

val previewEvents: List<EventBudgetCardState> = previewEventListActive

@Composable
fun BudgetScreenContent(
    events: List<EventBudgetCardState>,
    onNewEvent: () -> Unit,
    modifier: Modifier = Modifier,
) {
    EventListScreenContent(
        events = events,
        onNewEvent = onNewEvent,
        onEventClick = {},
        modifier = modifier,
    )
}
