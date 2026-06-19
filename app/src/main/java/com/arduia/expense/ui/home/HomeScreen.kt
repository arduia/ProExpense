package com.arduia.expense.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.arduia.expense.R
import com.arduia.expense.ui.design.DayHeader
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.QuickAccessTile
import com.arduia.expense.ui.design.TransactionRow
import com.arduia.expense.ui.preview.HomeUiState
import com.arduia.expense.ui.preview.previewHomeCasual
import com.arduia.expense.ui.preview.previewHomeEmpty
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun HomeScreenContent(
    state: HomeUiState,
    onReportsClick: () -> Unit,
    onDebtClick: () -> Unit,
    onSplitClick: () -> Unit,
    onEventsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = dimens.space18, vertical = dimens.space24),
        verticalArrangement = Arrangement.spacedBy(dimens.space16),
    ) {
        item(key = "home-summary") {
            Column(verticalArrangement = Arrangement.spacedBy(dimens.space16)) {
                if (state.greetingName.isNotBlank()) {
                    Text(
                        text = stringResource(R.string.home_greeting, state.greetingName),
                        style = typography.eyebrow,
                        color = colors.muted,
                    )
                }
                Text(
                    text = stringResource(R.string.home_spend_this_month),
                    style = typography.eyebrow,
                    color = colors.muted,
                )
                Text(
                    text = state.monthSpend,
                    style = typography.displayAmount,
                    color = colors.onSurface,
                )
                if (state.monthDelta != null) {
                    Text(
                        text = state.monthDelta,
                        style = typography.caption,
                        color = colors.success,
                    )
                } else if (state.showEmptyHint) {
                    Text(
                        text = stringResource(R.string.home_empty_hint),
                        style = typography.body,
                        color = colors.onSurfaceMuted,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimens.space8),
                ) {
                    QuickAccessTile(
                        label = stringResource(R.string.quick_access_reports),
                        icon = ProIconGlyph.FeatReports,
                        tint = colors.primaryTint,
                        accent = colors.primaryDeep,
                        onClick = onReportsClick,
                        modifier = Modifier.weight(1f),
                    )
                    QuickAccessTile(
                        label = stringResource(R.string.quick_access_debt),
                        icon = ProIconGlyph.FeatDebt,
                        tint = colors.successTint,
                        accent = colors.success,
                        onClick = onDebtClick,
                        modifier = Modifier.weight(1f),
                    )
                    QuickAccessTile(
                        label = stringResource(R.string.quick_access_split),
                        icon = ProIconGlyph.FeatSplit,
                        tint = colors.tagTint,
                        accent = colors.tagDeep,
                        onClick = onSplitClick,
                        modifier = Modifier.weight(1f),
                    )
                    QuickAccessTile(
                        label = stringResource(R.string.quick_access_events),
                        icon = ProIconGlyph.FeatEvents,
                        tint = colors.highlightSoft,
                        accent = colors.highlightDeep,
                        onClick = onEventsClick,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        if (state.dayGroups.isNotEmpty()) {
            item(key = "home-recent-title") {
                Text(
                    text = stringResource(R.string.recent),
                    style = typography.sectionHead,
                    color = colors.onSurface,
                    modifier = Modifier.padding(top = dimens.space8),
                )
            }
            state.dayGroups.forEach { group ->
                item(key = "home-header-${group.dayTitle}") {
                    DayHeader(title = group.dayTitle, total = group.dayTotal)
                }
                items(
                    items = group.transactions,
                    key = { "${group.dayTitle}-${it.note}-${it.amount}" },
                ) { item ->
                    TransactionRow(
                        categoryId = item.categoryId,
                        note = item.note,
                        meta = item.meta,
                        amount = item.amount,
                        tag = item.tag,
                    )
                }
            }
        }
    }
}

@Composable
fun TabPlaceholderContent(
    title: String,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(ProExpenseTheme.dimensions.space24),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = title, style = typography.screenTitle, color = colors.onSurface)
    }
}

@Preview(
    name = "Home — casual",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun HomeCasualPreview() {
    ProExpenseTheme {
        HomeScreenContent(
            state = previewHomeCasual,
            onReportsClick = {},
            onDebtClick = {},
            onSplitClick = {},
            onEventsClick = {},
        )
    }
}

@Preview(
    name = "Home — empty",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun HomeEmptyPreview() {
    ProExpenseTheme {
        HomeScreenContent(
            state = previewHomeEmpty,
            onReportsClick = {},
            onDebtClick = {},
            onSplitClick = {},
            onEventsClick = {},
        )
    }
}
