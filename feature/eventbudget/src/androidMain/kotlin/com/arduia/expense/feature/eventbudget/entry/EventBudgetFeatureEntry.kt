package com.arduia.expense.feature.eventbudget.entry

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arduia.expense.feature.eventbudget.ui.EventsFlow
import com.arduia.expense.feature.eventbudget.ui.preview.EventDetailUiState
import com.arduia.expense.feature.eventbudget.ui.preview.previewEventList
import com.arduia.expense.ui.design.EventBudgetCardState
import com.arduia.expense.ui.design.HomeNavTab

interface EventBudgetFeatureEntry {
    @Composable
    fun EventsTab(
        onTabSelected: (HomeNavTab) -> Unit,
        onAddClick: () -> Unit,
        events: List<EventBudgetCardState> = previewEventList,
        detailFor: (String) -> EventDetailUiState? = { null },
        onCreateEvent: (name: String, budgetRaw: String) -> Unit = { _, _ -> },
        modifier: Modifier = Modifier,
    )
}

internal class EventBudgetFeatureEntryImpl : EventBudgetFeatureEntry {
    @Composable
    override fun EventsTab(
        onTabSelected: (HomeNavTab) -> Unit,
        onAddClick: () -> Unit,
        events: List<EventBudgetCardState>,
        detailFor: (String) -> EventDetailUiState?,
        onCreateEvent: (name: String, budgetRaw: String) -> Unit,
        modifier: Modifier,
    ) {
        EventsFlow(
            onTabSelected = onTabSelected,
            onAddClick = onAddClick,
            events = events,
            detailFor = detailFor,
            onCreateEvent = onCreateEvent,
            modifier = modifier,
        )
    }
}

object EventBudgetFeatureUi : EventBudgetFeatureEntry by EventBudgetFeatureEntryImpl()
