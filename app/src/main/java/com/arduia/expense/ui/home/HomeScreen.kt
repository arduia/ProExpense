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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.R
import com.arduia.expense.ui.design.DayHeader
import com.arduia.expense.ui.design.EventBudgetCard
import com.arduia.expense.ui.design.EventBudgetCardState
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.QuickAccessTile
import com.arduia.expense.ui.design.TransactionRow
import com.arduia.expense.ui.design.proCardSurface
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.ui.preview.HomeUiState
import com.arduia.expense.ui.preview.previewHomeBudget
import com.arduia.expense.ui.preview.previewHomeCasual
import com.arduia.expense.ui.preview.previewHomeEmpty
import com.arduia.expense.ui.preview.previewHomeEvent
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
    showPinSetupBanner: Boolean = false,
    onPinBannerTap: () -> Unit = {},
    onPinBannerDismiss: () -> Unit = {},
    onActiveEventClick: (String) -> Unit = {},
    onSeeAllRecentClick: () -> Unit = {},
    onCustomizeQuickAccessClick: () -> Unit = {},
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val cardShape = ProExpenseTheme.shapes.card

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = dimens.space18,
            end = dimens.space18,
            top = dimens.space24,
            bottom = dimens.space24 + dimens.navShellBottomInset,
        ),
        verticalArrangement = Arrangement.spacedBy(dimens.space16),
    ) {
        item(key = "home-summary") {
            Column(verticalArrangement = Arrangement.spacedBy(dimens.space16)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = state.dateHeader.ifBlank { stringResource(R.string.home_date_header) },
                        style = typography.eyebrow,
                        color = colors.muted,
                    )
                    ProIcon(
                        glyph = ProIconGlyph.Bell,
                        contentDescription = stringResource(R.string.notifications),
                        tint = colors.onSurface,
                        size = dimens.iconNav,
                    )
                }

                if (state.greetingName.isNotBlank()) {
                    val greetingPrefix = stringResource(R.string.home_greeting_prefix)
                    val flourish = typography.displayFlourish
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    color = colors.onSurface,
                                    fontFamily = flourish.fontFamily,
                                    fontSize = flourish.fontSize,
                                    fontStyle = flourish.fontStyle,
                                    letterSpacing = flourish.letterSpacing,
                                ),
                            ) {
                                append(greetingPrefix)
                            }
                            withStyle(
                                SpanStyle(
                                    color = colors.primary,
                                    fontFamily = flourish.fontFamily,
                                    fontSize = flourish.fontSize,
                                    fontStyle = flourish.fontStyle,
                                    letterSpacing = flourish.letterSpacing,
                                ),
                            ) {
                                append(state.greetingName)
                            }
                        },
                    )
                }

                if (showPinSetupBanner) {
                    PinSetupBanner(
                        onTap = onPinBannerTap,
                        onDismiss = onPinBannerDismiss,
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .proCardSurface(shape = cardShape)
                        .padding(dimens.cardPadding),
                    verticalArrangement = Arrangement.spacedBy(dimens.space8),
                ) {
                    Text(
                        text = if (state.spendPeriodLabel.isNotBlank()) {
                            stringResource(R.string.home_spent_period, state.spendPeriodLabel)
                        } else {
                            stringResource(R.string.home_spend_this_month)
                        },
                        style = typography.eyebrow,
                        color = colors.muted,
                    )
                    Text(
                        text = state.monthSpend,
                        style = typography.summaryAmount,
                        color = colors.onSurface,
                    )
                    if (state.budgetSummary != null) {
                        val budget = state.budgetSummary
                        Text(
                            text = "${budget.statusLabel} · ${budget.spentLabel} ${budget.budgetLabel}",
                            style = typography.bodyMedium,
                            color = if (budget.isOverBudget) colors.danger else colors.onSurfaceMuted,
                        )
                    }
                    if (state.monthDelta != null) {
                        Text(
                            text = state.monthDelta,
                            style = typography.caption,
                            color = if (state.monthDelta.startsWith("+")) colors.danger else colors.success,
                        )
                    } else if (state.showEmptyHint) {
                        Text(
                            text = stringResource(R.string.home_empty_hint),
                            style = typography.body,
                            color = colors.onSurfaceMuted,
                        )
                    }
                }

                state.activeEvent?.let { event ->
                    EventBudgetCard(
                        state = EventBudgetCardState(
                            id = event.eventId,
                            title = event.title,
                            dateRange = event.dateRange,
                            spentLabel = event.spentLabel,
                            budgetLabel = event.budgetLabel,
                            progress = event.progress,
                            isOverBudget = event.isOverBudget,
                        ),
                        modifier = Modifier.proClickable(
                            onClick = { onActiveEventClick(event.eventId) },
                            shape = cardShape,
                        ),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.quick_access_section),
                        style = typography.eyebrow,
                        color = colors.muted,
                    )
                    Text(
                        text = stringResource(R.string.customize),
                        style = typography.caption,
                        color = colors.muted,
                        modifier = Modifier.proClickable(
                            onClick = onCustomizeQuickAccessClick,
                            shape = ProExpenseTheme.shapes.chip,
                        ),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimens.space8),
                ) {
                    QuickAccessTile(
                        label = stringResource(R.string.quick_access_reports),
                        icon = ProIconGlyph.FeatReports,
                        onClick = onReportsClick,
                        iconTint = colors.primaryDeep,
                        iconBackground = colors.primaryTint,
                        modifier = Modifier.weight(1f),
                    )
                    QuickAccessTile(
                        label = stringResource(R.string.quick_access_debt),
                        icon = ProIconGlyph.FeatDebt,
                        onClick = onDebtClick,
                        iconTint = colors.success,
                        iconBackground = colors.successTint,
                        modifier = Modifier.weight(1f),
                    )
                    QuickAccessTile(
                        label = stringResource(R.string.quick_access_split),
                        icon = ProIconGlyph.FeatSplit,
                        onClick = onSplitClick,
                        iconTint = colors.tagDeep,
                        iconBackground = colors.tagTint,
                        modifier = Modifier.weight(1f),
                    )
                    QuickAccessTile(
                        label = stringResource(R.string.quick_access_events),
                        icon = ProIconGlyph.FeatEvents,
                        onClick = onEventsClick,
                        iconTint = colors.highlightDeep,
                        iconBackground = colors.highlightSoft,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        if (state.dayGroups.isNotEmpty()) {
            item(key = "home-recent-title") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimens.space8),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.recent),
                        style = typography.sectionHead,
                        color = colors.onSurface,
                    )
                    Text(
                        text = stringResource(R.string.see_all),
                        style = typography.bodyMedium,
                        color = colors.primary,
                        modifier = Modifier.proClickable(
                            onClick = onSeeAllRecentClick,
                            shape = ProExpenseTheme.shapes.chip,
                        ),
                    )
                }
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
    name = "Home — budget",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun HomeBudgetPreview() {
    ProExpenseTheme {
        HomeScreenContent(
            state = previewHomeBudget,
            onReportsClick = {},
            onDebtClick = {},
            onSplitClick = {},
            onEventsClick = {},
        )
    }
}

@Preview(
    name = "Home — event",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun HomeEventPreview() {
    ProExpenseTheme {
        HomeScreenContent(
            state = previewHomeEvent,
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
