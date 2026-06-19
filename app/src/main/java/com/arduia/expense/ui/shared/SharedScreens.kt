package com.arduia.expense.ui.shared

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
import com.arduia.expense.ui.design.PeopleStepper
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProTopBar
import com.arduia.expense.ui.design.ProfileNameField
import com.arduia.expense.ui.design.SharedPersonRow
import com.arduia.expense.ui.preview.SharedHistoryItem
import com.arduia.expense.ui.preview.previewSharedHistory
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun SharedCostsScreenContent(
    amountText: String,
    onAmountChange: (String) -> Unit,
    peopleCount: Int,
    showZeroValidation: Boolean,
    onIncrementPeople: () -> Unit,
    onDecrementPeople: () -> Unit,
    onCalculate: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val canProceed = amountText.isNotBlank() && amountText != "0"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.paper)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        ProTopBar(
            title = stringResource(R.string.shared_costs_title),
            onBack = onBack,
            modifier = Modifier.padding(horizontal = dimens.space18),
        )
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimens.space18, vertical = dimens.space16),
            verticalArrangement = Arrangement.spacedBy(dimens.space24),
        ) {
            Text(text = stringResource(R.string.shared_total_label), style = typography.eyebrow, color = colors.muted)
            ProfileNameField(
                value = amountText,
                onValueChange = onAmountChange,
                placeholder = stringResource(R.string.amount),
            )
            if (showZeroValidation) {
                Text(
                    text = stringResource(R.string.amount_must_be_greater_than_zero),
                    style = typography.caption,
                    color = colors.danger,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = stringResource(R.string.shared_people_label), style = typography.bodySemiBold, color = colors.onSurface)
                PeopleStepper(
                    count = peopleCount,
                    onIncrement = onIncrementPeople,
                    onDecrement = onDecrementPeople,
                )
            }
            ProButton(
                text = stringResource(R.string.shared_calculate),
                onClick = onCalculate,
                enabled = canProceed,
                size = ProButtonSize.Lg,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun SharedSummaryScreenContent(
    people: List<Pair<String, String>>,
    onBack: () -> Unit,
    onSave: () -> Unit,
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
            title = stringResource(R.string.shared_summary_title),
            onBack = onBack,
            modifier = Modifier.padding(horizontal = dimens.space18),
        )
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimens.space18, vertical = dimens.space16),
            verticalArrangement = Arrangement.spacedBy(dimens.space8),
        ) {
            Text(text = stringResource(R.string.shared_per_person), style = typography.eyebrow, color = colors.muted)
            people.forEach { (name, amount) ->
                SharedPersonRow(name = name, amount = amount)
            }
            ProButton(
                text = stringResource(R.string.shared_save_split),
                onClick = onSave,
                size = ProButtonSize.Lg,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimens.space16),
            )
        }
    }
}

@Composable
fun SharedHistoryScreenContent(
    items: List<SharedHistoryItem>,
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
            title = stringResource(R.string.shared_history_title),
            onBack = onBack,
            modifier = Modifier.padding(horizontal = dimens.space18),
        )
        if (items.isEmpty()) {
            Text(
                text = stringResource(R.string.shared_history_empty),
                style = typography.body,
                color = colors.onSurfaceMuted,
                modifier = Modifier.padding(horizontal = dimens.space18, vertical = dimens.space24),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimens.space18, vertical = dimens.space16),
                verticalArrangement = Arrangement.spacedBy(dimens.space12),
            ) {
                items(items, key = { "${it.title}-${it.dateLabel}" }) { item ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.surface, ProExpenseTheme.shapes.card)
                            .padding(dimens.space14),
                    ) {
                        Text(text = item.title, style = typography.bodySemiBold, color = colors.onSurface)
                        Text(text = item.dateLabel, style = typography.caption, color = colors.onSurfaceMuted)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = dimens.space8),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = stringResource(R.string.shared_people_count, item.peopleCount),
                                style = typography.caption,
                                color = colors.muted,
                            )
                            Text(text = item.total, style = typography.bodySemiBold, color = colors.onSurface)
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Shared input", widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP, heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP, showBackground = true)
@Composable
private fun SharedInputPreview() {
    ProExpenseTheme {
        SharedCostsScreenContent(
            amountText = "120.00",
            onAmountChange = {},
            peopleCount = 4,
            showZeroValidation = false,
            onIncrementPeople = {},
            onDecrementPeople = {},
            onCalculate = {},
            onBack = {},
        )
    }
}

@Preview(name = "Shared summary", widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP, heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP, showBackground = true)
@Composable
private fun SharedSummaryPreview() {
    ProExpenseTheme {
        SharedSummaryScreenContent(
            people = listOf("Alex" to "$30.00", "Jordan" to "$30.00", "Sam" to "$30.00", "Taylor" to "$30.00"),
            onBack = {},
            onSave = {},
        )
    }
}

@Preview(name = "Shared history", widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP, heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP, showBackground = true)
@Composable
private fun SharedHistoryPreview() {
    ProExpenseTheme {
        SharedHistoryScreenContent(items = previewSharedHistory, onBack = {})
    }
}

@Preview(name = "Shared history — empty", widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP, heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP, showBackground = true)
@Composable
private fun SharedHistoryEmptyPreview() {
    ProExpenseTheme {
        SharedHistoryScreenContent(items = emptyList(), onBack = {})
    }
}
