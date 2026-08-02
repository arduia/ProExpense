package com.arduia.expense.feature.reports.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arduia.expense.feature.reports.R
import com.arduia.expense.feature.reports.ui.preview.ReportsCategoryUi
import com.arduia.expense.feature.reports.ui.preview.ReportsUiState
import com.arduia.expense.feature.reports.ui.preview.previewReports
import com.arduia.expense.feature.reports.ui.preview.previewReportsEmpty
import com.arduia.expense.feature.reports.ui.preview.previewReportsPeriodEmpty
import com.arduia.expense.feature.reports.ui.preview.previewReportsUncategorized
import com.arduia.expense.feature.reports.ui.preview.previewReportsWithOtherRollup
import com.arduia.expense.ui.design.EmptyStateContent
import com.arduia.expense.ui.design.ProFlatHeader
import com.arduia.expense.ui.design.ProIcon
import com.arduia.expense.ui.design.ProIconGlyph
import com.arduia.expense.ui.design.proCardShadow
import com.arduia.expense.ui.design.proIconClickable
import com.arduia.expense.ui.theme.ProArtboard
import com.arduia.expense.ui.theme.ProExpenseTheme

@Composable
fun ReportsScreen(
    state: ReportsUiState,
    onBack: () -> Unit,
    onPrevPeriod: () -> Unit,
    onNextPeriod: () -> Unit,
    modifier: Modifier = Modifier,
    globalEmpty: Boolean = state.empty,
    onLogFirstExpense: () -> Unit = {},
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.paper)
                .statusBarsPadding()
                .navigationBarsPadding(),
    ) {
        ProFlatHeader(
            title = stringResource(R.string.reports_title),
            eyebrow = stringResource(R.string.reports_eyebrow),
            onBack = onBack,
            backContentDescription = stringResource(R.string.reports_back),
            modifier =
                Modifier
                    .padding(horizontal = dimens.screenPadding)
                    .padding(vertical = dimens.space14),
        )

        // Global empty (never logged anything, ever) has no periods to switch between, so the
        // month pill is meaningless here — this is the only case that hides it (US-REP-3 Scenario
        // 1). A per-period empty month (state.empty while globalEmpty is false, e.g. after
        // swiping to a month with no spending) must still show the pill/chevrons below, or the
        // user gets stranded with no way back to a month that has data.
        if (globalEmpty) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = dimens.screenPadding),
                contentAlignment = Alignment.Center,
            ) {
                EmptyStateContent(
                    title = stringResource(R.string.reports_empty_title),
                    subtitle = stringResource(R.string.reports_empty_subtitle),
                    actionLabel = stringResource(R.string.reports_empty_action),
                    onActionClick = onLogFirstExpense,
                )
            }
            return@Column
        }

        // The pill is a fixed header, not part of the swipeable body below — it must stay put
        // while only the content underneath changes, so switching periods never reads like
        // navigating to a new screen.
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimens.screenPadding)
                    .padding(top = dimens.space8),
            contentAlignment = Alignment.Center,
        ) {
            ReportsPeriodPill(
                label = state.periodLabel,
                onPrev = onPrevPeriod,
                onNext = onNextPeriod,
            )
        }

        ReportsPeriodContent(
            state = state,
            onLogFirstExpense = onLogFirstExpense,
        )
    }
}

@Composable
internal fun ReportsPeriodContent(
    state: ReportsUiState,
    onLogFirstExpense: () -> Unit,
    modifier: Modifier = Modifier,
    allPeriodsEmpty: Boolean = false,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimens.screenPadding)
                .padding(top = dimens.space16, bottom = dimens.space24),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimens.space16),
    ) {
        if (state.empty) {
            // "Swipe/use the arrows" is useless advice when every period in this granularity's
            // window is empty — nudge toward the granularity toggle instead, since that's the
            // only control that can actually surface data at that point.
            EmptyStateContent(
                title =
                    stringResource(
                        if (allPeriodsEmpty) R.string.reports_window_empty_title else R.string.reports_period_empty_title,
                    ),
                subtitle =
                    stringResource(
                        if (allPeriodsEmpty) R.string.reports_window_empty_subtitle else R.string.reports_period_empty_subtitle,
                    ),
                actionLabel = stringResource(R.string.reports_period_empty_action),
                onActionClick = onLogFirstExpense,
                modifier = Modifier.padding(vertical = dimens.space24),
            )
            return@Column
        }

        if (state.uncategorized) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.reports_period_total, state.periodLabel),
                    style = typography.eyebrow,
                    color = colors.onSurfaceMuted,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = state.totalLabel,
                    style = typography.summaryAmount,
                    color = colors.onSurface,
                    modifier = Modifier.padding(top = dimens.space8),
                )
            }
            ReportsDonut(
                categories = state.categories,
                uncategorized = true,
                modifier = Modifier.padding(vertical = dimens.space8),
            )
            ReportsTipBanner()
        } else {
            ReportsDonutLegendCard(
                categories = state.categories,
                totalLabel = state.totalLabel,
            )
            Column(verticalArrangement = Arrangement.spacedBy(dimens.space10)) {
                Text(
                    text = stringResource(R.string.reports_top_categories),
                    style = typography.eyebrow,
                    color = colors.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                )
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clip(ProExpenseTheme.shapes.card)
                            .border(BorderStroke(1.dp, colors.line), ProExpenseTheme.shapes.card)
                            .background(colors.surface),
                ) {
                    state.categories.forEachIndexed { index, category ->
                        if (index > 0) {
                            Box(Modifier.fillMaxWidth().height(1.dp).background(colors.lineSoft))
                        }
                        ReportsRankRow(category = category)
                    }
                }
            }
        }
    }
}

