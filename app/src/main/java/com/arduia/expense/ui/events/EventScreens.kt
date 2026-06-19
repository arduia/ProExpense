package com.arduia.expense.ui.events

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.design.ProfileNameField
import com.arduia.expense.ui.design.TransactionRow
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.ui.preview.HomeTransactionItem
import com.arduia.expense.ui.preview.previewEventDetailTransactions
import com.arduia.expense.ui.preview.previewEventListActive
import com.arduia.expense.ui.preview.previewEventListEmpty
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun EventListScreenContent(
    events: List<EventBudgetCardState>,
    onNewEvent: () -> Unit,
    onEventClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .padding(
                start = dimens.space18,
                end = dimens.space18,
                top = dimens.space24,
                bottom = dimens.space24 + dimens.navShellBottomInset,
            ),
        verticalArrangement = Arrangement.spacedBy(dimens.space16),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
                    Text(text = stringResource(R.string.budget_tracker), style = typography.eyebrow, color = colors.muted)
                    Text(text = stringResource(R.string.events), style = typography.screenTitle, color = colors.onSurface)
                }
                ProButton(text = stringResource(R.string.new_event), onClick = onNewEvent, size = ProButtonSize.Sm)
            }
        }

        if (events.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimens.space32),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(dimens.space8),
                ) {
                    Text(text = stringResource(R.string.event_empty_title), style = typography.sectionHead, color = colors.onSurface)
                    Text(text = stringResource(R.string.event_empty_subtitle), style = typography.body, color = colors.onSurfaceMuted)
                }
            }
        } else {
            items(events, key = { it.title }) { event ->
                EventBudgetCard(
                    state = event,
                    modifier = Modifier.proClickable(onClick = { onEventClick(event.title) }, shape = ProExpenseTheme.shapes.card),
                )
            }
        }
    }
}

@Composable
fun EventCreateScreenContent(
    name: String,
    onNameChange: (String) -> Unit,
    dateRange: String,
    amountText: String,
    onAmountChange: (String) -> Unit,
    showErrors: Boolean,
    onBack: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val canSave = name.isNotBlank() && amountText.isNotBlank() && amountText != "0"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        ProTopBar(
            title = stringResource(R.string.event_create_title),
            onBack = onBack,
            modifier = Modifier.padding(horizontal = dimens.space18),
        )
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimens.space18, vertical = dimens.space16),
            verticalArrangement = Arrangement.spacedBy(dimens.space16),
        ) {
            ProfileNameField(
                value = name,
                onValueChange = onNameChange,
                placeholder = stringResource(R.string.event_name_hint),
            )
            Text(text = stringResource(R.string.event_date_range), style = typography.eyebrow, color = colors.muted)
            Text(text = dateRange, style = typography.bodySemiBold, color = colors.onSurface)
            Text(text = stringResource(R.string.event_budget_amount), style = typography.eyebrow, color = colors.muted)
            ProfileNameField(
                value = amountText,
                onValueChange = onAmountChange,
                placeholder = stringResource(R.string.amount),
            )
            if (showErrors && name.isBlank()) {
                Text(text = stringResource(R.string.event_name_required), style = typography.caption, color = colors.danger)
            }
            if (showErrors && (amountText.isBlank() || amountText == "0")) {
                Text(
                    text = stringResource(R.string.amount_must_be_greater_than_zero),
                    style = typography.caption,
                    color = colors.danger,
                )
            }
            ProButton(
                text = stringResource(R.string.event_save),
                onClick = onSave,
                enabled = canSave,
                size = ProButtonSize.Lg,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun EventDetailScreenContent(
    title: String,
    dateRange: String,
    spentLabel: String,
    budgetLabel: String,
    progress: Float,
    isClosed: Boolean,
    transactions: List<HomeTransactionItem>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        ProTopBar(
            title = stringResource(R.string.event_detail_title),
            onBack = onBack,
            modifier = Modifier.padding(horizontal = dimens.space18),
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dimens.space18, vertical = dimens.space16),
            verticalArrangement = Arrangement.spacedBy(dimens.space16),
        ) {
            item {
                EventBudgetCard(
                    state = EventBudgetCardState(
                        title = title,
                        dateRange = dateRange,
                        spentLabel = spentLabel,
                        budgetLabel = budgetLabel,
                        progress = progress,
                        isOverBudget = progress > 1f,
                    ),
                )
            }
            if (isClosed) {
                item {
                    Text(text = stringResource(R.string.event_closed_label), style = typography.caption, color = colors.muted)
                }
            }
            item {
                Text(text = stringResource(R.string.event_linked_transactions), style = typography.sectionHead, color = colors.onSurface)
            }
            items(
                items = transactions,
                key = { "${it.categoryId}-${it.note}-${it.meta}" },
            ) { item ->
                TransactionRow(
                    categoryId = item.categoryId,
                    note = item.note,
                    meta = item.meta,
                    amount = item.amount,
                )
            }
        }
    }
}

@Preview(name = "Event list — active", widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP, heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP, showBackground = true)
@Composable
private fun EventListActivePreview() {
    ProExpenseTheme {
        EventListScreenContent(events = previewEventListActive, onNewEvent = {}, onEventClick = {})
    }
}

@Preview(name = "Event list — empty", widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP, heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP, showBackground = true)
@Composable
private fun EventListEmptyPreview() {
    ProExpenseTheme {
        EventListScreenContent(events = previewEventListEmpty, onNewEvent = {}, onEventClick = {})
    }
}

@Preview(name = "Event create", widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP, heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP, showBackground = true)
@Composable
private fun EventCreatePreview() {
    ProExpenseTheme {
        EventCreateScreenContent(
            name = "Bali Trip",
            onNameChange = {},
            dateRange = "May 12 — May 26",
            amountText = "2000",
            onAmountChange = {},
            showErrors = false,
            onBack = {},
            onSave = {},
        )
    }
}

@Preview(name = "Event detail", widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP, heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP, showBackground = true)
@Composable
private fun EventDetailPreview() {
    ProExpenseTheme {
        EventDetailScreenContent(
            title = "Bali Trip",
            dateRange = "May 12 — May 26",
            spentLabel = "$1,240",
            budgetLabel = "of $2,000",
            progress = 0.62f,
            isClosed = false,
            transactions = previewEventDetailTransactions,
            onBack = {},
        )
    }
}
