package com.arduia.expense.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arduia.expense.R
import com.arduia.expense.ui.design.DayGroup
import com.arduia.expense.ui.design.EmptyStateContent
import com.arduia.expense.ui.design.EventBudgetCard
import com.arduia.expense.ui.design.EventBudgetCardState
import com.arduia.expense.ui.design.HeroGreeting
import com.arduia.expense.ui.design.NoticeBanner
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.ProTransactionRowModel
import com.arduia.expense.ui.design.ProTextAction
import com.arduia.expense.ui.design.QuickAccessTile
import com.arduia.expense.ui.design.proClickable
import com.arduia.expense.ui.design.proIconClickable
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
    onLogFirstExpense: () -> Unit = {},
    onSeeAll: () -> Unit = {},
    onCustomizeQuickAccess: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onTransactionClick: (String) -> Unit = {},
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimens.screenPadding)
            .padding(top = dimens.space14),
    ) {
        HomeHeader(
            dateLabel = state.dateLabel,
            greetingName = state.greetingName,
            greetingPrefixRes = state.greetingPrefixRes,
            onNotificationsClick = onNotificationsClick,
        )

        Column(
            modifier = Modifier.padding(top = dimens.space18),
            verticalArrangement = Arrangement.spacedBy(dimens.space16),
        ) {
            if (showPinSetupBanner) {
                NoticeBanner(
                    title = stringResource(R.string.home_pin_banner_title),
                    body = stringResource(R.string.home_pin_banner_body),
                    onClick = onPinBannerTap,
                    onDismiss = onPinBannerDismiss,
                    dismissContentDescription = stringResource(R.string.dismiss),
                )
            }

            MonthSpendCard(
                monthLabel = state.monthLabel,
                monthSpend = state.monthSpend,
                budgetSummary = state.budgetSummary,
                monthDelta = state.monthDelta,
                showEmptyHint = state.showEmptyHint,
                showSparkline = !state.isEmpty,
            )

            state.activeEvent?.let { event ->
                val cardShape = ProExpenseTheme.shapes.card
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
        }

        HomeQuickAccessSection(
            showCustomize = !state.isEmpty,
            onCustomize = onCustomizeQuickAccess,
            onReportsClick = onReportsClick,
            onDebtClick = onDebtClick,
            onSplitClick = onSplitClick,
            onEventsClick = onEventsClick,
            modifier = Modifier.padding(top = dimens.space24),
        )

        if (state.isEmpty) {
            HomeEmptyContent(
                onLogFirstExpense = onLogFirstExpense,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(bottom = dimens.navShellBottomInset),
            )
        } else {
            HomeRecentSection(
                groups = state.dayGroups,
                onSeeAll = onSeeAll,
                onTransactionClick = onTransactionClick,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = dimens.space26),
            )
        }
    }
}

@Composable
private fun HomeHeader(
    dateLabel: String,
    greetingName: String,
    greetingPrefixRes: String,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val greetingPrefix = when (greetingPrefixRes) {
        "welcome" -> stringResource(R.string.home_welcome_prefix)
        else -> stringResource(R.string.home_greeting_prefix)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column {
            if (dateLabel.isNotBlank()) {
                Text(
                    text = dateLabel.uppercase(),
                    style = typography.eyebrow,
                    color = colors.onSurfaceVariant,
                )
            }
            if (greetingName.isNotBlank()) {
                HeroGreeting(
                    name = greetingName,
                    prefix = greetingPrefix,
                    modifier = Modifier.padding(top = dimens.space4),
                )
            }
        }
        Box(
            modifier = Modifier
                .size(dimens.touchTargetMin)
                .clip(CircleShape)
                .border(BorderStroke(1.dp, colors.lineStrong), CircleShape)
                .background(colors.surface)
                .proIconClickable(onClick = onNotificationsClick),
            contentAlignment = Alignment.Center,
        ) {
            ProIcon(
                glyph = ProIconGlyph.Bell,
                contentDescription = stringResource(R.string.notifications),
                tint = colors.onSurfaceVariant,
                size = dimens.iconNav,
            )
        }
    }
}

@Composable
private fun MonthSpendCard(
    monthLabel: String,
    monthSpend: String,
    budgetSummary: com.arduia.expense.ui.preview.HomeBudgetSummaryState?,
    monthDelta: String?,
    showEmptyHint: Boolean,
    showSparkline: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val cardShape = ProExpenseTheme.shapes.card
    val cardElevation = ProExpenseTheme.elevation.card.firstOrNull()
    val periodLabel = if (monthLabel.isNotBlank()) {
        stringResource(R.string.home_spent_period, monthLabel.uppercase())
    } else {
        stringResource(R.string.home_spend_this_month)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (cardElevation != null) {
                    Modifier.shadow(
                        elevation = cardElevation.blur,
                        shape = cardShape,
                        spotColor = cardElevation.color,
                        ambientColor = cardElevation.color,
                    )
                } else {
                    Modifier
                },
            )
            .clip(cardShape)
            .border(BorderStroke(1.dp, colors.line), cardShape)
            .background(colors.surface)
            .padding(dimens.cardPadding),
    ) {
        Text(
            text = periodLabel,
            style = typography.eyebrow,
            color = colors.onSurfaceVariant,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimens.space4),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
                Text(
                    text = monthSpend,
                    style = typography.summaryAmount,
                    color = colors.onSurface,
                )
            if (showSparkline) {
                SpendSparkline(
                    modifier = Modifier
                        .width(86.dp)
                        .height(40.dp),
                )
            }
        }
        if (budgetSummary != null) {
            Text(
                text = "${budgetSummary.statusLabel} · ${budgetSummary.spentLabel} ${budgetSummary.budgetLabel}",
                style = typography.bodyMedium,
                color = if (budgetSummary.isOverBudget) colors.danger else colors.onSurfaceMuted,
                modifier = Modifier.padding(top = dimens.space6),
            )
        }
        when {
            monthDelta != null -> {
                val isIncrease = monthDelta.startsWith("+")
                val deltaText = monthDelta.removePrefix("+").removePrefix("-")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(dimens.space4),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = dimens.space6),
                ) {
                    Text(
                        text = if (isIncrease) "↑" else "↓",
                        style = typography.caption.copy(fontWeight = FontWeight.SemiBold),
                        color = if (isIncrease) colors.danger else colors.success,
                    )
                    Text(
                        text = deltaText,
                        style = typography.caption,
                        color = if (isIncrease) colors.danger else colors.success,
                    )
                }
            }
            showEmptyHint -> {
                Text(
                    text = stringResource(R.string.home_empty_hint),
                    style = typography.body,
                    color = colors.onSurfaceMuted,
                    modifier = Modifier.padding(top = dimens.space6),
                )
            }
        }
    }
}