/**
 * [label]'s period moves further into the past on [onPrev] (earlier) and back toward the
 * present on [onNext] (later) — standard calendar-navigator direction, independent of how the
 * underlying period list happens to be ordered.
 */
@Composable
internal fun ReportsPeriodPill(
    label: String,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    prevEnabled: Boolean = true,
    nextEnabled: Boolean = true,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier =
            Modifier
                .clip(ProExpenseTheme.shapes.chip)
                .border(BorderStroke(1.dp, colors.line), ProExpenseTheme.shapes.chip)
                .background(colors.surface)
                .padding(horizontal = dimens.space8, vertical = dimens.space4),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        ProIcon(
            // Same chevron as the "next" icon below, mirrored — a back-arrow glyph here read as
            // a different, unrelated affordance instead of the pair's matching "previous".
            glyph = ProIconGlyph.ChevronRight,
            contentDescription = stringResource(R.string.reports_prev_period),
            tint = if (prevEnabled) colors.onSurfaceMuted else colors.muted2,
            size = dimens.iconInline,
            modifier =
                Modifier
                    .rotate(180f)
                    .proIconClickable(onClick = onPrev, enabled = prevEnabled),
        )
        Text(
            text = label,
            style = typography.bodySemiBold,
            color = colors.onSurface,
            modifier = Modifier.padding(vertical = dimens.space4),
        )
        ProIcon(
            glyph = ProIconGlyph.ChevronRight,
            contentDescription = stringResource(R.string.reports_next_period),
            tint = if (nextEnabled) colors.onSurfaceMuted else colors.muted2,
            size = dimens.iconInline,
            modifier = Modifier.proIconClickable(onClick = onNext, enabled = nextEnabled),
        )
    }
}

/**
 * Combined donut + per-category legend card — replaces a separate "total spent" hero and a
 * standalone donut, matching the canvas's single card with the total inside the ring and the
 * legend beside it.
 */
@Composable
private fun ReportsDonutLegendCard(
    categories: List<ReportsCategoryUi>,
    totalLabel: String,
    modifier: Modifier = Modifier,
) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val cardShape = ProExpenseTheme.shapes.card

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .proCardShadow(cardShape)
                .clip(cardShape)
                .border(BorderStroke(1.dp, colors.line), cardShape)
                .background(colors.surface)
                .padding(dimens.space18),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space20),
    ) {
        ReportsDonut(
            categories = categories,
            uncategorized = false,
            centerLabel = totalLabel,
            size = ReportsDonutLegendSize,
            strokeWidth = ReportsDonutLegendStroke,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(dimens.space8),
        ) {
            categories.forEach { category ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimens.space7),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(dimens.space8)
                                .clip(CircleShape)
                                .background(colors.category(category.categoryId).accent),
                    )
                    Text(
                        text = category.label,
                        style = typography.captionMedium,
                        color = colors.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = category.percentLabel,
                        style = typography.monoFigure,
                        color = colors.onSurfaceMuted,
                    )
                }
            }
        }
    }
}

private val ReportsDonutLegendSize = 128.dp
private val ReportsDonutLegendStroke = 14.dp

