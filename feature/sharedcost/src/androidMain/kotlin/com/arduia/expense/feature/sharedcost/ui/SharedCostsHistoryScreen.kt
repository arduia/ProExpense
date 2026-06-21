package com.arduia.expense.feature.sharedcost.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.feature.sharedcost.R
import com.arduia.expense.ui.design.ProButton
import com.arduia.expense.ui.design.ProButtonSize
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.feature.sharedcost.ui.components.SharedCostHistoryRow
import com.arduia.expense.feature.sharedcost.ui.preview.SharedCostHistoryItemUi
import com.arduia.expense.feature.sharedcost.ui.preview.previewSharedHistoryItems
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun SharedCostsHistoryScreen(
    items: List<SharedCostHistoryItemUi>,
    onNewSplit: () -> Unit,
    onItemClick: (SharedCostHistoryItemUi) -> Unit,
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
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = dimens.screenPadding)
            .padding(bottom = dimens.space18),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimens.space8, bottom = dimens.space16),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(dimens.space8),
            ) {
                Text(
                    text = stringResource(R.string.shared_bill_splitter),
                    style = typography.eyebrow,
                    color = colors.muted,
                )
                Text(
                    text = stringResource(R.string.shared_costs_heading),
                    style = typography.profileScreenTitle,
                    color = colors.onSurface,
                )
            }
            ProButton(
                text = stringResource(R.string.shared_new_split),
                onClick = onNewSplit,
                size = ProButtonSize.Sm,
                leading = {
                    ProIcon(
                        glyph = ProIconGlyph.Plus,
                        contentDescription = null,
                        tint = ProExpenseTheme.colors.onPrimaryWarm,
                        size = dimens.iconInline,
                    )
                },
            )
        }

        Text(
            text = stringResource(R.string.shared_recent_splits),
            style = typography.eyebrow,
            color = colors.onSurfaceVariant,
            modifier = Modifier.padding(bottom = dimens.space10),
        )

        Column(verticalArrangement = Arrangement.spacedBy(dimens.space8)) {
            items.forEach { item ->
                SharedCostHistoryRow(
                    title = item.title,
                    meta = stringResource(
                        R.string.shared_history_meta,
                        item.peopleCount,
                        item.perPersonLabel,
                        item.dateLabel,
                    ),
                    total = item.totalLabel,
                    onClick = { onItemClick(item) },
                )
            }
        }
    }
}

@Preview(
    name = "Shared costs — history",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun SharedCostsHistoryPreview() {
    ProExpenseTheme {
        SharedCostsHistoryScreen(
            items = previewSharedHistoryItems,
            onNewSplit = {},
            onItemClick = {},
        )
    }
}
