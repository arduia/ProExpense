package com.arduia.expense.ui.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.R
import com.arduia.expense.ui.design.EventBudgetCard
import com.arduia.expense.ui.design.EventBudgetCardState
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

val previewEvents = listOf(
    EventBudgetCardState(
        title = "Bali Trip",
        dateRange = "May 12 — May 26",
        spentLabel = "$1,240",
        budgetLabel = "of $2,000",
        progress = 0.62f,
    ),
    EventBudgetCardState(
        title = "John's Wedding",
        dateRange = "Jun 14 — Jun 15",
        spentLabel = "$800",
        budgetLabel = "of $1,500",
        progress = 0.53f,
    ),
    EventBudgetCardState(
        title = "Birthday party",
        dateRange = "Apr 20 — Apr 20",
        spentLabel = "$545",
        budgetLabel = "of $500",
        progress = 1f,
        isOverBudget = true,
        overAmountLabel = "over",
    ),
    EventBudgetCardState(
        title = "Weekend trip",
        dateRange = "Jun 1 — Jun 3",
        spentLabel = "$320",
        budgetLabel = "of $800",
        progress = 0.4f,
    ),
)

@Composable
fun BudgetScreenContent(
    events: List<EventBudgetCardState>,
    onNewEvent: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = dimens.space18, vertical = dimens.space24),
        verticalArrangement = Arrangement.spacedBy(dimens.space16),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
                Text(
                    text = stringResource(R.string.budget_tracker),
                    style = typography.eyebrow,
                    color = colors.muted,
                )
                Text(
                    text = stringResource(R.string.events),
                    style = typography.screenTitle,
                    color = colors.onSurface,
                )
            }
            ProButton(
                text = stringResource(R.string.new_event),
                onClick = onNewEvent,
                size = ProButtonSize.Sm,
            )
        }

        events.forEach { event ->
            EventBudgetCard(state = event)
        }
    }
}

@Preview(
    name = "Budget — events list",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun BudgetScreenPreview() {
    ProExpenseTheme {
        BudgetScreenContent(
            events = previewEvents,
            onNewEvent = {},
            modifier = Modifier.background(ProExpenseTheme.colors.paper),
        )
    }
}