@Composable
private fun ReportsDonut(
    categories: List<ReportsCategoryUi>,
    uncategorized: Boolean,
    modifier: Modifier = Modifier,
    // Non-null for the categorized donut+legend card, which shows the period total inside the
    // ring instead of the category count — the uncategorized caller leaves this null and keeps
    // its own "100% Uncategorized" center content.
    centerLabel: String? = null,
    size: Dp = 160.dp,
    strokeWidth: Dp = 22.dp,
) {
    val colors = ProExpenseTheme.colors
    val typography = ProExpenseTheme.typography
    val segments =
        if (uncategorized) {
            listOf(1f to colors.lineStrong)
        } else {
            categories.map { it.fraction to colors.category(it.categoryId).accent }
        }
    val gapDegrees = if (uncategorized) 0f else 4f

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = strokeWidth.toPx()
            val inset = stroke / 2f
            val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
            var startAngle = -90f + gapDegrees / 2f
            segments.forEach { (fraction, color) ->
                val sweep = fraction * 360f - gapDegrees
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweep.coerceAtLeast(0f),
                    useCenter = false,
                    topLeft =
                        androidx.compose.ui.geometry
                            .Offset(inset, inset),
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
                startAngle += fraction * 360f
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (uncategorized) {
                Text(text = "100%", style = typography.bodySemiBold, color = colors.onSurfaceMuted)
                Text(
                    text = stringResource(R.string.reports_uncategorized_label),
                    style = typography.caption,
                    color = colors.onSurfaceMuted,
                )
            } else if (centerLabel != null) {
                Text(text = centerLabel, style = typography.sectionHead, color = colors.onSurface)
                Text(
                    text = stringResource(R.string.reports_total_spent),
                    style = typography.caption,
                    color = colors.onSurfaceMuted,
                )
            } else {
                Text(
                    text = stringResource(R.string.reports_by_category),
                    style = typography.eyebrow,
                    color = colors.onSurfaceMuted,
                )
                Text(
                    text = categories.size.toString(),
                    style = typography.summaryAmount,
                    color = colors.onSurface,
                )
            }
        }
    }
}

@Composable
private fun ReportsRankRow(category: ReportsCategoryUi) {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography
    val categoryColor = colors.category(category.categoryId)

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.space14, vertical = dimens.space12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space10),
    ) {
        Box(
            modifier =
                Modifier
                    .size(dimens.space32)
                    .clip(ProExpenseTheme.shapes.tile)
                    .background(categoryColor.tint),
            contentAlignment = Alignment.Center,
        ) {
            ProIcon(
                glyph = ProIconGlyph.FeatReports,
                contentDescription = null,
                tint = categoryColor.accent,
                size = dimens.iconInline,
            )
        }
        Text(
            text = category.label,
            style = typography.bodySemiBold,
            color = colors.onSurface,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = category.amountLabel,
            style = typography.listAmount,
            color = colors.onSurface,
        )
    }
}

@Composable
private fun ReportsTipBanner() {
    val colors = ProExpenseTheme.colors
    val dimens = ProExpenseTheme.dimensions
    val typography = ProExpenseTheme.typography

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(ProExpenseTheme.shapes.card)
                .background(colors.primaryTint)
                .padding(dimens.space14),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        ProIcon(
            glyph = ProIconGlyph.Sparkle,
            contentDescription = null,
            tint = colors.primaryDeep,
            size = dimens.iconNav,
        )
        Text(
            text = stringResource(R.string.reports_uncategorized_tip),
            style = typography.bodyMedium,
            color = colors.primaryDeep,
        )
    }
}

@Preview(
    name = "Reports — monthly",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun ReportsPreview() {
    ProExpenseTheme {
        ReportsScreen(previewReports, {}, {}, {})
    }
}

@Preview(
    name = "Reports — monthly (dark)",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun ReportsDarkPreview() {
    ProExpenseTheme(darkTheme = true) {
        ReportsScreen(previewReports, {}, {}, {})
    }
}

@Preview(
    name = "Reports — other rollup",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun ReportsOtherRollupPreview() {
    ProExpenseTheme {
        ReportsScreen(previewReportsWithOtherRollup, {}, {}, {})
    }
}

@Preview(
    name = "Reports — all uncategorized",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun ReportsUncategorizedPreview() {
    ProExpenseTheme {
        ReportsScreen(previewReportsUncategorized, {}, {}, {})
    }
}

@Preview(
    name = "Reports — no data yet",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun ReportsEmptyPreview() {
    ProExpenseTheme {
        ReportsScreen(previewReportsEmpty, {}, {}, {})
    }
}

@Preview(
    name = "Reports — empty month (pill stays visible)",
    widthDp = ProArtboard.PIXEL_9_PRO_WIDTH_DP,
    heightDp = ProArtboard.PIXEL_9_PRO_HEIGHT_DP,
    showBackground = true,
)
@Composable
private fun ReportsPeriodEmptyPreview() {
    ProExpenseTheme {
        ReportsScreen(
            state = previewReportsPeriodEmpty,
            onBack = {},
            onPrevPeriod = {},
            onNextPeriod = {},
            globalEmpty = false,
        )
    }
}