@Composable
private fun SpendSparkline(modifier: Modifier = Modifier) {
    val lineColor = ProExpenseTheme.colors.primary
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val path = Path().apply {
            moveTo(2f, size.height * 0.72f)
            cubicTo(
                size.width * 0.2f,
                size.height * 0.55f,
                size.width * 0.35f,
                size.height * 0.8f,
                size.width * 0.5f,
                size.height * 0.45f,
            )
            cubicTo(
                size.width * 0.65f,
                size.height * 0.2f,
                size.width * 0.8f,
                size.height * 0.5f,
                size.width,
                size.height * 0.28f,
            )
        }
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 2.5f, cap = StrokeCap.Round),
        )
        drawCircle(
            color = lineColor,
            radius = 4f,
            center = androidx.compose.ui.geometry.Offset(size.width, size.height * 0.28f),
        )
    }
}

@Composable
private fun HomeQuickAccessSection(
    showCustomize: Boolean,
    onCustomize: () -> Unit,
    onReportsClick: () -> Unit,
    onDebtClick: () -> Unit,
    onSplitClick: () -> Unit,
    onEventsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.quick_access_section),
                style = typography.eyebrow,
                color = colors.onSurfaceVariant,
            )
            if (showCustomize) {
                ProTextAction(
                    text = stringResource(R.string.customize),
                    onClick = onCustomize,
                    style = typography.caption,
                    color = colors.onSurfaceMuted,
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimens.space10),
            horizontalArrangement = Arrangement.spacedBy(dimens.space12),
        ) {
            QuickAccessTile(
                label = stringResource(R.string.quick_access_reports),
                icon = ProIconGlyph.FeatReports,
                onClick = onReportsClick,
                modifier = Modifier.weight(1f),
            )
            QuickAccessTile(
                label = stringResource(R.string.quick_access_debt),
                icon = ProIconGlyph.FeatDebt,
                onClick = onDebtClick,
                modifier = Modifier.weight(1f),
            )
            QuickAccessTile(
                label = stringResource(R.string.quick_access_split),
                icon = ProIconGlyph.FeatSplit,
                onClick = onSplitClick,
                modifier = Modifier.weight(1f),
            )
            QuickAccessTile(
                label = stringResource(R.string.quick_access_events),
                icon = ProIconGlyph.FeatEvents,
                onClick = onEventsClick,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun HomeEmptyContent(
    onLogFirstExpense: () -> Unit,
    modifier: Modifier = Modifier,
) {
    EmptyStateContent(
        title = stringResource(R.string.home_empty_title),
        subtitle = stringResource(R.string.home_empty_subtitle),
        actionLabel = stringResource(R.string.home_log_first_expense),
        onActionClick = onLogFirstExpense,
        addHintPrefix = stringResource(R.string.home_empty_tap_add_prefix),
        addHintSuffix = stringResource(R.string.home_empty_tap_add_suffix),
        modifier = modifier,
    )
}

@Composable
private fun HomeRecentSection(
    groups: List<com.arduia.expense.ui.preview.HomeDayGroup>,
    onSeeAll: () -> Unit,
    onTransactionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimens.space10),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.recent_section),
                style = typography.eyebrow,
                color = colors.onSurface,
            )
            ProTextAction(
                text = stringResource(R.string.see_all),
                onClick = onSeeAll,
                style = typography.caption.copy(fontWeight = FontWeight.SemiBold),
                color = colors.primary,
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(bottom = dimens.navShellBottomInset),
            verticalArrangement = Arrangement.spacedBy(dimens.space12),
        ) {
            items(groups, key = { it.dayTitle }) { group ->
                DayGroup(
                    title = group.dayTitle,
                    total = group.dayTotal,
                    transactions = group.transactions.mapIndexed { index, item ->
                        ProTransactionRowModel(
                            id = item.id.ifBlank { "${group.dayTitle}_$index" },
                            categoryId = item.categoryId,
                            note = item.note,
                            meta = item.meta,
                            amount = item.amount,
                            tag = item.tag,
                        )
                    },
                    cardWrapped = false,
                    onRowClick = { model -> onTransactionClick(model.id) },
                )
            }
        }
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

@Preview(
    name = "Home — casual with PIN banner",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun HomeCasualWithPinBannerPreview() {
    ProExpenseTheme {
        HomeScreenContent(
            state = previewHomeCasual,
            onReportsClick = {},
            onDebtClick = {},
            onSplitClick = {},
            onEventsClick = {},
            showPinSetupBanner = true,
        )
    }
}
